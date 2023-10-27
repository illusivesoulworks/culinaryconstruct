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

package com.illusivesoulworks.culinaryconstruct.common.advancement;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class CraftFoodTrigger extends SimpleCriterionTrigger<CraftFoodTrigger.Instance> {

  public static final CraftFoodTrigger INSTANCE = new CraftFoodTrigger();

  @Nonnull
  @Override
  protected Instance createInstance(@Nonnull JsonObject pJson,
                                    @Nonnull Optional<ContextAwarePredicate> p_297533_,
                                    @Nonnull DeserializationContext pDeserializationContext) {
    return new Instance(p_297533_);
  }

  public void trigger(ServerPlayer player) {
    this.trigger(player, (instance) -> true);
  }

  public static class Instance extends AbstractCriterionTriggerInstance {

    public Instance(Optional<ContextAwarePredicate> player) {
      super(player);
    }
  }
}
