package com.github.synth.lib.api.registry

import net.minecraftforge.eventbus.api.*
import thedarkcolour.kotlinforforge.forge.*
import kotlin.reflect.full.*

interface IRegistry {

    /**
     * A simple method that is passed around the synth api to register everything
     */
    fun register(modId: String, modBus: IEventBus, forgeBus: IEventBus){}

    /**
     * Register all instances within a class, useful for objects with multi child registry objects
     */
    fun registerAll(
        modID: String, modBus: IEventBus, forgeBus: IEventBus,
    ) {
        for (child in this::class.nestedClasses) {
            if (child.isSubclassOf(IRegistry::class)) {
                val instance = child.objectInstance ?: continue
                if (instance is IRegistry) {
                    instance.register(modID, modBus, forgeBus)
                }
            }
        }
    }
}