package com.seris02.mattertransporter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ItemMatterTransporter extends Item {
	
	private int damageOnPickup = 1;

	public ItemMatterTransporter(Properties properties, int damageOnPickup) {
		super(properties);
		this.damageOnPickup = damageOnPickup;
	}
    
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack matter = player.getItemInHand(hand);
		
		if (world.isClientSide) {
			return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, matter);
		}
		
		System.out.println("Starting");
		
		BlockHitResult ray = getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
		BlockPos lookPos = ray.getBlockPos();
		BlockPos blockPos = lookPos.relative(ray.getDirection());
		
		CompoundTag nbtTotal = matter.serializeNBT();
		CompoundTag nbt = nbtTotal.getCompound("tag");
		if (!nbt.contains("has_block")) {
			nbt = new CompoundTag();
			nbt.putBoolean("has_block", false);
			nbt.putBoolean("has_tile_entity", false);
		}
		boolean hasBlock = nbt.getBoolean("has_block");
		boolean hasTE = nbt.getBoolean("has_tile_entity");
		
		//are we trying to place/remove from an empty space
		if (world.getBlockState(lookPos).isAir() || world.getBlockState(lookPos).getFluidState().createLegacyBlock().getBlock() == world.getBlockState(lookPos).getBlock()) {
			return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, matter);
		}
		
		
		if (hasBlock && nbt.contains("blockstate")) {
			CompoundTag statenbt = nbt.getCompound("blockstate");
			if (statenbt.contains("Properties") && statenbt.getCompound("Properties").contains("facing")) {
				CompoundTag prop = statenbt.getCompound("Properties");
				prop.putBoolean("waterlogged", false);
				Vec3 n = player.getLookAngle();
				prop.putString("facing", Direction.getNearest(n.x, n.y, n.z).getOpposite().getName());
				statenbt.put("Properties", prop);
			}
			BlockState state = NbtUtils.readBlockState(statenbt);
			if (state == null) {
				nbt.putBoolean("has_block", false);
				return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, matter);
			}
			ItemStack blockStack = new ItemStack(state.getBlock().asItem());
			BlockPlaceContext p = new BlockPlaceContext(world, player, hand, blockStack, ray);
			if (world.getBlockState(blockPos).getBlock() == Blocks.AIR || world.getBlockState(blockPos).canBeReplaced(p)) {
				world.setBlock(blockPos, state, 3);
				if (hasTE) {
					try {
						CompoundTag teTag = nbt.getCompound("tile_entity");
						teTag.putInt("x", blockPos.getX());
						teTag.putInt("y", blockPos.getY());
						teTag.putInt("z", blockPos.getZ());
						BlockEntity te = world.getBlockEntity(blockPos);
						if (te == null) {
							te = BlockEntity.loadStatic(blockPos, state, teTag);
						} else {
							te.deserializeNBT(teTag);
						}
						te.setChanged();
					} catch (Exception e) {
						return new InteractionResultHolder<ItemStack>(InteractionResult.FAIL, matter);
					}
				}
				nbt.putBoolean("has_block", false);
				nbt.putBoolean("has_tile_entity", false);
				nbt.put("tile_entity", new CompoundTag());
				world.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 0.7F);
				nbtTotal.put("tag", nbt);
				matter.deserializeNBT(nbtTotal);
				matter.hurt(damageOnPickup, RandomSource.create(), (ServerPlayer) player);
			}
			
		} else {
			BlockState state = world.getBlockState(lookPos);
			BlockEntity te = world.getBlockEntity(lookPos);
			if (te != null) {
				nbt.put("tile_entity", te.serializeNBT());
				nbt.putBoolean("has_tile_entity", true);
			}
			nbt.put("blockstate", NbtUtils.writeBlockState(state));
			nbt.putBoolean("has_block", true);
			
			world.removeBlockEntity(lookPos);
			world.removeBlock(lookPos, true);
			world.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 0.7F);
			nbtTotal.put("tag", nbt);
			matter.deserializeNBT(nbtTotal);
		}
		
		return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, matter);
	}

	@Override
	public boolean isFoil(ItemStack item) {
	   return item.serializeNBT().getBoolean("has_block");
	}
}