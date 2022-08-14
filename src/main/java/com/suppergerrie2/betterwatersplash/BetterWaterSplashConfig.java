package com.suppergerrie2.betterwatersplash;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class BetterWaterSplashConfig {
    static final ForgeConfigSpec serverSpec;
    public static final ServerConfig SERVER_CONFIG;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        serverSpec = serverPair.getRight();
        SERVER_CONFIG = serverPair.getLeft();
    }

    public static class ServerConfig {
        private final ForgeConfigSpec.ConfigValue<List<? extends String>> listedDimensionsSpec;
        private final ForgeConfigSpec.BooleanValue isWhitelist;
        private HashSet<ResourceKey<Level>> listedDimensions;

        ServerConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings").push("server");
            this.listedDimensionsSpec = builder
                    .comment("A blacklist of dimensions in which splash water bottles extinguish entities",
                            "Example: [\"minecraft:nether\", \"suppergerrie2:super_cool_dimension\"]",
                            "Default value: []")
                    .defineList("blacklistedDimensions", Collections.emptyList(), o -> o instanceof String);
            this.isWhitelist = builder
                    .comment("If TRUE, the blacklist will be treated as a whitelist instead",
                            "Default value: false")
                    .define("isWhitelist", false);
        }

        public void onLoadConfig() {
            listedDimensions = new HashSet<>();
            for (String dimensionName : listedDimensionsSpec.get()) {
                listedDimensions.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimensionName)));
            }
        }

        public boolean isDimensionAllowed(ResourceKey<Level> dimension) {
            return listedDimensions.contains(dimension) == isWhitelist.get();
        }
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent event) {
        if (!event.getConfig().getModId().equals(BetterWaterSplash.MOD_ID)) return;
        SERVER_CONFIG.onLoadConfig();
    }
}
