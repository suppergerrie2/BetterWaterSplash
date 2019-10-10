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

@Mod(BetterWaterSplash.MOD_ID)
@Mod.EventBusSubscriber(modid = BetterWaterSplash.MOD_ID)
public class BetterWaterSplash {

    public static final String MOD_ID = "sbetterwatersplash";

    @SubscribeEvent
    public static void onProjectileImpactEvent(ProjectileImpactEvent.Throwable event) {
        //We only want to know when a potion impacts something, not arrows or other entities.
        if (event.getThrowable().world.isRemote || !(event.getThrowable() instanceof PotionEntity)) {
            return;
        }

        //To make sure it is a water potion we need to check if it is the WATER potion, or if it has no effects at all.
        PotionEntity potionEntity = (PotionEntity) event.getThrowable();

        ItemStack potionItemstack = potionEntity.getItem();

        //Read the potion type and potion effects from the potion ItemStack
        Potion potionType = PotionUtils.getPotionFromItem(potionItemstack);
        List<EffectInstance> potionEffects = PotionUtils.getEffectsFromStack(potionItemstack);

        boolean isWaterPotion = (potionType == Potions.WATER && potionEffects.isEmpty());

        //If it isn't a water potion we shouldn't put out the fire.
        if (!isWaterPotion) {
            return;
        }

        //Get the effect radius. TODO: Make distance a config setting?
        AxisAlignedBB axisalignedbb = potionEntity.getBoundingBox().grow(4.0D, 2.0D, 4.0D);

        //Get all entities in the potion's effect radius
        List<LivingEntity> entitiesWithinRange = potionEntity.world.<LivingEntity>getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);

        //Now we need to check if the entities are in a sphere with a radius of 4, and if they are extinguish them.
        if (!entitiesWithinRange.isEmpty()) {
            for (LivingEntity entity : entitiesWithinRange) {
                double distanceSq = potionEntity.getDistanceSq(entity);

                if (distanceSq < 16.0D) {
                    entity.extinguish();
                }
            }
        }
    }
}