package com.uavwaffle.mobcrystals.config;

import com.uavwaffle.mobcrystals.MobCrystals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = MobCrystals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue WHITE_LIST = BUILDER.comment("Toggles between using the whitelist or blacklist").define("doWhiteList", false);

    private static final ForgeConfigSpec.IntValue MAX_MOB_HP = BUILDER.comment("Max hp of a mob before it can't be sealed away").defineInRange("maxHP", 20, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue MAX_STACK_SIZE = BUILDER.comment("Max stack size for the Mob Crystals").defineInRange("stackSize", 16, 1, 64);

//    public static final ForgeConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number").define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
//    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> VALID_MOBS = BUILDER.comment("A list of mobs that can be sealed away").defineListAllowEmpty("entityWhiteList", List.of("minecraft:pig", "minecraft:cow", "minecraft:sheep"), Config::validateEntityName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> INVALID_MOBS = BUILDER.comment("A list of mobs that can't be sealed away").defineListAllowEmpty("entityBlackList", List.of("minecraft:wither", "minecraft:elder_guardian"), Config::validateEntityName);


    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean useWhiteList;
    public static int maxHP;
    public static int maxStackSize;
//    public static Set<Item> items;
public static Set<EntityType> validEntities;
    public static Set<EntityType> invalidEntities;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    private static boolean validateEntityName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ENTITY_TYPES.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        useWhiteList = WHITE_LIST.get();
        maxHP = MAX_MOB_HP.get();
        maxStackSize = MAX_STACK_SIZE.get();

        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream().map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).collect(Collectors.toSet());
        validEntities = VALID_MOBS.get().stream().map(entityName -> ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityName))).collect(Collectors.toSet());
        invalidEntities = INVALID_MOBS.get().stream().map(entityName -> ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityName))).collect(Collectors.toSet());

    }
}
