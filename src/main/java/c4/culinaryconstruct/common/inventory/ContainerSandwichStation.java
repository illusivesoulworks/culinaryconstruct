/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.common.inventory;

import c4.culinaryconstruct.api.ICulinaryIngredient;
import c4.culinaryconstruct.common.tileentity.TileEntitySandwichStation;
import c4.culinaryconstruct.common.util.BreadHelper;
import c4.culinaryconstruct.common.util.ConfigHandler;
import c4.culinaryconstruct.common.util.NBTHelper;
import c4.culinaryconstruct.common.util.SandwichHelper;
import c4.culinaryconstruct.proxy.CommonProxy;
import net.minecraft.block.BlockCake;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class ContainerSandwichStation extends Container {

    private final InventoryCraftResult outputSlot = new InventoryCraftResult();
    private final IItemHandler ingredientHandler;
    private final BlockPos pos;
    private final World world;
    private String sandwichName;
    private TileEntitySandwichStation sandwichStation;

    public ContainerSandwichStation(InventoryPlayer playerInventory, final World world, final BlockPos pos, TileEntitySandwichStation te) {
        this.pos = pos;
        this.world = world;
        this.sandwichStation = te;
        this.ingredientHandler = sandwichStation.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        initSlots(playerInventory);
        this.updateSandwichOutput();
    }

    private void initSlots(InventoryPlayer playerInventory) {
        this.addSlotToContainer(new SlotBread(ingredientHandler, 0, 10, 50));

        for (int i = 1; i < 6; ++i) {
            this.addSlotToContainer(new SlotLayeredIngredient(ingredientHandler, i, 12 + i * 18, 50));
        }
        this.addSlotToContainer(new SlotSandwich(this.outputSlot, 6, 150, 50));
        addPlayerSlots(playerInventory);
    }

    private void addPlayerSlots(InventoryPlayer playerInventory) {

        //Main inventory
        for (int row = 0; row < 3; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 79 + row * 18));
            }
        }

        //Hotbar
        for (int hotbar = 0; hotbar < 9; hotbar++)
        {
            this.addSlotToContainer(new Slot(playerInventory, hotbar, 8 + hotbar * 18, 137));
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer playerIn) {
        return world.getBlockState(pos).getBlock() == CommonProxy.sandwichStation && playerIn.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public void updateSandwichOutput() {
        ItemStack bread = this.ingredientHandler.getStackInSlot(0);

        if (bread.isEmpty()) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            this.detectAndSendChanges();
            return;
        }

        NonNullList<ItemStack> ingredientsList = NonNullList.create();
        int totalFood = 0;
        float totalSaturation = 0;
        int complexity = 0;

        for (int i = 1; i < this.ingredientHandler.getSlots(); i++) {
            ItemStack stack = this.ingredientHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ICulinaryIngredient) {
                    totalFood += ((ICulinaryIngredient)stack.getItem()).getFoodAmount(stack);
                } else if (stack.getItem() instanceof ItemFood) {
                    totalFood += ((ItemFood) stack.getItem()).getHealAmount(stack);
                } else if (stack.getItem() instanceof ItemBlockSpecial && ((ItemBlockSpecial) stack.getItem()).getBlock() instanceof BlockCake) {
                    totalFood += 14;
                } else {
                    this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                    this.detectAndSendChanges();
                    return;
                }
                boolean flag = true;
                for (ItemStack existing : ingredientsList) {
                    if (!existing.isEmpty() && existing.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == existing.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, existing)) {
                        flag = false;
                        break;
                    }
                }
                if (flag && !BreadHelper.isValidBread(stack)) complexity++;
                ItemStack copy = stack.copy();
                if (copy.getCount() > 1) {
                    copy.setCount(1);
                }
                ingredientsList.add(copy);
            }
        }

        //Copy the ingredients check to the bread stack last to maintain list order for backwards-compatibility
        //TODO: Adjust appropriately in 1.13
        if (bread.getItem() instanceof ItemFood) {
            totalFood += ((ItemFood) bread.getItem()).getHealAmount(bread);
        } else {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            this.detectAndSendChanges();
            return;
        }
        ItemStack copy = bread.copy();
        if (copy.getCount() > 1) {
            copy.setCount(1);
        }
        ingredientsList.add(copy);

        for (ItemStack stack : ingredientsList) {
            double proportion = 0.0D;
            double saturationModifier = 0.0D;
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ICulinaryIngredient) {
                    proportion = ((double) ((ICulinaryIngredient)stack.getItem()).getFoodAmount(stack)) /
                            ((double) totalFood);
                    saturationModifier = ((ICulinaryIngredient)stack.getItem()).getSaturationModifier(stack);
                }
                if (stack.getItem() instanceof ItemFood) {
                    proportion = ((double) ((ItemFood)stack.getItem()).getHealAmount(stack)) / ((double) totalFood);
                    saturationModifier = ((ItemFood)stack.getItem()).getSaturationModifier(stack);
                }
                else if (stack.getItem() instanceof ItemBlockSpecial && ((ItemBlockSpecial)stack.getItem()).getBlock() instanceof BlockCake) {
                    proportion = 14.0D / ((double) totalFood);
                    saturationModifier = 2.8D;
                }
                totalSaturation += proportion * saturationModifier;
            }
        }

        if (ingredientsList.size() <= 1 || totalFood <= 0 || totalSaturation < 0) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            this.detectAndSendChanges();
            return;
        }

        double count = 1.0D;
        int averageFood = MathHelper.ceil(((double) totalFood) / count);
        while (averageFood > ConfigHandler.maxTotalFood) {
            count++;
            averageFood = MathHelper.ceil(((double) totalFood) / count);
        }

        int size = ingredientsList.size() - 1;
        int bonus = MathHelper.clamp(complexity - (size / 2 + 1), -2, 2);
        totalSaturation *= 1.0F + (bonus * 0.2F);

        ItemStack output = new ItemStack(CommonProxy.sandwich);
        NBTHelper.setTagSize(output, size);
        NBTHelper.setIngredientsList(output, ingredientsList);
        NBTHelper.setTagFood(output, averageFood);
        NBTHelper.setTagSaturation(output, totalSaturation);
        NBTHelper.setTagBonus(output, bonus);
        if (StringUtils.isBlank(this.sandwichName))
        {
            output.clearCustomName();
        }
        else if (!this.sandwichName.equals(output.getDisplayName()))
        {
            output.setStackDisplayName(this.sandwichName);
        }
        output.setCount((int) count);
        this.outputSlot.setInventorySlotContents(0, output);
        this.detectAndSendChanges();
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 6)
            {
                if (!this.mergeItemStack(itemstack1, 7, 43, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 7)
            {
                if (index < 43)
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false) && !this.mergeItemStack(itemstack1, 1, 6,
                            false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (!this.mergeItemStack(itemstack1, 7, 43, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    public void updateSandwichName(String newName)
    {
        this.sandwichName = newName;
        this.updateSandwichOutput();
    }

    private class SlotBread extends SlotItemHandler
    {
        public SlotBread(IItemHandler handler, int index, int xPosition, int yPosition)
        {
            super(handler, index, xPosition, yPosition);
        }

        @Override
        public void onSlotChanged()
        {
            ContainerSandwichStation.this.updateSandwichOutput();
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return BreadHelper.isValidBread(stack);
        }
    }

    private class SlotLayeredIngredient extends SlotItemHandler
    {
        public SlotLayeredIngredient(IItemHandler handler, int index, int xPosition, int yPosition)
        {
            super(handler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return SandwichHelper.isValidIngredient(stack);
        }

        @Override
        public void onSlotChanged()
        {
            ContainerSandwichStation.this.updateSandwichOutput();
        }
    }

    private class SlotSandwich extends Slot
    {
        public SlotSandwich(IInventory inventory, int index, int xPosition, int yPosition)
        {
            super(inventory, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) { return false; }

        @Override
        public void onSlotChanged()
        {
            ContainerSandwichStation.this.updateSandwichOutput();
        }

        @Nonnull
        @Override
        public ItemStack onTake(EntityPlayer thePlayer, @Nonnull ItemStack stack)
        {
            IItemHandler ingredients = ContainerSandwichStation.this.ingredientHandler;
            if (ingredients != null) {
                for (int i = 0; i < ingredients.getSlots(); i++) {
                    ItemStack slot = ingredients.getStackInSlot(i);
                    slot.shrink(1);
                }
            }
            ContainerSandwichStation.this.updateSandwichOutput();
            return stack;
        }
    }
}
