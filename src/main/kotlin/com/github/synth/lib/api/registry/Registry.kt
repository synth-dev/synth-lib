package com.github.synth.lib.api.registry

import com.google.common.collect.*
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.eventbus.api.*
import net.minecraftforge.fml.event.*
import net.minecraftforge.fml.loading.*
import net.minecraftforge.registries.*
import org.lwjgl.system.CallbackI.*
import java.util.*
import java.util.function.*
import kotlin.properties.*
import kotlin.reflect.*
import kotlin.reflect.full.*

abstract class Registry<B : IForgeRegistryEntry<B>>(private val registry: IForgeRegistry<B>) : IRegistry {
    private lateinit var deferredRegister: DeferredRegister<B>
    private val registers: Queue<Pair<String, () -> B>> = Queues.newArrayDeque()
    private val objects: MutableMap<String, RegistryObject<B>> = HashMap()

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
    protected fun <T : B> register(
        name: String, supplier: () -> T
    ): ReadOnlyProperty<Any?, T> {
        registers.add(name to supplier)
        return object : ReadOnlyProperty<Any?, T>, Supplier<T>, () -> T {
            override fun get(): T = objects[name]!!.get() as T
            override fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
            override fun invoke(): T = get()
        }
    }
}