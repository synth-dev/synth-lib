package com.github.synth.lib.api.util.dsl

import net.minecraft.network.chat.*
import kotlin.reflect.*

/**
 * Computes the resource location for the given class
 */
internal inline fun <reified T : Any> computeName(underscore: Boolean = true, capitialized: Boolean = false): String =
    computeName(T::class, underscore, capitialized)

/**
 * Computes the resource location for the given class
 */
internal inline fun <reified T : Any> computeComp(overrideValue: String? = null, baseIn: String? = null, modID: String): Component =
    computeComp(T::class, overrideValue, baseIn, modID)

internal fun String.spaced(): String = this.replace("(.)([A-Z])".toRegex(), "$1 $2")


/**
 * Computes the resource location for the given class
 */
internal fun <T : Any> computeName(clazz: KClass<T>, underscore: Boolean = true, capitalized: Boolean = false): String {
    val name = clazz.simpleName!!.replace("\\d+".toRegex(), "").replace("Tile", "").replace("Model", "").replace("Block", "").replace("Item", "")
        .replace("(.)([A-Z])".toRegex(), if (underscore) "$1_$2" else "$1 $2")
    return if (!capitalized) name.lowercase() else name
}

internal fun <T : Any> computeComp(clazz: KClass<T>, overrideValue: String? = null, baseIn: String? = null, modID: String): Component {
    if (overrideValue != null) return TextComponent(overrideValue)
    val clsName = clazz.simpleName ?: return TextComponent("Invalid name component for ${clazz::class.simpleName}")
    val name = computeName(clazz)
    val base: String = baseIn ?: if (clsName.endsWith("Tile") || clsName.endsWith("Block")) "block"
    else if (clsName.endsWith("Item")) "item"
    else if (clsName.endsWith("Container")) "container"
    else if (clsName.endsWith("Screen")) "screen"
    else if (clsName.endsWith("Tab")) "tab"
    else "misc"
    return TranslatableComponent("${base}.${modID}.${name}")
}
