package com.uavwaffle.mobcrystals.item.custom;

import com.uavwaffle.mobcrystals.MobCrystals;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MobCrystal extends Item {
//    private List<CompoundTag> mobStorage;

//    CompoundTag storedEntityTag = getDefaultInstance().getOrCreateTagElement(MobCrystals.MOD_ID);

    public MobCrystal(Properties properties) {
        super(properties);
//        this.mobStorage = new ArrayList<>();
//        this.storedEntityTag = getDefaultInstance().getOrCreateTagElement(MobCrystals.MOD_ID);
    }


    private void saveDataToCrystal(Mob mob, ItemStack itemstack, @NotNull Player player, @NotNull InteractionHand usedHand) {
        /* temp close
        CompoundTag entityTag = new CompoundTag();
        mob.save(entityTag);
        mobStorage.add(entityTag);

         */

        ItemStack copy = itemstack.copy();
        copy.setCount(1);
        CompoundTag storedEntityTag = new CompoundTag();//copy.getOrCreateTagElement(MobCrystals.MOD_ID);
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

//        ItemStack copy = itemstack.copy();
//        copy.setCount(1);
//        CompoundTag entityTag = copy.getOrCreateTag();
//        mob.save(entityTag);
//        CompoundTag tag = copy.getOrCreateTag();
//        tag.put("boundentity", entityTag);

//        System.out.println("entityTag: " + entityTag.getAsString());
//        storedEntityTag.put("stored_mob", entityTag);
//        System.out.println("storedEntityTag: " + storedEntityTag);

//        System.out.println("entityTag save: " + entityTag.getAsString());
//        CompoundTag tag = itemstack.getOrCreateTag();
//        tag.put("boundentity", entityTag);
//        System.out.println("tag save: " + tag.getAsString());

//        CompoundTag mobTag = itemstack.getOrCreateTag().getCompound("boundentity");
//        System.out.println("mobTag save: " + mobTag.getAsString());

//        if (tag.contains("boundentity")) {
//            tag.put("boundentity", entityTag);
//            System.out.println("tag save: " + tag.getAsString());

//            int id = tag.getInt("BoundEntity");
//            Entity boundEntity = level.getEntity(id);
//            if (boundEntity == null) {
//                tag.remove("BoundEntity");
//                tag.remove("BoundType");
//            }
//        }

    }

    private void loadDataFromCrystal(Player player, CompoundTag pTag) {

    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = player.getItemInHand(pUsedHand);
//        if(mobStorage.isEmpty()) {
//            return InteractionResultHolder.fail(itemstack);
//        }
        if(level.isClientSide()) {
            return InteractionResultHolder.success(itemstack);
        }
        /* temp close

        EntityType.create(mobStorage.get(mobStorage.size() - 1), level).ifPresent((entity) -> {
            if (entity instanceof TamableAnimal) {
                ((TamableAnimal)entity).setOwnerUUID(player.getUUID());
            }

            entity.setPos(player.getX(), player.getY() + (double)0.7F, player.getZ());
            ((ServerLevel)level).addWithUUID(entity);
        });

         */

        itemstack.getTags().forEach(tag -> {
            System.out.println("me taag: " + tag);
        });

//        CompoundTag mobTag = itemstack.getOrCreateTag().getCompound("boundentity");
        CompoundTag tag = itemstack.getTagElement(MobCrystals.MOD_ID);
        if (tag == null) {
            System.out.println("NUUUUULLLL");
            return InteractionResultHolder.fail(itemstack);
        }
        CompoundTag mobTag = tag.getCompound("boundentity");


        System.out.println("mobTag: " + mobTag.getAsString());

        EntityType.create(mobTag,level).ifPresent((entity) -> {
            if (entity instanceof TamableAnimal) {
                ((TamableAnimal)entity).setOwnerUUID(player.getUUID());
            }

            entity.setPos(player.getX(), player.getY() + (double)0.7F, player.getZ());
            ((ServerLevel)level).addWithUUID(entity);
        });


        return InteractionResultHolder.success(itemstack);
    }

    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {
//        mobStorage.add(pInteractionTarget);
        if (!(pInteractionTarget instanceof Mob mobToSave)) {
            return InteractionResult.FAIL;
        }
        if (!pPlayer.level().isClientSide()) {
            saveDataToCrystal(mobToSave, pStack, pPlayer, pUsedHand);
        }
        pInteractionTarget.discard();
        return InteractionResult.SUCCESS;
    }
}
