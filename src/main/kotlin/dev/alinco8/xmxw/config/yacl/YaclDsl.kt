package dev.alinco8.xmxw.config.yacl

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import net.minecraft.network.chat.Component

@DslMarker
internal annotation class YaclDsl

internal class I18n(
    private val modId: String,
) {
    private fun key(parts: List<String>) = parts.joinToString(".")

    fun config(path: String): Component = Component.translatable(key(listOf(modId, "config", path)))

    fun category(
        categoryId: String,
        path: String,
    ): Component =
        Component.translatable(key(listOf(modId, "config", "categories", categoryId, path)))

    fun group(
        categoryId: String,
        groupId: String,
        path: String,
    ): Component =
        Component.translatable(
            key(
                listOf(
                    modId,
                    "config",
                    "categories",
                    categoryId,
                    "groups",
                    groupId,
                    path,
                ),
            ),
        )

    fun option(
        categoryId: String,
        groupId: String?,
        optionName: String,
        path: String,
        vararg args: Any,
    ): Component =
        Component.translatable(
            key(
                buildList {
                    add(modId)
                    add("config")
                    add("categories")
                    add(categoryId)
                    if (groupId != null) {
                        add("groups")
                        add(groupId)
                    }
                    add("options")
                    add(optionName)
                    add(path)
                },
            ),
            *args,
        )
}

@YaclDsl
internal class ScreenScope(
    private val i18n: I18n,
    val builder: YetAnotherConfigLib.Builder,
) {
    fun t(path: String) = i18n.config(path)

    fun category(
        categoryId: String,
        block: CategoryScope.() -> Unit,
    ) {
        val catBuilder = ConfigCategory.createBuilder()
        CategoryScope(i18n, categoryId, catBuilder).apply(block)
        builder.category(catBuilder.build())
    }
}

@YaclDsl
internal class CategoryScope(
    private val i18n: I18n,
    private val categoryId: String,
    val builder: ConfigCategory.Builder,
) {
    fun t(path: String) = i18n.category(categoryId, path)

    fun group(
        groupId: String,
        block: GroupScope.() -> Unit,
    ) {
        val grpBuilder = OptionGroup.createBuilder()
        GroupScope(i18n, categoryId, groupId, grpBuilder).apply(block)
        builder.group(grpBuilder.build())
    }

    fun <T> option(
        optionName: String,
        block: OptionScope<T>.() -> Unit,
    ) {
        val optBuilder = Option.createBuilder<T>()
        OptionScope(i18n, categoryId, null, optionName, optBuilder).apply(block)
        builder.option(optBuilder.build())
    }
}

@YaclDsl
internal class GroupScope(
    val i18n: I18n,
    val categoryId: String,
    val groupId: String,
    val builder: OptionGroup.Builder,
) {
    fun t(path: String) = i18n.group(categoryId, groupId, path)

    fun <T> option(
        optionName: String,
        block: OptionScope<T>.() -> Unit,
    ) {
        val optBuilder = Option.createBuilder<T>()
        OptionScope(i18n, categoryId, groupId, optionName, optBuilder).apply(block)
        builder.option(optBuilder.build())
    }


    fun name(component: Component) {
        builder.name(component)
    }
}

@YaclDsl
internal class OptionScope<T>(
    val i18n: I18n,
    val categoryId: String,
    val groupId: String?,
    val optionName: String,
    val builder: Option.Builder<T>,
) {
    fun t(path: String) = i18n.option(categoryId, groupId, optionName, path)

    fun name(component: Component) {
        builder.name(component)
    }
}

internal fun configScreen(
    modId: String,
    block: ScreenScope.() -> Unit,
): YetAnotherConfigLib {
    val i18n = I18n(modId)
    val b = YetAnotherConfigLib.createBuilder()
    ScreenScope(i18n, b).apply(block)
    return b.build()
}
