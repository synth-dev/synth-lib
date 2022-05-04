package com.github.synth.lib.api.registry

import com.github.synth.lib.api.util.*
import com.github.synth.lib.api.util.DistSide.*
import net.minecraftforge.api.distmarker.*
import net.minecraftforge.eventbus.api.*
import net.minecraftforge.fml.event.*
import net.minecraftforge.fml.loading.*
import kotlin.reflect.*
import kotlin.reflect.full.*

abstract class ListenerRegistry : IRegistry {
    private fun registerListeners(modBus: IEventBus, forgeBus: IEventBus) {
        for (member in this::class.functions) {
            if (member.visibility != KVisibility.PUBLIC) continue
            if (member.hasAnnotation<Sub>()) {
                val side = member.findAnnotation<Sub>()?.dist
                if (side == Client && FMLEnvironment.dist != Dist.CLIENT) continue
                if (side == Server && FMLEnvironment.dist != Dist.DEDICATED_SERVER) continue
                for (param in member.parameters) {
                    val type = param.type.classifier as KClass<*>
                    if (type.isSubclassOf(Event::class)) {
                        val modType: Class<out Event> = type.java as Class<out Event>
                        if (type.isSubclassOf(IModBusEvent::class)) modBus.addListener(
                            EventPriority.LOWEST,
                            true,
                            modType
                        ) {
                            member.call(this, it)
                        }
                        else forgeBus.addListener(EventPriority.LOWEST, true, modType) {
                            member.call(this, it)
                        }
                        continue
                    }
                }
            }
        }
    }
}

