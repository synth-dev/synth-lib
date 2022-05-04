package com.github.synth.lib.api.registry

import net.minecraft.client.*
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.client.*
import net.minecraftforge.eventbus.api.*
import net.minecraftforge.fml.event.lifecycle.*
import thedarkcolour.kotlinforforge.forge.*
import java.util.function.*
import kotlin.collections.set
import kotlin.properties.*
import kotlin.reflect.*

abstract class KeyRegistry : IRegistry {
    private val mappings = HashMap<String, KeyMapping>()
    private val delegated = HashMap<String, Pair<String, Int>>()

    /**
     * This is used delegate registration using properties
     */
    protected fun register(
        name: String, category: String, keycode: Int
    ): ReadOnlyProperty<Any?, KeyMapping> {
        delegated[name] = category to keycode
        return object : ReadOnlyProperty<Any?, KeyMapping>, Supplier<KeyMapping>, () -> KeyMapping {
            override fun get(): KeyMapping = mappings[name]!!
            override fun getValue(thisRef: Any?, property: KProperty<*>): KeyMapping = get()

            override fun invoke(): KeyMapping = get()
        }
    }

    override fun register(modId: String, modBus: IEventBus, forgeBus: IEventBus) {
        runWhenOn(Dist.CLIENT) {
            delegated.forEach { (name, data) ->
                val mapping = KeyMapping("${modId}.$name", data.second, data.first)
                mappings[name] = (mapping)
            }

        }
        modBus.addListener<FMLClientSetupEvent> {
            mappings.forEach {
                ClientRegistry.registerKeyBinding(it.value)
            }
        }
    }
}