package dev.alinco8.xmxw.mixin

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class MixinPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String) {}
    override fun getRefMapperConfig(): String? = null

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        val paths = mixinClassName.split(".").dropWhile { it != "mixin" }.drop(1)
        if (paths.firstOrNull() != "compat") return true
        val modId = paths.drop(1).firstOrNull() ?: return true

        //? if fabric {
        /*return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded(modId)
        *///? } else if neoforge {
        return net.neoforged.fml.loading.LoadingModList.get().mods.any { it.modId == modId }
        //? } else if forge {
        /*return net.minecraftforge.fml.loading.LoadingModList.get().mods.any { it.modId == modId }
        *///? }
    }

    override fun acceptTargets(myTargets: MutableSet<String>, otherTargets: MutableSet<String>) {}
    override fun getMixins(): MutableList<String>? = null
    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
    }

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
    }
}
