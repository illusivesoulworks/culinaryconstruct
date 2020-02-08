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
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.common.advancement.CraftFoodTrigger.Instance;

public class CraftFoodTrigger implements ICriterionTrigger<Instance> {
  public static final ResourceLocation ID = new ResourceLocation(CulinaryConstruct.MODID, "craft_food");
  private final Map<PlayerAdvancements, CraftFoodTrigger.Listeners> listeners = Maps
      .newHashMap();

  @Nonnull
  @Override
  public ResourceLocation getId() {
    return ID;
  }

  @Override
  public void addListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull ICriterionTrigger.Listener<CraftFoodTrigger.Instance> listener) {
    CraftFoodTrigger.Listeners foodListeners = this.listeners.get(playerAdvancementsIn);
    if (foodListeners == null) {
      foodListeners = new CraftFoodTrigger.Listeners(playerAdvancementsIn);
      this.listeners.put(playerAdvancementsIn, foodListeners);
    }
    foodListeners.add(listener);
  }

  @Override
  public void removeListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull ICriterionTrigger.Listener<CraftFoodTrigger.Instance> listener) {
    CraftFoodTrigger.Listeners foodListeners = this.listeners.get(playerAdvancementsIn);
    if (foodListeners != null) {
      foodListeners.remove(listener);
      if (foodListeners.isEmpty()) {
        this.listeners.remove(playerAdvancementsIn);
      }
    }

  }

  public void removeAllListeners(@Nonnull PlayerAdvancements playerAdvancementsIn) {
    this.listeners.remove(playerAdvancementsIn);
  }

  @Nonnull
  @Override
  public CraftFoodTrigger.Instance deserializeInstance(
      @Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
    return new CraftFoodTrigger.Instance();
  }

  public void trigger(ServerPlayerEntity player) {
    CraftFoodTrigger.Listeners foodListeners = this.listeners.get(player.getAdvancements());
    if (foodListeners != null) {
      foodListeners.trigger();
    }
  }

  public static class Instance extends CriterionInstance {
    public Instance() {
      super(CraftFoodTrigger.ID);
    }
  }

  static class Listeners {
    private final PlayerAdvancements playerAdvancements;
    private final Set<Listener<CraftFoodTrigger.Instance>> listeners = Sets
        .newHashSet();

    public Listeners(PlayerAdvancements playerAdvancementsIn) {
      this.playerAdvancements = playerAdvancementsIn;
    }

    public boolean isEmpty() {
      return this.listeners.isEmpty();
    }

    public void add(ICriterionTrigger.Listener<CraftFoodTrigger.Instance> listener) {
      this.listeners.add(listener);
    }

    public void remove(ICriterionTrigger.Listener<CraftFoodTrigger.Instance> listener) {
      this.listeners.remove(listener);
    }

    public void trigger() {
      for(ICriterionTrigger.Listener<CraftFoodTrigger.Instance> listener : Lists
          .newArrayList(this.listeners)) {
        listener.grantCriterion(this.playerAdvancements);
      }
    }
  }
}
