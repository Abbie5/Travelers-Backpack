package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraftforge.items.ItemStackHandler;

public class CraftingInventoryImproved extends CraftingInventory
{
    private final ITravelersBackpackInventory inventory;
    private final ItemStackHandler craftingInventory;
    private final Container eventHandler;

    public CraftingInventoryImproved(ITravelersBackpackInventory inventory, Container eventHandlerIn)
    {
        super(eventHandlerIn, 3, 3);
        this.inventory = inventory;
        this.craftingInventory = inventory.getCraftingGridInventory();
        this.eventHandler = eventHandlerIn;
    }

    public int getSizeInventory()
    {
        return this.craftingInventory.getSlots();
    }

    @Override
    public boolean isEmpty()
    {
        for(int i = 0; i < getSizeInventory(); i++)
        {
            if(!getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        return index >= this.getSizeInventory() ? ItemStack.EMPTY : this.craftingInventory.getStackInSlot(index);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        if(index >= 0 && index < this.getSizeInventory())
        {
            ItemStack stack = getStackInSlot(index).copy();
            setInventorySlotContents(index, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = index >= 0 && index < getSizeInventory() && !getStackInSlot(index).isEmpty() && count > 0 ? getStackInSlot(index).split(count) : ItemStack.EMPTY;

        if(!itemstack.isEmpty())
        {
            this.eventHandler.onCraftMatrixChanged(this);
            markDirty();
        }
        return itemstack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        this.craftingInventory.setStackInSlot(index, stack);
        this.eventHandler.onCraftMatrixChanged(this);
    }

    @Override
    public void markDirty()
    {
        if(this.inventory.getScreenID() != Reference.TRAVELERS_BACKPACK_TILE_SCREEN_ID)
        {
            this.inventory.markDirty();
        }
    }

    public boolean isUsableByPlayer(PlayerEntity player)
    {
        return true;
    }

    public void clear() { }

    @Override
    public int getHeight()
    {
        return 3;
    }

    @Override
    public int getWidth()
    {
        return 3;
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper)
    {
        for(int i = 0; i < getSizeInventory(); i++)
        {
            helper.accountPlainStack(getStackInSlot(i));
        }
    }
}