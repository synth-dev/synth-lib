package com.github.synth.lib.api.multiblock

import net.minecraft.util.*
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty

enum class MultiBlockState : StringRepresentable {
    Formed, Unformed, Side, VEdge, HEdge, Corner;

    override fun getSerializedName(): String = name.lowercase()

    companion object {
        val State: EnumProperty<MultiBlockState> = EnumProperty.create("state", MultiBlockState::class.java)
    }
}