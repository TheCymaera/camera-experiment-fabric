package com.heledron.camera_experiment.client

import com.heledron.camera_experiment.client.utilities.toDegrees
import com.heledron.camera_experiment.client.utilities.toRadians
import net.minecraft.world.entity.player.Player
import org.joml.Quaternionf
import org.joml.Vector3f

private var playerCameraZRot = 0f
private var playerCameraPivot = Quaternionf()


@Suppress("UnusedReceiverParameter")
fun Player.getCameraPivot(): Quaternionf {
    return Quaternionf(playerCameraPivot)
}

@Suppress("UnusedReceiverParameter")
fun Player.setCameraPivot(pivot: Quaternionf) {
    playerCameraPivot = Quaternionf(pivot)
}

@Suppress("UnusedReceiverParameter")
var Player.cameraZRot: Float
    get() {
        return playerCameraZRot
    }
    set(value) {
        playerCameraZRot = value
    }

fun Player.getCameraEulerYXZ(): Vector3f {
    val x = -toRadians(xRot)
    val y = -toRadians(yRot + 180f)
    val z = toRadians(cameraZRot)
    return Vector3f(x, y, z)
}

fun Player.setCameraEulerYXZ(euler: Vector3f) {
    xRot = -toDegrees(euler.x)
    yRot = -toDegrees(euler.y) - 180f
    cameraZRot = toDegrees(euler.z)
}

fun Player.getCameraOrientation(): Quaternionf {
    val euler = getCameraEulerYXZ()
    return Quaternionf().rotateYXZ(euler.y, euler.x, euler.z)
}

fun Player.setCameraOrientation(orientation: Quaternionf) {
    val euler = orientation.getEulerAnglesYXZ(Vector3f())
//    val optimizedEuler = getEulerYXZWithLeastChange(euler, getEulerYXZ())
    setCameraEulerYXZ(euler)
}

fun Player.resetCameraPivot() {
    setCameraPivot(Quaternionf())
}