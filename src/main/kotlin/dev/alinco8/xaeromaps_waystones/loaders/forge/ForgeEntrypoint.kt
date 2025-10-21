//? if forge {
/*package dev.alinco8.xaeromaps_waystones.loaders.forge

import dev.alinco8.xaeromaps_waystones.XaerosMapsWaystonesMod
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(XaerosMapsWaystonesMod.MOD_ID)
class ForgeEntrypoint {
    init {
        XaerosMapsWaystonesMod.initialize()

        FMLJavaModLoadingContext.get().modEventBus.register(this)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        ForgeNetworking.register()
    }
}
*///?}
