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

package ironfurnaces.blocks;

import ironfurnaces.init.Registration;
import ironfurnaces.tileentity.BlockWirelessEnergyHeaterTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class BlockWirelessEnergyHeater extends Block implements EntityBlock {

    public static final String HEATER = "heater";

    public BlockWirelessEnergyHeater(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new BlockWirelessEnergyHeaterTile(p_153215_, p_153216_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(level, type, Registration.HEATER_TILE.get());
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
        return p_152134_ == p_152133_ ? (BlockEntityTicker<A>)p_152135_ : null;
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level p_151988_, BlockEntityType<T> p_151989_, BlockEntityType<? extends BlockWirelessEnergyHeaterTile> p_151990_) {
        return p_151988_.isClientSide ? null : createTickerHelper(p_151989_, p_151990_, BlockWirelessEnergyHeaterTile::tick);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (!world.isClientSide) {
            BlockWirelessEnergyHeaterTile te = (BlockWirelessEnergyHeaterTile) world.getBlockEntity(pos);
            ItemStack stack = new ItemStack(Registration.HEATER.get());
            if (te.hasCustomName()) {
                stack.set(DataComponents.CUSTOM_NAME, te.getDisplayName());
            }
            if (te.getEnergy() > 0) {
                stack.set(Registration.ENERGY.get(), te.getEnergy());
            }
            if (!player.isCreative()) Containers.dropItemStack(world, te.getBlockPos().getX(), te.getBlockPos().getY(), te.getBlockPos().getZ(), stack);
        }
        return super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            if (world.getBlockEntity(pos) != null)
            {
                BlockWirelessEnergyHeaterTile te = (BlockWirelessEnergyHeaterTile) world.getBlockEntity(pos);
                if (stack.get(DataComponents.CUSTOM_NAME) != null) {
                    te.setCustomName(stack.get(DataComponents.CUSTOM_NAME));
                }
                int energy = stack.getOrDefault(Registration.ENERGY.get(), 0);
                te.setEnergy(energy);
            }


        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult pHitResult) {
        return this.interactWith(level, pos, player, state);
    }

    private InteractionResult interactWith(Level level, BlockPos pos, Player player, BlockState state) {

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            serverPlayer.openMenu((MenuProvider) be, buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean p_196243_5_) {
        if (state.getBlock() != oldState.getBlock()) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof BlockWirelessEnergyHeaterTile) {
                Containers.dropContents(world, pos, (BlockWirelessEnergyHeaterTile) te);
                world.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, world, pos, oldState, p_196243_5_);
        }
    }

}
