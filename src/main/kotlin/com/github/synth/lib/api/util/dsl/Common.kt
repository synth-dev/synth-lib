package com.github.synth.lib.api.util.dsl

import com.github.synth.lib.api.multiblock.*
import com.github.synth.lib.api.multiblock.StructureBlockVariant.*
import net.minecraft.core.*
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.*
import net.minecraft.world.level.block.state.*
import net.minecraft.world.phys.*
import net.minecraft.world.phys.shapes.*


/**
 * This is used for easy block entity registration
 */
inline fun <reified T : BlockEntity> tileTypeOf(
    block: Block, crossinline supplier: (Pair<BlockPos, BlockState>) -> T,
): BlockEntityType<T> {
    return BlockEntityType.Builder.of({ pos, state -> supplier(pos to state) }, block).build(null)
}

/**
 * Attempts to create a nicely formmated to string
 */
val Any.str: String
    get() {
        var string = this.toString().replace(this::class.java.simpleName, "")
        if (string.startsWith("[")) string = string.substring(1)
        else if (string.startsWith("(")) string = string.substring(1)
        else if (string.startsWith("{")) string = string.substring(1)
        if (string.endsWith("]")) string = string.substring(0, string.lastIndex)
        else if (string.endsWith(")")) string = string.substring(0, string.lastIndex)
        else if (string.endsWith("}")) string = string.substring(0, string.lastIndex)
        return "[$string]"
    }


fun VoxelShape.join(other: VoxelShape, op: BooleanOp): VoxelShape {
    return Shapes.join(this, other, op)
}


fun BlockPos.offset(direction: Direction): BlockPos {
    return offset(direction.normal)
}

fun Vec3i.min(): Vec3i = Vec3i(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)
fun Vec3i.max(): Vec3i = Vec3i(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)

val Vec3i.bp: BlockPos get() = if (this is BlockPos) this else BlockPos(this.x, this.y, this.z)
val Vec3i.corner: Vec3 get() = Vec3.atLowerCornerOf(this)
val Vec3i.center: Vec3 get() = Vec3.atCenterOf(this)

val Vec3i.isMin: Boolean get() = this.x == Int.MIN_VALUE && this.y == Int.MIN_VALUE && this.z == Int.MIN_VALUE
val Vec3i.isMax: Boolean get() = this.x == Int.MAX_VALUE && this.y == Int.MAX_VALUE && this.z == Int.MAX_VALUE
