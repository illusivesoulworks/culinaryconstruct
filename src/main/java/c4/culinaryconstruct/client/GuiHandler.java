/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.client;

import c4.culinaryconstruct.common.inventory.ContainerSandwichStation;
import c4.culinaryconstruct.common.tileentity.TileEntitySandwichStation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int GUI_SANDWICH_STATION_ID = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        switch (ID) {
            case GUI_SANDWICH_STATION_ID:
                BlockPos pos = new BlockPos(x, y, z);
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntitySandwichStation) {
                    return new ContainerSandwichStation(player.inventory, world, pos, (TileEntitySandwichStation) te);
                }
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        switch (ID) {
            case GUI_SANDWICH_STATION_ID:
                BlockPos pos = new BlockPos(x, y, z);
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntitySandwichStation) {
                    return new GuiContainerSandwichStation(new ContainerSandwichStation(player.inventory, world, pos, (TileEntitySandwichStation) te));
                }
        }

        return null;
    }
}
