package com.suppergerrie2.betterwatersplash;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
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
        if (event.getThrowable().world.isRemote || !(event.getThrowable() instanceof EntityPotion)) {
            return;
        }

        EntityPotion potionEntity = (EntityPotion) event.getThrowable();

        ItemStack potionItemstack = potionEntity.getPotion();
        PotionType potiontype = PotionUtils.getPotionFromItem(potionItemstack);
        List<PotionEffect> potionEffects = PotionUtils.getEffectsFromStack(potionItemstack);
        boolean isWaterPotion = potiontype == PotionTypes.WATER && potionEffects.isEmpty();

        if (!isWaterPotion) {
            return;
        }

        AxisAlignedBB axisalignedbb = potionEntity.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List<EntityLivingBase> entitiesWithinRange = potionEntity.world.<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

        if (!entitiesWithinRange.isEmpty()) {
            for (EntityLivingBase entitylivingbase : entitiesWithinRange) {
                double distanceSq = potionEntity.getDistanceSq(entitylivingbase);

                if (distanceSq < 16.0D) {
                    entitylivingbase.extinguish();
                }
            }
        }
    }

}
