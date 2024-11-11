package com.heledron.camera_experiment.client

import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.Entity

fun castEntityAsClientPlayer(entity: Entity): LocalPlayer? {
    val player = Minecraft.getInstance().player ?: return null
    if (entity.uuid.equals(player.uuid)) return player
    return null
}

fun <T>uncheckedCast(entity: Any): T {
    @Suppress("UNCHECKED_CAST")
    return entity as T
}

fun toDegrees(float: Float): Float {
    return Math.toDegrees(float.toDouble()).toFloat()
}

fun toRadians(float: Float): Float {
    return Math.toRadians(float.toDouble()).toFloat()
}