package com.heledron.camera_experiment.client.utilities

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

object ParticleRenderer {
    fun drawLine(level: Level, start: Vec3, vector: Vec3) {
        val interval = .1
        val length = vector.length() / interval
        val stride = vector.normalize().scale(interval)

        var current = start
        for (i in 0 until length.toInt()) {
            level.addParticle(ParticleTypes.BUBBLE, current.x, current.y, current.z, 0.0, 0.0, 0.0)
            current = current.add(stride)

        }
    }

    fun drawLineBetween(level: Level, a: Vec3, b: Vec3) {
        drawLine(level, a, b.subtract(a))
    }
}