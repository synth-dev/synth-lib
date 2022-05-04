package com.github.synth.lib.api.util.dsl

import com.github.synth.lib.api.multiblock.*
import com.github.synth.lib.api.multiblock.MultiBlockState.*
import com.github.synth.lib.api.multiblock.StructureBlockVariant.*
import com.github.synth.lib.api.multiblock.StructureBlockVariant.Corner
import com.github.synth.lib.api.multiblock.StructureBlockVariant.Side
import net.minecraft.core.*
import net.minecraft.core.Direction.*


/**
 * returns the next enum
 */
inline fun <reified T : Enum<T>> T.skip(skipBy: Int): T {
    val index = this.ordinal
    val values = T::class.java.enumConstants
    var next = (index + skipBy)
    if (next < 0) next = values.lastIndex
    else if (next > values.lastIndex) next = 0
    return values[next]
}

/**
 * Gets the next enum by skipping by 1
 */
inline val <reified T : Enum<T>> T.next get() = skip(1)

/**
 * Gets the next enum by skipping by 1
 */
inline val <reified T : Enum<T>> T.prev get() = skip(-1)

/**
 * Increment the enum
 */
inline operator fun <reified T : Enum<T>> T.inc(): T = next

/**
 * Decrement the enum
 */
inline operator fun <reified T : Enum<T>> T.dec(): T = prev


val StructureBlockVariant.multiBlockState: MultiBlockState
    get() = when (this) {
        Side -> MultiBlockState.Side
        Inner -> MultiBlockState.Unformed
        VerticalEdge -> MultiBlockState.VEdge
        HorizontalEdge -> MultiBlockState.HEdge
        Corner -> MultiBlockState.Corner
    }


val Direction.horizontal: Direction
    get() = when (this) {
        UP, DOWN -> NORTH
        else -> this
    }
