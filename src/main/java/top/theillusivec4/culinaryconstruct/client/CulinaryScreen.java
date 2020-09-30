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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import top.theillusivec4.culinaryconstruct.CulinaryConstruct;
import top.theillusivec4.culinaryconstruct.common.inventory.CulinaryStationContainer;
import top.theillusivec4.culinaryconstruct.common.network.CPacketRename;
import top.theillusivec4.culinaryconstruct.common.network.CulinaryConstructNetwork;

public class CulinaryScreen extends ContainerScreen<CulinaryStationContainer> implements
    IContainerListener {

  private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(
      CulinaryConstruct.MODID, "textures/gui/culinary_station_gui.png");
  private static final int WIDTH = 176;
  private static final int HEIGHT = 161;

  private TextFieldWidget nameField;

  public CulinaryScreen(CulinaryStationContainer screenContainer, PlayerInventory inv,
      ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    this.xSize = WIDTH;
    this.ySize = HEIGHT;
    this.titleX = 60;
    this.titleY = 6;
    this.playerInventoryTitleX = 8;
    this.playerInventoryTitleY = 67;
  }

  @Override
  protected void init() {
    super.init();

    if (this.minecraft != null) {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
    }
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    this.nameField = new TextFieldWidget(this.font, i + 62, j + 20, 103, 12,
        new TranslationTextComponent("culinaryconstruct.culinary_container"));
    this.nameField.setCanLoseFocus(false);
    this.nameField.setTextColor(-1);
    this.nameField.setDisabledTextColour(-1);
    this.nameField.setEnableBackgroundDrawing(false);
    this.nameField.setMaxStringLength(35);
    this.nameField.setResponder(this::updateName);
    this.children.add(this.nameField);
    this.setFocusedDefault(this.nameField);
    this.container.addListener(this);
  }

  @Override
  public void resize(@Nonnull Minecraft minecraft, int mouse1, int mouse2) {
    String s = this.nameField.getText();
    this.init(minecraft, mouse1, mouse2);
    this.nameField.setText(s);
  }

  @Override
  public void onClose() {
    super.onClose();
    this.container.removeListener(this);

    if (this.minecraft != null) {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
    }
  }

  @Override
  public boolean keyPressed(int key1, int key2, int key3) {

    if (key1 == 256 && this.minecraft != null && this.minecraft.player != null) {
      this.minecraft.player.closeScreen();
    }
    return this.nameField.keyPressed(key1, key2, key3) || this.nameField.canWrite() || super
        .keyPressed(key1, key2, key3);
  }

  @Override
  public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    RenderSystem.disableBlend();
    this.nameField.render(matrixStack, mouseX, mouseY, partialTicks);
    this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
  }

  private void updateName(String name) {
    if (this.container.getSlot(6).getHasStack()) {
      this.container.updateItemName(name);

      if (this.minecraft != null) {
        CulinaryConstructNetwork.INSTANCE.sendToServer(new CPacketRename(name));
      }
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrixStack,
      float partialTicks, int mouseX, int mouseY) {

    if (this.minecraft != null) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bindTexture(GUI_BACKGROUND);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
      this.blit(matrixStack, i + 59, j + 16, 0,
          this.ySize + (this.container.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

      if (this.container.getSlot(0).getHasStack() && !this.container.getSlot(6).getHasStack()) {
        this.blit(matrixStack, i + 133, j + 43, this.xSize, 0, 18, 18);
      }
    }
  }

  @Override
  public void sendAllContents(@Nonnull Container containerToSend,
      @Nonnull NonNullList<ItemStack> itemsList) {
    this.sendSlotContents(containerToSend, 6, containerToSend.getSlot(6).getStack());
  }

  @Override
  public void sendSlotContents(@Nonnull Container containerToSend, int slotInd,
      @Nonnull ItemStack stack) {

    if (slotInd == 6) {
      this.nameField.setText(stack.isEmpty() ? "" : this.nameField.getText());
      this.nameField.setEnabled(!stack.isEmpty());
      this.setListener(this.nameField);
    }
  }

  @Override
  public void sendWindowProperty(@Nonnull Container containerIn, int varToUpdate, int newValue) {
    //NO-OP
  }
}
