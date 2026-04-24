package dev.alinco8.xmxw

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.network.chat.Component

internal object XMXWToasts {
    //? if <=1.20.1 {
    /*private val MODDED_WAYPOINT_SET_NOT_EDITABLE = SystemToast.SystemToastIds.PERIODIC_NOTIFICATION
    private val MODDED_WAYPOINT_NOT_EDITABLE = SystemToast.SystemToastIds.PERIODIC_NOTIFICATION
    private val UPDATE_AVAILABLE = SystemToast.SystemToastIds.PERIODIC_NOTIFICATION
    private val ALL_SETS_DISABLED = SystemToast.SystemToastIds.PERIODIC_NOTIFICATION
    *///? } else {
    private val MODDED_WAYPOINT_SET_NOT_EDITABLE = SystemToast.SystemToastId()
    private val MODDED_WAYPOINT_NOT_EDITABLE = SystemToast.SystemToastId()
    private val UPDATE_AVAILABLE = SystemToast.SystemToastId()
    private val ALL_SETS_DISABLED = SystemToast.SystemToastId()
    //? }

    private fun createToastComponent(id: String, vararg args: Any) =
        Component.translatable("xmxw.toast.$id", *args)

    private fun showToast(
        //? if <=1.20.1 {
        /*id: SystemToast.SystemToastIds,
        *///? } else {
        id: SystemToast.SystemToastId,
        //? }
        message: Component,
    ) {
        //? if >=1.21.4 {
        /*val toasts = Minecraft.getInstance().toastManager
        *///? } else {
        val toasts = Minecraft.getInstance().toasts
        //? }

        toasts.addToast(
            SystemToast.multiline(
                Minecraft.getInstance(),
                id,
                Component.literal("Xaero's Maps x Waystones"),
                message
            )
        )
    }

    @JvmStatic
    fun moddedWaypointSetNotEditable() {
        showToast(
            MODDED_WAYPOINT_SET_NOT_EDITABLE,
            createToastComponent("modded_waypoint_set_not_editable")
        )
    }

    @JvmStatic
    fun moddedWaypointNotEditable() {
        showToast(
            MODDED_WAYPOINT_NOT_EDITABLE,
            createToastComponent("modded_waypoint_not_editable")
        )
    }

    @JvmStatic
    fun updateAvailable(newVersion: String, currentVersion: String) {
        showToast(
            UPDATE_AVAILABLE,
            createToastComponent("update_available", newVersion, currentVersion)
        )
    }

    @JvmStatic
    fun allSetsDisabled() {
        showToast(
            ALL_SETS_DISABLED,
            createToastComponent("all_sets_disabled")
        )
    }
}
