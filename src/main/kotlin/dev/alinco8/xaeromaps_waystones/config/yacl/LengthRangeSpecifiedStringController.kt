package dev.alinco8.xaeromaps_waystones.config.yacl

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.gui.controllers.string.IStringController

data class LengthRangeSpecifiedStringController(
    private val option: Option<String>,
    private val minLength: Int = 0,
    private val maxLength: Int,
) : IStringController<String> {
    companion object {
        fun create(minLength: Int, maxLength: Int) = { option: Option<String> ->
            LengthRangeSpecifiedStringController(option, minLength, maxLength)
        }
    }

    override fun getString() = option.pendingValue()

    override fun setFromString(value: String) {
        option.requestSet(value)
    }

    override fun isInputValid(input: String): Boolean {
        return input.length in minLength..maxLength
    }

    override fun option() = option
}