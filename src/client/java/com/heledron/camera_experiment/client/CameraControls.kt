package com.heledron.camera_experiment.client

import com.heledron.camera_experiment.client.utilities.ParticleRenderer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
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

val PIVOTED_CAMERA_CONTROLS: CameraControls = Controls@{ player, moveVertical, moveHorizontal ->
    val orientation = player.getCameraOrientation()
    val pivot = player.getCameraPivot()


    if (player.isShiftKeyDown) {
        orientation.rotateZ(-moveVertical -moveHorizontal)
        player.setCameraPivot(orientation)
        player.setCameraOrientation(orientation)

        // draw pivot axis
        // TODO: Replace this with a proper graphics when I figure out rendering
        val pivotAxis = Vec3(pivot.transform(Vector3f(0f,2f,0f)))
        val middle = player.eyePosition.add(player.forward.scale(5.0))
        val start = middle.subtract(pivotAxis.scale(.5))
        ParticleRenderer.drawLine(player.level(), start,pivotAxis)

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