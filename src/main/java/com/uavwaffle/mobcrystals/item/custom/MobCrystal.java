package com.uavwaffle.mobcrystals.item.custom;

import com.uavwaffle.mobcrystals.MobCrystals;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MobCrystal extends Item {

    public MobCrystal(Properties properties) {
        super(properties);
    }


    private void saveMobToCrystal(Mob mob, ItemStack itemstack, @NotNull Player player, @NotNull InteractionHand usedHand) {
        ItemStack copy = itemstack.copy();
        copy.setCount(1);
        CompoundTag storedEntityTag = new CompoundTag();
        mob.save(storedEntityTag);
        CompoundTag tag = copy.getOrCreateTagElement(MobCrystals.MOD_ID);
        tag.put("boundentity", storedEntityTag);

        if (itemstack.getCount() == 1) {
            player.setItemInHand(usedHand, copy);
        } else {
            itemstack.shrink(1);
            if (!player.getInventory().add(copy)) {
                player.drop(copy, false);
            }
        }

    }

    private void loadMobFromCrystal(@NotNull Level level, Player player, CompoundTag mobTag, Vec3 clickedPos) {
        EntityType.create(mobTag,level).ifPresent((entity) -> {
            entity.setPos(clickedPos);
            ((ServerLevel)level).addWithUUID(entity);
        });
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        ItemStack itemstack = context.getItemInHand();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }
        InteractionHand usedHand = player.getUsedItemHand();

        if(level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        CompoundTag tag = itemstack.getTagElement(MobCrystals.MOD_ID);
        if (tag == null) {
            return InteractionResult.FAIL;
        }
        CompoundTag mobTag = tag.getCompound("boundentity");

        loadMobFromCrystal(level, player, mobTag, context.getClickLocation());


        ItemStack defaultCrystal = this.getDefaultInstance();
        if (itemstack.getCount() == 1) {
            player.setItemInHand(usedHand, defaultCrystal);
        } else {
            itemstack.shrink(1);
            if (!player.getInventory().add(defaultCrystal)) {
                player.drop(defaultCrystal, false);
            }
        }


        return InteractionResult.SUCCESS;
    }

    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack itemstack, @NotNull Player player, @NotNull LivingEntity interactionTarget, @NotNull InteractionHand usedHand) {
        if (!(interactionTarget instanceof Mob mobToSave)) {
            return InteractionResult.FAIL;
        }
        CompoundTag tag = itemstack.getTagElement(MobCrystals.MOD_ID);
        if (tag != null) {
            return InteractionResult.FAIL;
        }

        if (!player.level().isClientSide()) {
            saveMobToCrystal(mobToSave, itemstack, player, usedHand);
        }

        interactionTarget.discard();
        return InteractionResult.SUCCESS;
    }
}
