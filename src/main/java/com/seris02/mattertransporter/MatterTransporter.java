package com.seris02.mattertransporter;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(MatterTransporter.MODID)
@EventBusSubscriber(modid = MatterTransporter.MODID, bus = Bus.MOD)
public class MatterTransporter {
	public static final  String MODID = "mattertransporter";
	public static final String VERSION = "v1.1";

	public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> VERSION, VERSION::equals, VERSION::equals);
	
	public MatterTransporter() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		Content.register(modBus);
        modBus.addListener(this::addCreative);
		
		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

    // Add the example block item to the building blocks tab
	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
			event.accept(Content.MATTER_TRANSPORTER);
			event.accept(Content.NETHERITE_MATTER_TRANSPORTER);
	}

}
