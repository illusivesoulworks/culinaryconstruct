/*
 * Copyright (c) 2018-2020 C4
 *
 * This file is part of Culinary Construct, a mod made for Minecraft.
 *
 * Culinary Construct is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Culinary Construct is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Culinary Construct.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.theillusivec4.culinaryconstruct.common.advancement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.common.advancement.CraftFoodTrigger.Instance;

public class CraftFoodTrigger extends AbstractCriterionTrigger<Instance> {

  public static final CraftFoodTrigger INSTANCE = new CraftFoodTrigger();
  public static final ResourceLocation ID = new ResourceLocation(CulinaryConstruct.MODID, "craft_food");

  @Nonnull
  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Nonnull
  @Override
  public Instance deserializeTrigger(@Nonnull JsonObject p_230241_1_,
      @Nonnull EntityPredicate.AndPredicate p_230241_2_,
      @Nonnull ConditionArrayParser p_230241_3_) {
    return new Instance(p_230241_2_);
  }

  public void trigger(ServerPlayerEntity player) {
    this.triggerListeners(player, (p_241523_0_) -> true);
  }

  public static class Instance extends CriterionInstance {

    public Instance(EntityPredicate.AndPredicate p_i232007_1_) {
      super(ID, p_i232007_1_);
    }
  }
}
