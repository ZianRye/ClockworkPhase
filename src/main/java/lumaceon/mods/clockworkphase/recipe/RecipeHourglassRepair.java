package lumaceon.mods.clockworkphase.recipe;

import lumaceon.mods.clockworkphase.init.ModItems;
import lumaceon.mods.clockworkphase.item.component.ItemClockwork;
import lumaceon.mods.clockworkphase.item.component.ItemMainspring;
import lumaceon.mods.clockworkphase.item.elemental.hourglass.ItemHourglass;
import lumaceon.mods.clockworkphase.lib.NBTTags;
import lumaceon.mods.clockworkphase.util.NBTHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class RecipeHourglassRepair implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting ic, World world)
    {
        ItemStack item;
        boolean mainspring = false;
        boolean clockwork = false;
        boolean hourglass = false;
        boolean doubles = false;

        boolean alreadyContainsMainspring = false;
        boolean alreadyContainsClockwork = false;

        for(int n = 0; n < ic.getSizeInventory(); n++)
        {
            item = ic.getStackInSlot(n);
            if(item != null)
            {
                if(item.getItem() instanceof ItemHourglass)
                {
                    if(hourglass)
                    {
                        doubles = true;
                    }
                    hourglass = true;

                    if(NBTHelper.getInt(item, NBTTags.MAX_TENSION) != 0)
                    {
                        alreadyContainsMainspring = true;
                    }

                    if(NBTHelper.hasTag(item, NBTTags.CLOCKWORK))
                    {
                        alreadyContainsClockwork = true;
                    }
                }

                if(item.getItem() instanceof ItemMainspring)
                {
                    if(mainspring)
                    {
                        doubles = true;
                    }
                    mainspring = true;
                }

                if(item.getItem() instanceof ItemClockwork)
                {
                    if(clockwork)
                    {
                        doubles = true;
                    }
                    clockwork = true;
                }
            }
        }

        if(alreadyContainsMainspring && mainspring)
        {
            return false;
        }

        if(alreadyContainsClockwork && clockwork)
        {
            return false;
        }

        if(doubles) { return false; }
        if((mainspring || clockwork) && hourglass) { return true; }

        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting ic)
    {
        ItemStack tempItem;
        ItemStack mainspring = null;
        ItemStack clockwork = null;
        ItemStack hourglass = null;

        for(int n = 0; n < ic.getSizeInventory(); n++)
        {
            tempItem = ic.getStackInSlot(n);
            if (tempItem != null)
            {
                if(tempItem.getItem() instanceof ItemMainspring)
                {
                    mainspring = tempItem.copy();
                }

                if(tempItem.getItem() instanceof ItemClockwork)
                {
                    clockwork = tempItem.copy();
                }

                if(tempItem.getItem() instanceof ItemHourglass)
                {
                    hourglass = tempItem.copy();
                }
            }
        }

        ItemStack output = hourglass;
        if(mainspring != null)
        {
            int currentTension = NBTHelper.getInt(mainspring, NBTTags.TENSION_ENERGY);
            int maxTension = NBTHelper.getInt(mainspring, NBTTags.MAX_TENSION);

            NBTHelper.setInteger(output, NBTTags.TENSION_ENERGY, currentTension);
            NBTHelper.setInteger(output, NBTTags.MAX_TENSION, maxTension);

            if(maxTension / 10 == 0) { output.setItemDamage(output.getMaxDamage()); }
            else { output.setItemDamage(10 - (currentTension / (maxTension / 10))); }
        }

        if(clockwork != null)
        {
            NBTHelper.setInteger(output, NBTTags.QUALITY, NBTHelper.getInt(clockwork, NBTTags.QUALITY));
            NBTHelper.setInteger(output, NBTTags.SPEED, NBTHelper.getInt(clockwork, NBTTags.SPEED));
            NBTHelper.setInteger(output, NBTTags.MEMORY, NBTHelper.getInt(clockwork, NBTTags.MEMORY));

            NBTTagList nbtList = new NBTTagList();
            NBTTagCompound tag = new NBTTagCompound();
            clockwork.writeToNBT(tag);
            nbtList.appendTag(tag);
            NBTHelper.setTag(output, NBTTags.CLOCKWORK, nbtList);
        }
        return output;
    }

    @Override
    public int getRecipeSize()
    {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return new ItemStack(ModItems.hourglass);
    }
}
