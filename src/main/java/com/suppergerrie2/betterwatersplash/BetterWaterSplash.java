package com.suppergerrie2.betterwatersplash;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(
    BetterWaterSplash.MOD_ID
)
@Mod.EventBusSubscriber(modid = BetterWaterSplash.MOD_ID)
public class BetterWaterSplash {

    public static final String MOD_ID = "sbetterwatersplash";

    @SubscribeEvent
    public static void onProjectileImpactEvent(ProjectileImpactEvent.Throwable event) {
        if (event.getThrowable().world.isRemote || !(event.getThrowable() instanceof PotionEntity)) {
            return;
        }

        PotionEntity potionEntity = (PotionEntity) event.getThrowable();

        ItemStack potionItemstack = potionEntity.getItem();
        Potion potiontype = PotionUtils.getPotionFromItem(potionItemstack);
        List<EffectInstance> potionEffects = PotionUtils.getEffectsFromStack(potionItemstack);
        boolean isWaterPotion = potiontype == Potions.WATER && potionEffects.isEmpty();

        if (!isWaterPotion) {
            return;
        }

        AxisAlignedBB axisalignedbb = potionEntity.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List<LivingEntity> entitiesWithinRange = potionEntity.world.<LivingEntity>getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);

        if (!entitiesWithinRange.isEmpty()) {
            for (LivingEntity entitylivingbase : entitiesWithinRange) {
                double distanceSq = potionEntity.getDistanceSq(entitylivingbase);

                if (distanceSq < 16.0D) {
                    entitylivingbase.extinguish();
                }
            }
        }
    }

}
