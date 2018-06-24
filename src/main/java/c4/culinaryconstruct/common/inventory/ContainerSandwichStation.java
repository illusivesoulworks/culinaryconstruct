/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.common.inventory;

import c4.culinaryconstruct.api.BreadRegistry;
import c4.culinaryconstruct.common.tileentity.TileEntitySandwichStation;
import c4.culinaryconstruct.common.util.NBTHelper;
import c4.culinaryconstruct.proxy.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

public class ContainerSandwichStation extends Container {

    private final InventoryCraftResult outputSlot = new InventoryCraftResult();
    private final IInventory ingredientSlots = new InventoryBasic("Ingredients", true, 6) {

        @Override
        public void markDirty()
        {
            super.markDirty();
            ContainerSandwichStation.this.onCraftMatrixChanged(this);
        }
    };
    private final BlockPos pos;
    private final World world;
    private String sandwichName;
    private TileEntitySandwichStation sandwichStation;

    public ContainerSandwichStation(InventoryPlayer playerInventory, final World world, final BlockPos pos, TileEntitySandwichStation te) {
        this.pos = pos;
        this.world = world;
        this.sandwichStation = te;
        initSlots(playerInventory);
        initInventory();
    }

    private void initInventory() {
        IItemHandler inventory = sandwichStation.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
        if (inventory != null) {
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    this.inventorySlots.get(i).putStack(inventory.extractItem(i, stack.getCount(), false));
                }
            }
        }
    }

    private void initSlots(InventoryPlayer playerInventory) {
        addLayerSlots();
        this.addSlotToContainer(new SlotBread(this.ingredientSlots, 5, 10, 50));
        this.addSlotToContainer(new SlotSandwich(this.outputSlot, 6, 150, 50));
        addPlayerSlots(playerInventory);
    }

    private void addLayerSlots() {
        for (int i = 0; i < 5; ++i)
        {
            this.addSlotToContainer(new SlotLayeredIngredient(this.ingredientSlots, i, 30 + i * 18, 50));
        }
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

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!this.world.isRemote)
        {
            this.clearContainer(playerIn, this.world, this.ingredientSlots);
        }
    }

    @Override
    protected void clearContainer(EntityPlayer playerIn, @Nonnull World worldIn, IInventory inventoryIn)
    {
        IItemHandler inventory = sandwichStation.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
        if (inventory != null) {
            for (int i = 0; i < inventory.getSlots(); ++i) {
                inventory.insertItem(i, inventoryIn.removeStackFromSlot(i), false);
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        super.onCraftMatrixChanged(inventoryIn);

        if (inventoryIn == this.ingredientSlots)
        {
            this.updateSandwichOutput();
        }
    }

    public void updateSandwichOutput() {
        ItemStack bread = this.ingredientSlots.getStackInSlot(5);

        if (bread.isEmpty()) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            return;
        }

        NonNullList<ItemStack> ingredientsList = NonNullList.create();
        int totalFood = 0;
        float totalSaturation = 0;
        int complexity = 0;

        for (int i = 0; i < this.ingredientSlots.getSizeInventory(); i++) {
            ItemStack stack = this.ingredientSlots.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (!(stack.getItem() instanceof ItemFood)) {
                    this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
                    return;
                } else {
                    ItemFood food = (ItemFood) stack.getItem();
                    totalFood += food.getHealAmount(stack);
                    boolean flag = true;
                    for (ItemStack existing : ingredientsList) {
                        if (!existing.isEmpty() && existing.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == existing.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, existing)) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag && !BreadRegistry.isValidBread(stack)) complexity++;
                    ItemStack copy = stack.copy();
                    if (copy.getCount() > 1) {
                        copy.setCount(1);
                    }
                    ingredientsList.add(copy);
                }
            }
        }

        for (ItemStack stack : ingredientsList) {
            if (!stack.isEmpty() && stack.getItem() instanceof ItemFood) {
                ItemFood food = (ItemFood) stack.getItem();
                double proportion = ((double) food.getHealAmount(stack)) / ((double) totalFood);
                totalSaturation += proportion * food.getSaturationModifier(stack);
            }
        }

        if (ingredientsList.size() <= 1 || totalFood <= 0 || totalSaturation < 0) {
            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
            return;
        }

        double count = 1.0D;
        int averageFood = MathHelper.ceil(((double) totalFood) / count);
        while (averageFood > 10) {
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
                if (index < 43 && !this.mergeItemStack(itemstack1, 0, 6, false))
                {
                    return ItemStack.EMPTY;
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

    private class SlotBread extends Slot
    {
        public SlotBread(IInventory iInventoryIn, int index, int xPosition, int yPosition)
        {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return BreadRegistry.isValidBread(stack);
        }
    }

    private class SlotLayeredIngredient extends Slot
    {
        public SlotLayeredIngredient(IInventory iInventoryIn, int index, int xPosition, int yPosition)
        {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return stack.getItem() instanceof ItemFood;
        }
    }

    private class SlotSandwich extends Slot
    {
        public SlotSandwich(IInventory iInventoryIn, int index, int xPosition, int yPosition)
        {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) { return false; }

        @Nonnull
        @Override
        public ItemStack onTake(EntityPlayer thePlayer, @Nonnull ItemStack stack)
        {
            IInventory ingredients = ContainerSandwichStation.this.ingredientSlots;
            for (int i = 0; i < ingredients.getSizeInventory(); i++) {
                ItemStack slot = ingredients.getStackInSlot(i);
                slot.shrink(1);
                if (slot.isEmpty()) {
                    ingredients.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
            ContainerSandwichStation.this.updateSandwichOutput();
            return stack;
        }
    }
}
