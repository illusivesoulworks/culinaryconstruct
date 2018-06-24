/*
 * Copyright (c) 2018 <C4>
 *
 * This Java class is distributed as a part of the Culinary Construct mod.
 * Culinary Construct is open source and distributed under the GNU Lesser General Public License v3.
 * View the source code and license file on github: https://github.com/TheIllusiveC4/CulinaryConstruct
 */

package c4.culinaryconstruct.debug;

import c4.culinaryconstruct.CulinaryConstruct;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;

public class CommandHunger extends CommandBase {

    public CommandHunger(){
        aliases = Lists.newArrayList(CulinaryConstruct.MODID, "DB", "db");
    }

    private final List<String> aliases;

    @Override
    @Nonnull
    public String getName() {
        return "db";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "db <val>";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {

        if (!(sender instanceof EntityPlayer) || args.length < 1) {
            return;
        }

        String s = args[0];
        int value;
        try {
            value = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Error parsing hunger value!"));
            return;
        }

        ((EntityPlayer) sender).getFoodStats().setFoodLevel(value);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
