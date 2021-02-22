package top.theillusivec4.culinaryconstruct.common.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.InterModComms;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.theillusivec4.culinaryconstruct.common.registry.CulinaryConstructRegistry;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class DietIntegration {

  public static void setup() {
    sendCulinaryMessage(CulinaryConstructRegistry.SANDWICH);
    sendCulinaryMessage(CulinaryConstructRegistry.FOOD_BOWL);
  }

  private static void sendCulinaryMessage(Item item) {
    InterModComms.sendTo("diet", "item",
        () -> new Tuple<Item, BiFunction<PlayerEntity, ItemStack, Triple<List<ItemStack>, Integer, Float>>>(
            item, (player, stack) -> new ImmutableTriple<>(getIngredients(stack),
            CulinaryNBTHelper.getFoodAmount(stack), CulinaryNBTHelper.getSaturation(stack))));
  }

  private static List<ItemStack> getIngredients(ItemStack stack) {
    List<ItemStack> result = new ArrayList<>(CulinaryNBTHelper.getIngredientsList(stack));
    result.add(CulinaryNBTHelper.getBase(stack));
    return result;
  }
}
