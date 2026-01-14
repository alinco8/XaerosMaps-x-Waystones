//? if fabric {
package dev.alinco8.xmxw.platform.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.alinco8.xmxw.config.ConfigScreen

class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*>? = { parent ->
        ConfigScreen.getConfigScreen(parent)
    }
}
//? }
