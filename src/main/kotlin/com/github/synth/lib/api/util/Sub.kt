package com.github.synth.lib.api.util

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Sub(val dist: DistSide = DistSide.Both)
