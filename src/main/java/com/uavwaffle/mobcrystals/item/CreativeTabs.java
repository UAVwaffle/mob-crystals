package com.uavwaffle.mobcrystals.item;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

public class CreativeTabs {

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) // for adding things to vanilla creative tabs
            event.accept(ModItems.MOB_CRYSTAL);
    }
}
