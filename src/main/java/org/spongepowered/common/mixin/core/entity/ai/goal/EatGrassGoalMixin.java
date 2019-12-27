/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.core.entity.ai.goal;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.Goal;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.common.bridge.entity.GrieferBridge;

@Mixin(EatGrassGoal.class)
public abstract class EatGrassGoalMixin extends Goal {

    @Shadow @Final private MobEntity grassEaterEntity;

    /**
     * @author gabizou - April 13th, 2018
     * @reason - Due to Forge's changes, there's no clear redirect or injection
     * point where Sponge can add the griefer checks. The original redirect aimed
     * at the gamerule check, but this can suffice for now.
     */
    @Redirect(
        method = "updateTask",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/base/Predicate;apply(Ljava/lang/Object;)Z",
            remap = false
        )
    )
    @SuppressWarnings({"unchecked", "rawtypes", "Guava"})
    private boolean onTallGrassApplyForGriefing(final Predicate predicate, final Object object) {
        return ((GrieferBridge) this.grassEaterEntity).bridge$canGrief() && predicate.apply(object);
    }

    /**
     * @author gabizou - April 13th, 2018
     * @reason - Due to Forge's changes, there's no clear redirect or injection
     * point where Sponge can add the griefer checks. The original redirect aimed
     * at the gamerule check, but this can suffice for now.
     */
    @Redirect(
        method = "updateTask",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
            ),
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/init/Blocks;GRASS:Lnet/minecraft/block/BlockGrass;",
                opcode = Opcodes.GETSTATIC
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/state/IBlockState;getBlock()Lnet/minecraft/block/Block;"
        )
    )
    private Block onSpongeGetBlockForgriefing(final BlockState state) {
        return ((GrieferBridge) this.grassEaterEntity).bridge$canGrief() ? state.getBlock() : Blocks.AIR;
    }
}