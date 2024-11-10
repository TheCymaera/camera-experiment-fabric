package com.heledron.camera_experiment.client.utilities

fun toDegrees(float: Float): Float {
    return Math.toDegrees(float.toDouble()).toFloat()
}

fun toRadians(float: Float): Float {
    return Math.toRadians(float.toDouble()).toFloat()
}