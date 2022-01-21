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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.common.inventory.CulinaryStationContainer;
import top.theillusivec4.culinaryconstruct.common.network.CPacketRename;
import top.theillusivec4.culinaryconstruct.common.network.CulinaryConstructNetwork;

public class CulinaryScreen extends AbstractContainerScreen<CulinaryStationContainer> implements
    ContainerListener {

  private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(
      CulinaryConstruct.MOD_ID, "textures/gui/culinary_station_gui.png");
  private static final int WIDTH = 176;
  private static final int HEIGHT = 161;

  private EditBox nameField;

  public CulinaryScreen(CulinaryStationContainer screenContainer, Inventory inv,
                        Component titleIn) {
    super(screenContainer, inv, titleIn);
    this.imageWidth = WIDTH;
    this.imageHeight = HEIGHT;
    this.titleLabelX = 60;
    this.titleLabelY = 6;
    this.inventoryLabelX = 8;
    this.inventoryLabelY = 67;
  }

  @Override
  protected void init() {
    super.init();

    if (this.minecraft != null) {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
    }
    int i = (this.width - this.imageWidth) / 2;
    int j = (this.height - this.imageHeight) / 2;
    this.nameField = new EditBox(this.font, i + 62, j + 20, 103, 12,
        new TranslatableComponent("culinaryconstruct.culinary_container"));
    this.nameField.setCanLoseFocus(false);
    this.nameField.setTextColor(-1);
    this.nameField.setTextColorUneditable(-1);
    this.nameField.setBordered(false);
    this.nameField.setMaxLength(35);
    this.nameField.setResponder(this::updateName);
    this.addWidget(this.nameField);
    this.setInitialFocus(this.nameField);
    this.menu.addSlotListener(this);
  }

  @Override
  public void resize(@Nonnull Minecraft minecraft, int mouse1, int mouse2) {
    String s = this.nameField.getValue();
    this.init(minecraft, mouse1, mouse2);
    this.nameField.setValue(s);
  }

  @Override
  public void removed() {
    super.removed();
    this.menu.removeSlotListener(this);

    if (this.minecraft != null) {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }
  }

  @Override
  public boolean keyPressed(int key1, int key2, int key3) {

    if (key1 == 256 && this.minecraft != null && this.minecraft.player != null) {
      this.minecraft.player.closeContainer();
    }
    return this.nameField.keyPressed(key1, key2, key3) || this.nameField.canConsumeInput() || super
        .keyPressed(key1, key2, key3);
  }

  @Override
  public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    RenderSystem.disableBlend();
    this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
    this.renderTooltip(matrixStack, mouseX, mouseY);
  }

  private void updateName(String name) {
    if (this.menu.getSlot(6).hasItem()) {
      this.menu.updateItemName(name);

      if (this.minecraft != null) {
        CulinaryConstructNetwork.INSTANCE.sendToServer(new CPacketRename(name));
      }
    }
  }

  @Override
  protected void renderBg(@Nonnull PoseStack matrixStack,
                          float partialTicks, int mouseX, int mouseY) {

    if (this.minecraft != null) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, GUI_BACKGROUND);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
      this.blit(matrixStack, i + 59, j + 16, 0,
          this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);

      if (this.menu.getSlot(0).hasItem() && !this.menu.getSlot(6).hasItem()) {
        this.blit(matrixStack, i + 133, j + 43, this.imageWidth, 0, 18, 18);
      }
    }
  }

  @Override
  public void slotChanged(@Nonnull AbstractContainerMenu containerToSend, int slotInd,
                          @Nonnull ItemStack stack) {

    if (slotInd == 6) {
      this.nameField.setValue(stack.isEmpty() ? "" : this.nameField.getValue());
      this.nameField.setEditable(!stack.isEmpty());
      this.setFocused(this.nameField);
    }
  }

  @Override
  public void dataChanged(@Nonnull AbstractContainerMenu pContainerMenu, int pDataSlotIndex,
                          int pValue) {
    this.slotChanged(pContainerMenu, 6, pContainerMenu.getSlot(6).getItem());
  }
}
