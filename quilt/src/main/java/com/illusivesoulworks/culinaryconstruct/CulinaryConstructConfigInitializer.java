package com.illusivesoulworks.culinaryconstruct;

import com.illusivesoulworks.spectrelib.config.SpectreLibInitializer;
import org.quiltmc.loader.api.ModContainer;

public class CulinaryConstructConfigInitializer implements SpectreLibInitializer {

  @Override
  public void onInitializeConfig(ModContainer modContainer) {
    CulinaryConstructMod.setupConfig();
  }
}
