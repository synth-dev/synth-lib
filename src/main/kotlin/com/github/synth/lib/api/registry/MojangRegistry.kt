package com.github.synth.lib.api.registry

import com.google.common.collect.*
import net.minecraft.resources.*
import net.minecraftforge.eventbus.api.*
import net.minecraftforge.registries.*
import java.util.*
import java.util.function.*
import kotlin.properties.*
import kotlin.reflect.*

abstract class MojangRegistry<R : net.minecraft.core.Registry<T>, T>(private val registry: ResourceKey<R>) : IRegistry {
    private lateinit var deferredRegister: DeferredRegister<T>
    private val registers: Queue<Pair<String, () -> T>> = Queues.newArrayDeque()
    private val objects: MutableMap<String, RegistryObject<T>> = HashMap()

    /**
     * A simple method that is passed around the synth api to register everything
     */
    override fun register(modId: String, modBus: IEventBus, forgeBus: IEventBus) {
        deferredRegister = DeferredRegister.create(registry, modId)
        while (registers.peek() != null) {
            val data = registers.remove()
            objects[data.first] = deferredRegister.register(data.first, data.second)
        }
        deferredRegister.register(modBus)
    }


    /**
     * This is used delegate registration using properties
     */
    protected fun <B : T> register(
        name: String, supplier: () -> B
    ): ReadOnlyProperty<Any?, B> {
        registers.add(name to supplier)
        return object : ReadOnlyProperty<Any?, B>, Supplier<B>, () -> B {
            override fun get(): B = objects[name]!!.get() as B

            override fun getValue(thisRef: Any?, property: KProperty<*>): B = get()

            override fun invoke(): B = get()
        }
    }
}