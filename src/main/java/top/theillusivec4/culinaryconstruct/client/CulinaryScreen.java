/*
 * Copyright (c) 2018-2019 C4
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

package top.theillusivec4.culinaryconstruct.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.common.inventory.CulinaryStationContainer;

public class CulinaryScreen extends ContainerScreen<CulinaryStationContainer> {

  private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(CulinaryConstruct.MODID, "textures/gui/culinary_station_gui.png");
  private static final int WIDTH = 176;
  private static final int HEIGHT = 161;

  public CulinaryScreen(CulinaryStationContainer screenContainer, PlayerInventory inv,
      ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    this.xSize = WIDTH;
    this.ySize = HEIGHT;
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    GlStateManager.disableLighting();
    GlStateManager.disableBlend();
    this.font.drawString(this.title.getFormattedText(), 58.0F, 6.0F, 4210752);
    GlStateManager.enableLighting();
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    this.renderBackground();
    super.render(mouseX, mouseY, partialTicks);
    this.renderHoveredToolTip(mouseX, mouseY);
    GlStateManager.disableLighting();
    GlStateManager.disableBlend();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    if (this.minecraft != null) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(GUI_BACKGROUND);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.blit(i, j, 0, 0, this.xSize, this.ySize);
      this.blit(i + 59, j + 16, 0, this.ySize + (this.container.getSlot(0).getHasStack() ? 0 : 16),
          110, 16);

      if ((this.container.getSlot(0).getHasStack() || this.container.getSlot(1).getHasStack()) && !this.container.getSlot(6).getHasStack()) {
        this.blit(i + 115, j + 54, this.xSize, 0, 28, 21);
      }
    }
  }
}
