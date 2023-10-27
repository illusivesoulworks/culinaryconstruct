/*
 * Copyright (C) 2018-2022 Illusive Soulworks
 *
 * Culinary Construct is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Culinary Construct is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Culinary Construct.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.illusivesoulworks.culinaryconstruct.common.network;

import com.illusivesoulworks.culinaryconstruct.CulinaryConstructConstants;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public class CulinaryConstructForgeNetwork {

  private static final int PTC_VERSION = 1;

  private static SimpleChannel instance;

  public static SimpleChannel get() {
    return instance;
  }

  public static void setup() {
    instance = ChannelBuilder.named(new ResourceLocation(CulinaryConstructConstants.MOD_ID, "main"))
        .networkProtocolVersion(PTC_VERSION)
        .clientAcceptedVersions(Channel.VersionTest.exact(PTC_VERSION))
        .serverAcceptedVersions(Channel.VersionTest.exact(PTC_VERSION)).simpleChannel();

    registerC2S(CPacketRename.class, CPacketRename::encode,
        CPacketRename::decode, CPacketRename::handle);
  }

  public static <M> void registerC2S(Class<M> clazz, BiConsumer<M, FriendlyByteBuf> encoder,
                                     Function<FriendlyByteBuf, M> decoder,
                                     BiConsumer<M, ServerPlayer> messageConsumer) {
    instance.messageBuilder(clazz)
        .decoder(decoder)
        .encoder(encoder)
        .consumerNetworkThread((m, context) -> {
          context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();

            if (sender != null) {
              messageConsumer.accept(m, sender);
            }
          });
          context.setPacketHandled(true);
        })
        .add();
  }
}
