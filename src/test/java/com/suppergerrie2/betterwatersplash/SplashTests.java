package com.suppergerrie2.betterwatersplash;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertPosException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@SuppressWarnings("unused")
@GameTestHolder(BetterWaterSplash.MOD_ID)
public class SplashTests {

    @GameTest(template = "splash_water_extinguishes_fire")
    @PrefixGameTestTemplate(false)
    public static void splashWaterExtinguishesFire(GameTestHelper gameTestHelper) {
        testSplashWaterExtinguishesFire(gameTestHelper);
    }

    @GameTest(template = "lingering_water_extinguishes_fire")
    @PrefixGameTestTemplate(false)
    public static void lingeringWaterExtinguishesFire(GameTestHelper gameTestHelper) {
        testSplashWaterExtinguishesFire(gameTestHelper);
    }

    @GameTest(template = "normal_potion_does_not_extinguish_fire")
    @PrefixGameTestTemplate(false)
    public static void potionWithEffectDoesNotExtinguishFire(GameTestHelper gameTestHelper) {
        Pig pig = gameTestHelper.spawn(EntityType.PIG, 1.5f, 2, 1.5f);

        gameTestHelper.setBlock(1, 2, 1, Blocks.FIRE);
        gameTestHelper.setBlock(1, 2, 2, Blocks.FIRE);
        gameTestHelper.setBlock(2, 2, 1, Blocks.FIRE);
        gameTestHelper.setBlock(2, 2, 2, Blocks.FIRE);

        gameTestHelper.runAfterDelay(10, () -> {
            gameTestHelper.setBlock(1, 2, 1, Blocks.AIR);
            gameTestHelper.setBlock(1, 2, 2, Blocks.AIR);
            gameTestHelper.setBlock(2, 2, 1, Blocks.AIR);
            gameTestHelper.setBlock(2, 2, 2, Blocks.AIR);

            if (!pig.isOnFire()) {
                throw new GameTestAssertPosException("Pig should be on fire", pig.blockPosition(), gameTestHelper.relativePos(pig.blockPosition()), gameTestHelper.getTick());
            }

            gameTestHelper.pulseRedstone(new BlockPos(1, 4, 2), 5);

            gameTestHelper.runAfterDelay(10, () -> {
                if (!pig.isOnFire()) {
                    throw new GameTestAssertPosException("Pig should still be on fire", pig.blockPosition(), gameTestHelper.relativePos(pig.blockPosition()), gameTestHelper.getTick());
                }

                gameTestHelper.succeed();
            });
        });
    }

    public static void testSplashWaterExtinguishesFire(GameTestHelper gameTestHelper) {
        Pig pig = gameTestHelper.spawn(EntityType.PIG, 1.5f, 2, 1.5f);

        gameTestHelper.setBlock(1, 2, 1, Blocks.FIRE);
        gameTestHelper.setBlock(1, 2, 2, Blocks.FIRE);
        gameTestHelper.setBlock(2, 2, 1, Blocks.FIRE);
        gameTestHelper.setBlock(2, 2, 2, Blocks.FIRE);

        gameTestHelper.runAfterDelay(10, () -> {
            gameTestHelper.setBlock(1, 2, 1, Blocks.AIR);
            gameTestHelper.setBlock(1, 2, 2, Blocks.AIR);
            gameTestHelper.setBlock(2, 2, 1, Blocks.AIR);
            gameTestHelper.setBlock(2, 2, 2, Blocks.AIR);

            if (!pig.isOnFire()) {
                throw new GameTestAssertPosException("Pig should be on fire", pig.blockPosition(), gameTestHelper.relativePos(pig.blockPosition()), gameTestHelper.getTick());
            }

            gameTestHelper.pulseRedstone(new BlockPos(1, 4, 2), 5);

            gameTestHelper.runAfterDelay(10, () -> {
                if (pig.isOnFire()) {
                    throw new GameTestAssertPosException("Pig should not be on fire", pig.blockPosition(), gameTestHelper.relativePos(pig.blockPosition()), gameTestHelper.getTick());
                }

                gameTestHelper.succeed();
            });
        });
    }

    @GameTest(template = "splash_has_range")
    @PrefixGameTestTemplate(false)
    public static void splashHasRange(GameTestHelper gameTestHelper) {
        Pig pig = gameTestHelper.spawn(EntityType.PIG, 1.5f, 2, 1.5f);

        BlockPos.betweenClosed(gameTestHelper.absolutePos(new BlockPos(1, 2, 1)), gameTestHelper.absolutePos(new BlockPos(14, 2, 2)))
                .forEach(p -> gameTestHelper.getLevel().setBlock(p, Blocks.FIRE.defaultBlockState(), 3));

        gameTestHelper.runAfterDelay(10, () -> {
            BlockPos.betweenClosed(gameTestHelper.absolutePos(new BlockPos(1, 2, 1)), gameTestHelper.absolutePos(new BlockPos(14, 2, 2)))
                    .forEach(p -> gameTestHelper.getLevel().setBlock(p, Blocks.AIR.defaultBlockState(), 3));


            if (!pig.isOnFire()) {
                throw new GameTestAssertPosException("Pig should be on fire", pig.blockPosition(), gameTestHelper.relativePos(pig.blockPosition()), gameTestHelper.getTick());
            }

            gameTestHelper.pulseRedstone(new BlockPos(13, 4, 2), 5);

            gameTestHelper.runAfterDelay(10, () -> {
                if (!pig.isOnFire()) {
                    throw new GameTestAssertPosException("Pig should still be on fire", pig.blockPosition(), gameTestHelper.relativePos(pig.blockPosition()), gameTestHelper.getTick());
                }

                gameTestHelper.succeed();
            });
        });
    }

}
