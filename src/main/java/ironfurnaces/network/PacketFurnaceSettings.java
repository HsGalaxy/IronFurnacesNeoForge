/*
 * Copyright 2025 Astryxion
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
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record PacketFurnaceSettings(int x, int y, int z, int index, int set) implements CustomPacketPayload {


    public static final Identifier ID = Identifier.fromNamespaceAndPath(IronFurnaces.MOD_ID, "furnace_settings_packet");
    public static final CustomPacketPayload.Type<PacketFurnaceSettings> TYPE = new Type<>(ID);




    public static final StreamCodec<RegistryFriendlyByteBuf, PacketFurnaceSettings> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketFurnaceSettings::x,
            ByteBufCodecs.INT, PacketFurnaceSettings::y,
            ByteBufCodecs.INT, PacketFurnaceSettings::z,
            ByteBufCodecs.INT, PacketFurnaceSettings::index,
            ByteBufCodecs.INT, PacketFurnaceSettings::set,
            PacketFurnaceSettings::new);



    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketFurnaceSettings create(int x, int y, int z, int index, int set) {
        return new PacketFurnaceSettings(x, y, z, index, set);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            Player player = ctx.player();
            BlockPos pos = new BlockPos(x, y, z);
            BlockIronFurnaceTileBase te = (BlockIronFurnaceTileBase) player.level().getBlockEntity(pos);
            if (player.level().isLoaded(pos)) {
                te.furnaceSettings.set(index, set);
                te.getLevel().markAndNotifyBlock(pos, player.level().getChunkAt(pos), te.getLevel().getBlockState(pos).getBlock().defaultBlockState(), te.getLevel().getBlockState(pos), 2, 0);
                te.setChanged();
            }

        });
    }
}