/*
 * Copyright 2025 pizzaatime and XenoMustache
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ironfurnaces.network;

import ironfurnaces.IronFurnaces;
import ironfurnaces.init.Registration;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketShowConfig(int set) implements CustomPacketPayload {



    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(IronFurnaces.MOD_ID, "show_config_packet");
    public static final CustomPacketPayload.Type<PacketShowConfig> TYPE = new Type<>(ID);


    public static final StreamCodec<RegistryFriendlyByteBuf, PacketShowConfig> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketShowConfig::set,
            PacketShowConfig::new);




    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



    public static PacketShowConfig create(int set) {
        return new PacketShowConfig(set);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            // Here we are server side
            Player player = ctx.player();
            player.getData(Registration.PLAYER_SHOW_CONFIG).config = set;
        });
    }



}
