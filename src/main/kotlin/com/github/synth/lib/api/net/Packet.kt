package com.github.synth.lib.api.net

import net.minecraft.network.FriendlyByteBuf

abstract class Packet {
    abstract fun write(buffer: FriendlyByteBuf)
    abstract fun read(buffer: FriendlyByteBuf)
}
