package com.seris02.mattertransporter;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Content {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MatterTransporter.MODID);
	public static final RegistryObject<Item> MATTER_TRANSPORTER = ITEMS.register("matter_transporter", () -> new ItemMatterTransporter(itemProp(50), 1));
	public static final RegistryObject<Item> NETHERITE_MATTER_TRANSPORTER = ITEMS.register("netherite_matter_transporter", () -> new ItemMatterTransporter(itemProp(300), 0));

	public static void register(IEventBus eventbus) {
		ITEMS.register(eventbus);
	}
	
	private static final Item.Properties itemProp(int durability) {
		return new Item.Properties().durability(durability);
	}
	
	
}
