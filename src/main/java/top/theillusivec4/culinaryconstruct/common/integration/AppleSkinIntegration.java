package top.theillusivec4.culinaryconstruct.common.integration;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.food.FoodValues;
import top.theillusivec4.culinaryconstruct.common.item.CulinaryItemBase;
import top.theillusivec4.culinaryconstruct.common.util.CulinaryNBTHelper;

public class AppleSkinIntegration {

  public static void setup() {
    MinecraftForge.EVENT_BUS.addListener(AppleSkinIntegration::onFoodValues);
  }

  private static void onFoodValues(final FoodValuesEvent evt) {

    if (evt.itemStack.getItem() instanceof CulinaryItemBase) {
      ItemStack stack = evt.itemStack;
      evt.defaultFoodValues = new FoodValues(CulinaryNBTHelper.getFoodAmount(stack),
          CulinaryNBTHelper.getSaturation(stack));
      evt.modifiedFoodValues = evt.defaultFoodValues;
    }
  }
}
