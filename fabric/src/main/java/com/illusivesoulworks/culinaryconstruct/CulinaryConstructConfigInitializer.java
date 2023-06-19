package com.illusivesoulworks.culinaryconstruct;

import com.illusivesoulworks.spectrelib.config.SpectreLibInitializer;

public class CulinaryConstructConfigInitializer implements SpectreLibInitializer {

  @Override
  public void onInitializeConfig() {
    CulinaryConstructMod.setupConfig();
  }
}
