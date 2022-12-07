/*
 * Copyright (C) 2018-2022 Illusive Soulworks
 *
 * Culinary Construct is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Culinary Construct is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Culinary Construct.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.culinaryconstruct.mixin.core;

import com.illusivesoulworks.culinaryconstruct.mixin.CulinaryConstructMixinHooks;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class CulinaryConstructWolfMixin {

  @SuppressWarnings("ConstantConditions")
  @Inject(at = @At("HEAD"), method = "isFood", cancellable = true)
  private void culinaryconstruct$isFood(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
    CulinaryConstructMixinHooks.isFood(stack, (Wolf) (Object) this).ifPresent(cir::setReturnValue);
  }
}
