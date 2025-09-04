package dev.alinco8.xaeromaps_waystones.config.yacl

import com.google.common.collect.ImmutableSet
import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.impl.ProvidesBindingForDeprecation
import net.minecraft.network.chat.Component
import org.apache.commons.lang3.Validate
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function

@Suppress("UnstableApiUsage")
class MapEntryOption<T>(
    private val mapOption: Option<MutableMap.MutableEntry<String, T>>,
    controlGetter: Function<Option<String>, ControllerBuilder<String>>
) : Option<String> {
    private val controller: Controller<String> = controlGetter.apply(this).build()
    private val stateManager: StateManager<String> = StateManager.createSimple<String>(
        mapOption.pendingValue().key,
        { mapOption.pendingValue().key },
        { k: String ->
            mapOption.requestSet(
                AbstractMap.SimpleEntry<String, T>(k, mapOption.pendingValue().value)
            )
        }
    )

    override fun name(): Component {
        return Component.empty()
    }

    override fun description(): OptionDescription {
        return mapOption.description()
    }

    @Deprecated("")
    override fun tooltip(): Component {
        return mapOption.tooltip()
    }

    override fun controller(): Controller<String> {
        return controller
    }

    override fun stateManager(): StateManager<String> {
        return stateManager
    }

    @Deprecated("")
    override fun binding(): Binding<String> {
        if (stateManager is ProvidesBindingForDeprecation<*>) {
            return (stateManager as ProvidesBindingForDeprecation<String>).getBinding()
        }
        throw UnsupportedOperationException(
            "Binding is not available for this option - using a new state manager which does not directly expose the binding as it may not have one."
        )
    }

    override fun available(): Boolean {
        return mapOption.available()
    }

    override fun setAvailable(available: Boolean) {
        mapOption.setAvailable(available)
    }

    override fun flags(): ImmutableSet<OptionFlag> {
        return mapOption.flags()
    }

    override fun changed(): Boolean {
        return mapOption.changed()
    }

    override fun pendingValue(): String {
        return mapOption.pendingValue().key
    }

    override fun requestSet(value: String) {
        Validate.notNull<String>(value, "`value` cannot be null")

        mapOption.requestSet(
            AbstractMap.SimpleEntry<String, T>(value, mapOption.pendingValue().value)
        )
    }

    override fun applyValue(): Boolean {
        return mapOption.applyValue()
    }

    override fun forgetPendingValue() {
        mapOption.forgetPendingValue()
    }

    override fun requestSetDefault() {
        mapOption.requestSetDefault()
    }

    override fun isPendingValueDefault(): Boolean {
        return mapOption.isPendingValueDefault
    }

    override fun addEventListener(listener: OptionEventListener<String>) {
        mapOption.addEventListener { _: Option<MutableMap.MutableEntry<String, T>>, e: OptionEventListener.Event ->
            listener.onEvent(
                this,
                e
            )
        }
    }

    @Deprecated("")
    @Suppress("DEPRECATION")
    override fun addListener(changedListener: BiConsumer<Option<String>, String>) {
        mapOption.addListener { _: Option<MutableMap.MutableEntry<String, T>>, e: MutableMap.MutableEntry<String, T> ->
            changedListener.accept(
                this,
                e.key
            )
        }
    }
}
