package com.suppergerrie2.betterwatersplash;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.*;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod(BetterWaterSplash.MOD_ID)
@Mod.EventBusSubscriber(modid = BetterWaterSplash.MOD_ID)
public class BetterWaterSplash {

    public static final String MOD_ID = "sbetterwatersplash";

    @SubscribeEvent
    public static void onProjectileImpactEvent(ProjectileImpactEvent event) {
        //We only want to know when a potion impacts something, not arrows or other entities.
        if (event.getProjectile().level.isClientSide() || !(event.getProjectile() instanceof ThrownPotion potionEntity)) {
            return;
        }

        //To make sure it is a water potion we need to check if it is the WATER potion, or if it has no effects at all.
        ItemStack potionItemstack = potionEntity.getItem();

        //Read the potion type and potion effects from the potion ItemStack
        Potion                  potionType    = PotionUtils.getPotion(potionItemstack);
        List<MobEffectInstance> potionEffects = PotionUtils.getCustomEffects(potionItemstack);

        boolean isWaterPotion = (potionType == Potions.WATER && potionEffects.isEmpty());

        //If it isn't a water potion we shouldn't put out the fire.
        if (!isWaterPotion) {
            return;
        }

        //Get the effect radius. TODO: Make distance a config setting?
        AABB axisalignedbb = potionEntity.getBoundingBox()
                                         .inflate(4.0D, 2.0D, 4.0D);

        //Get all entities in the potion's effect radius
        List<LivingEntity> entitiesWithinRange = potionEntity.level.getEntitiesOfClass(LivingEntity.class,
                                                                                       axisalignedbb);

        //Now we need to check if the entities are in a sphere with a radius of 4, and if they are extinguish them.
        if (!entitiesWithinRange.isEmpty()) {
            for (LivingEntity entity : entitiesWithinRange) {
                double distanceSq = potionEntity.distanceToSqr(entity);

                if (distanceSq < 16.0D) {
                    entity.clearFire();
                }
            }
        }
    }
}
