package com.github.synth.lib.api.util.dsl

import net.minecraft.core.*
import net.minecraft.nbt.*
import net.minecraft.resources.*
import net.minecraft.world.item.*
import net.minecraft.world.level.block.*
import java.util.*
import kotlin.reflect.*


fun CompoundTag.putBlockPos(name: String, blockPos: Vec3i) {
    putIntArray(name, intArrayOf(blockPos.x, blockPos.y, blockPos.z))
}

fun CompoundTag.getBlockPos(name: String): Vec3i {
    val array = getIntArray(name)
    if (array.size != 3) return BlockPos.ZERO
    return BlockPos(array[0], array[1], array[2])
}



/**
 * Convert a generic map to nbt
 */
inline fun <reified K : Any, reified V : Any> Map<K, V>.toCompound(): CompoundTag {
    val tag = CompoundTag()
    tag.putInt("size", this.size)
    val store = CompoundTag()
    this.keys.forEachIndexed { i, key ->
        store.tryPut("key_$i", key)
        store.tryPut("value_$i", this[key]!!)
    }
    tag.put("store", store)
    return tag
}

inline fun <reified K : Any, reified V : Any> CompoundTag.putMap(name: String, map: Map<K, V>) {
    val compound = map.toCompound()
    this.put(name, compound)
}

/**
 * Gets a map with the given name
 */
inline fun <reified K : Any, reified V : Any> CompoundTag.getMap(name: String): MutableMap<K, V> {
    if (!this.contains(name)) return hashMapOf()
    return this.getCompound(name).toMap()
}

/**
 * Deserialize the map
 */
inline fun <reified K : Any, reified V : Any> CompoundTag.toMap(): MutableMap<K, V> {
    val map = HashMap<K, V>()
    val size = this.getInt("size")
    val store = this.getCompound("store")
    for (i in 0 until size) {
        val key = store.tryGet<K>("key_$i")
        val value = store.tryGet<V>("value_$i")
        if (key != null && value != null) map[key] = value
    }
    return map
}

operator fun CompoundTag.invoke(name: String): CompoundTag = this.getCompound(name)


/**
 * Convert a generic map to nbt
 */
inline fun <reified V : Any> List<V>.toCompound(): CompoundTag {
    val tag = CompoundTag()
    tag.putInt("size", this.size)
    val store = CompoundTag()
    this.forEachIndexed { i, key ->
        store.tryPut("value_$i", key)
    }
    tag.put("store", store)
    return tag
}

/**
 * Deserialize the map
 */
inline fun <reified V : Any> CompoundTag.toList(): MutableList<V> {
    val list = ArrayList<V>()
    val size = this.getInt("size")
    val store = this.getCompound("store")
    for (i in 0 until size) {
        val value = store.tryGet<V>("value_$i")
        if (value != null) list.add(value)
    }
    return list
}

/**
 * Attempts to serialize a lot of stufff
 */
fun <T : Any> CompoundTag.tryPut(name: String, value: T): Boolean {
    when (value) {
        is Enum<*> -> this.putEnumBasic(name, value)
        is BlockPos -> this.putBlockPos(name, value)
        is Int -> this.putInt(name, value)
        is Float -> this.putFloat(name, value)
        is Double -> this.putDouble(name, value)
        is Boolean -> this.putBoolean(name, value)
        is Byte -> this.putByte(name, value)
        is String -> this.putString(name, value)
        is ItemStack -> this.put(name, value.serializeNBT())
        is ResourceLocation -> {
            val subTag = CompoundTag()
            subTag.putString("namespace", value.namespace)
            subTag.putString("path", value.path)
            this.put(name, subTag)
        }
        is Block -> return this.tryPut(name, value.registryName!!)
        is UUID -> this.putUUID(name, value)
        is IntArray -> this.putIntArray(name, value)
        is ByteArray -> this.putByteArray(name, value)
        is LongArray -> this.putLongArray(name, value)
        is CompoundTag -> this.put(name, value)
        else -> {
            return false
        }
    }
//    debug { "Serialized ${value::class.simpleName} with name: $name and value: $value" }
    return true
}

inline fun <reified T : Any> CompoundTag.tryGet(name: String): T? = tryGet(name, T::class)

/**
 * Attempts to get the value
 */
fun <T : Any> CompoundTag.tryGet(name: String, classType: KClass<T>): T? {
    if (classType.java.isEnum) return classType.cast(getEnumBasic(name).getOrNull())
    return when (classType) {
        BlockPos::class -> classType.cast(this.getBlockPos(name))
        Int::class -> classType.cast(this.getInt(name))
        Float::class -> classType.cast(this.getFloat(name))
        Double::class -> classType.cast(this.getDouble(name))
        Boolean::class -> classType.cast(this.getBoolean(name))
        Byte::class -> classType.cast(this.getByte(name))
        String::class -> classType.cast(this.getString(name))
        UUID::class -> classType.cast(this.getUUID(name))
        ItemStack::class -> classType.cast(ItemStack.of(getCompound(name)))
        IntArray::class -> classType.cast(this.getIntArray(name))
        ByteArray::class -> classType.cast(this.getByteArray(name))
        LongArray::class -> classType.cast(this.getLongArray(name))
        CompoundTag::class -> classType.cast(this.getCompound(name))
        Block::class -> {
            var result: T? = null
            this.tryGet(name, ResourceLocation::class)?.let {
                val block = net.minecraft.core.Registry.BLOCK.get(it)
                result = classType.cast(block)
            }
            result
        }
        ResourceLocation::class -> {
            val subTag = this.getCompound(name)
            return classType.cast(ResourceLocation(subTag.getString("namespace"), subTag.getString("path")))
        }
        else -> null
    }
}




/**
 * Serialize a generic type of a enum
 */
fun CompoundTag.putEnumBasic(name: String, enum: Enum<*>): CompoundTag {
    this.putString("${name}_enum_name", enum.name)
    this.putString("${name}_enum_class", enum::class.java.name)
    return this
}

/**
 * Gets a generic type of enum
 */
@Suppress("UNCHECKED_CAST")
fun CompoundTag.getEnumBasic(name: String): Opt<Enum<*>> {
    val enumName = this.getString("${name}_enum_name")
    val clazzName = this.getString("${name}_enum_class")
    return try {
        val clazz = Class.forName(clazzName) as Class<out Enum<*>>
        val enumValue = java.lang.Enum.valueOf(clazz, enumName)
        Opt.ofNilable(enumValue)
    } catch (ex: Exception) {
        Opt.nil()
    }
}


fun <E : Enum<E>> CompoundTag.putEnum(name: String, enum: E): CompoundTag {
    this.putInt("${name}_enum", enum.ordinal)
    return this
}

/**
 * Gets the enum of the given name
 */
inline fun <reified E : Enum<E>> CompoundTag.getEnum(name: String): E {
    val contents = E::class.java.enumConstants
    val value = this.getInt("${name}_enum")
    return contents[value]
}
