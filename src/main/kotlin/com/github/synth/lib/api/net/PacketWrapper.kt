package com.github.synth.lib.api.net

import com.github.synth.lib.api.net.*
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkEvent

class PacketWrapper<T : Packet>(val id: Int, private val supplier: () -> T) {
    private val serverListeners: MutableList<(T, NetworkEvent.Context) -> Boolean> = ArrayList()
    private val clientListeners: MutableList<(T, NetworkEvent.Context) -> Boolean> = ArrayList()

    fun serverListener(listener: (T, NetworkEvent.Context) -> Boolean) =
        addListener(NetworkDirection.PLAY_TO_SERVER, listener)

    fun clientListener(listener: (T, NetworkEvent.Context) -> Boolean) =
        addListener(NetworkDirection.PLAY_TO_CLIENT, listener)

    fun removeClientListener(listener: (T, NetworkEvent.Context) -> Boolean) {
        clientListeners.remove(listener)
    }

    fun removeServerListener(listener: (T, NetworkEvent.Context) -> Boolean) {
        serverListeners.remove(listener)
    }

    operator fun invoke(configure: T.() -> Unit = {}): T {
        val packet = supplier()
        packet.apply(configure)
        return packet
    }

    fun invoke(packet: T, ctx: NetworkEvent.Context) {
        var handled = false
        ctx.enqueueWork {
            when (ctx.direction) {
                NetworkDirection.PLAY_TO_CLIENT -> {
                    for (listener in clientListeners) {
                        if (listener(packet, ctx)) {
                            handled = true
                            break
                        }
                    }
                }
                NetworkDirection.PLAY_TO_SERVER -> {
                    for (listener in serverListeners) {
                        if (listener(packet, ctx)) {
                            handled = true
                            break
                        }
                    }
                }
                else -> println("Attempted to handle login packet")

            }
            ctx.packetHandled = handled
        }
    }

    fun addListener(
        side: NetworkDirection,
        listener: (T, NetworkEvent.Context) -> Boolean
    ) {
        println("adding listener for ${side.name}, in packet ${this::class.simpleName}")
        when (side) {
            NetworkDirection.PLAY_TO_CLIENT -> clientListeners.add(listener)
            NetworkDirection.PLAY_TO_SERVER -> serverListeners.add(listener)
            else -> println("Attempted to add listener for unsupported side: ${side.name}")
        }
    }

}