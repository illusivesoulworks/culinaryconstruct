/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.network;

import c4.culinaryconstruct.common.inventory.ContainerSandwichStation;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketName implements IMessage {

    public PacketName(){}

    private String toSend;

    public PacketName(String name) {
        this.toSend = name;
    }

    @Override public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, toSend);
    }

    @Override public void fromBytes(ByteBuf buf) {
        toSend = ByteBufUtils.readUTF8String(buf);
    }

    public static class PacketNameHandler implements IMessageHandler<PacketName, IMessage> {
        // Do note that the default constructor is required, but implicitly defined in this case

        @Override public IMessage onMessage(PacketName message, MessageContext ctx) {
            // This is the player the packet was sent to the server from
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            // The value that was sent
            String s = message.toSend;
            // Execute the action on the main server thread by adding it as a scheduled task
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                if (serverPlayer.openContainer instanceof ContainerSandwichStation) {
                    ContainerSandwichStation sandwichStation = (ContainerSandwichStation) serverPlayer.openContainer;
                    sandwichStation.updateSandwichName(s);
                }
            });
            // No response packet
            return null;
        }
    }
}
