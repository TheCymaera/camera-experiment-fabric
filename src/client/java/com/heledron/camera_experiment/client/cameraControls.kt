package com.heledron.camera_experiment.client

import net.minecraft.world.entity.player.Player
import org.joml.Quaternionf
import org.joml.Vector3f


typealias CameraControls = (
    player: Player,
    moveVertical: Float,
    moveHorizontal: Float
) -> Unit


val UNFIXED_CAMERA_CONTROLS: CameraControls = { player, moveVertical, moveHorizontal ->
    val orientation = player.getCameraOrientation()

    if (player.isShiftKeyDown) {
        orientation.rotateZ(-moveVertical)
    } else {
        orientation.rotateY(-moveHorizontal)
        orientation.rotateX(-moveVertical)
    }

    player.setCameraOrientation(orientation)
}

var isAdjustingPivot = false

val PIVOTED_CAMERA_CONTROLS: CameraControls = Controls@{ player, moveVertical, moveHorizontal ->
    val orientation = player.getCameraOrientation()
    val pivot = player.getCameraPivot()


    isAdjustingPivot = player.isShiftKeyDown
    if (isAdjustingPivot) {
        orientation.rotateZ(-moveVertical -moveHorizontal)
        player.setCameraPivot(orientation)
        player.setCameraOrientation(orientation)

        return@Controls
    }

    var relative = Quaternionf(pivot).invert().mul(orientation)
    val euler = relative.getEulerAnglesYXZ(Vector3f())

    // rotate
    euler.x -= moveVertical
    euler.y -= moveHorizontal

    // clamp
    val clampX = Math.PI.toFloat() / 2 - .001f
    val clampY = Float.MAX_VALUE // Math.PI.toFloat() / 2 - .001f
    euler.x = euler.x.coerceIn(-clampX, clampX)
    euler.y = euler.y.coerceIn(-clampY, clampY)

    // update
    relative = Quaternionf().rotateYXZ(euler.y, euler.x, euler.z)
    orientation.set(pivot).mul(relative)

    player.setCameraOrientation(orientation)
}

var selectedCameraControls: CameraControls? = null