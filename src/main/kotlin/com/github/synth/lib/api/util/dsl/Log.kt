package com.github.synth.lib.api.util.dsl

import com.github.synth.lib.*
import org.apache.logging.log4j.*

internal object Log {
    private val logger = LogManager.getLogger(SynthLib.ModId)
    fun info(supplier: String) = logger.info(supplier)
    fun warn(supplier: String) = logger.warn(supplier)
    fun debug(supplier: String) = logger.debug(supplier)
    fun error(supplier: String) = logger.error(supplier)
    fun trace(supplier: String) = logger.trace(supplier)
}

internal inline fun info(supplier: () -> String) = Log.info(supplier())
internal inline fun warn(supplier: () -> String) = Log.warn(supplier())
internal inline fun debug(supplier: () -> String) = Log.debug(supplier())
internal inline fun error(supplier: () -> String) = Log.error(supplier())
internal inline fun trace(supplier: () -> String) = Log.trace(supplier())

