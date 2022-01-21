package top.theillusivec4.culinaryconstruct.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import javax.annotation.Nonnull;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

public enum FoodBowlLoader implements IModelLoader<FoodBowlModel> {

  INSTANCE;

  @Override
  public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {

  }

  @Nonnull
  @Override
  public FoodBowlModel read(@Nonnull JsonDeserializationContext deserializationContext,
      @Nonnull JsonObject modelContents) {
    return new FoodBowlModel();
  }
}
