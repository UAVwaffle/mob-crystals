package com.uavwaffle.mobcrystals.item.custom;

import com.uavwaffle.mobcrystals.MobCrystals;
import com.uavwaffle.mobcrystals.config.Config;
import com.uavwaffle.mobcrystals.utilities.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

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
        tag.put("bound_entity", storedEntityTag);

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
        CompoundTag mobTag = tag.getCompound("bound_entity");

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
        if (!isValidMob(mobToSave)) {
            return InteractionResult.FAIL;
        }

        if (!player.level().isClientSide()) {
            saveMobToCrystal(mobToSave, itemstack, player, usedHand);
        }

        interactionTarget.discard();
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemstack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {

        if (level == null) return;
        CompoundTag tag = itemstack.getTagElement(MobCrystals.MOD_ID);
        if (tag == null) {
            tooltipComponents.add(Constants.EMPTY_CRYSTAL);
            return;
        }

        if (tag.contains("bound_entity")) {
            CompoundTag id = tag.getCompound("bound_entity");
            String displayName = id.getString("id");
            tooltipComponents.add(Constants.MOB_CRYSTAL_TOOLTIP);
            tooltipComponents.add(Component.translatable(displayName).withStyle(ChatFormatting.GREEN));
        } else {
            tooltipComponents.add(Constants.EMPTY_CRYSTAL);
        }
    }

    private boolean isValidMob(Mob mob) {
        if (mob.getMaxHealth() > Config.maxHP) {
            return false;
        }

        if (Config.useWhiteList && !checkMobWhiteList(mob)) {
            return false;
        }
        if (!Config.useWhiteList && checkMobBlackList(mob)) {
            return false;
        }
        return true;
    }

    private boolean checkMobBlackList(Entity mob) {
        Set<EntityType> invalidEntities = Config.invalidEntities;


        for(EntityType entity : invalidEntities) {
            if (entity.toString().equals(mob.getType().toString())) {
                return true;
            }
        }
        return false;
    }
    private boolean checkMobWhiteList(Entity mob) {
        Set<EntityType> invalidEntities = Config.validEntities;


        for(EntityType entity : invalidEntities) {
            if (entity.toString().equals(mob.getType().toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Config.maxStackSize;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack itemstack) {
        CompoundTag tag = itemstack.getTagElement(MobCrystals.MOD_ID);
        return tag != null;
    }
}
