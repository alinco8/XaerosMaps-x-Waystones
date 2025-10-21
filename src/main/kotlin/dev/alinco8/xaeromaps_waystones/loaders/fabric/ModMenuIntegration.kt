//? if fabric {
package dev.alinco8.xaeromaps_waystones.loaders.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.alinco8.xaeromaps_waystones.config.ConfigScreen

class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory() =
        ConfigScreenFactory(ConfigScreen::getConfigScreen)
}
//?}