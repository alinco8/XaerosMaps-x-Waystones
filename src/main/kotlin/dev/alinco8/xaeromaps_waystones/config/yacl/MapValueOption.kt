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

class MapValueOption<T : Any>(
    private val mapOption: Option<MutableMap.MutableEntry<String, T>>,
    controlGetter: Function<Option<T>, ControllerBuilder<T>>
) : Option<T> {
    private val controller: Controller<T> = controlGetter.apply(this).build()
    private val stateManager: StateManager<T> = StateManager.createSimple<T>(
        mapOption.pendingValue().value,
        { mapOption.pendingValue().value },
        { v: T ->
            mapOption.requestSet(
                AbstractMap.SimpleEntry(mapOption.pendingValue().key, v)
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

    override fun controller(): Controller<T> {
        return controller
    }

    override fun stateManager(): StateManager<T> {
        return stateManager
    }

    @Deprecated("")
    override fun binding(): Binding<T> {
        if (stateManager is ProvidesBindingForDeprecation<*>) {
            return (stateManager as ProvidesBindingForDeprecation<T>).getBinding()
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

    override fun pendingValue(): T {
        return mapOption.pendingValue().value
    }

    override fun requestSet(value: T) {
        Validate.notNull<T>(value, "`value` cannot be null")

        mapOption.requestSet(
            AbstractMap.SimpleEntry<String, T>(mapOption.pendingValue().key, value)
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
        return mapOption.isPendingValueDefault()
    }

    override fun addEventListener(listener: OptionEventListener<T>) {
        mapOption.addEventListener({ o: Option<MutableMap.MutableEntry<String, T>>, e: OptionEventListener.Event ->
            listener.onEvent(
                this,
                e
            )
        })
    }

    @Deprecated("")
    override fun addListener(changedListener: BiConsumer<Option<T>, T>) {
        mapOption.addListener({ o: Option<MutableMap.MutableEntry<String, T>>, e: MutableMap.MutableEntry<String, T> ->
            changedListener.accept(
                this,
                e.value
            )
        })
    }
}

