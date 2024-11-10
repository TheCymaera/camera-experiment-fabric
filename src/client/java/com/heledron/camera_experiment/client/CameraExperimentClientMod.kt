@file:Suppress("UNCHECKED_CAST")

package com.heledron.camera_experiment.client

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f


class CameraExperimentClientMod : ClientModInitializer {
    override fun onInitializeClient() {
        val controlTypes = arrayOf(
            "Default" to null,
            "Pivoted" to PIVOTED_CAMERA_CONTROLS,
            "Unfixed" to UNFIXED_CAMERA_CONTROLS
        )


        var cooldown = 0
        ClientTickEvents.START_WORLD_TICK.register { level ->
            val minecraft = Minecraft.getInstance()
            if (level != minecraft.level) return@register
            val player = minecraft.player ?: return@register

            // Throttle
            if (cooldown > 0) {
                cooldown--
                return@register
            }

            // On right click, toggle camera override
            if (minecraft.options.keyUse.isDown) {
                cooldown = 10

                // check correct item
                if (!player.getItemInHand(InteractionHand.MAIN_HAND).displayName.string.contains("camera", ignoreCase = true)) {
                    return@register
                }

                // get next camera
                val index = controlTypes.indexOfFirst { it.second == selectedCameraControls }
                val nextIndex = (index + 1) % controlTypes.size
                val (name,selected) = controlTypes[nextIndex]

                selectedCameraControls = selected

                // reset everything if switching to default camera
                if (selected == null) {
                    player.resetCameraPivot()
                    player.cameraZRot = 0f
                }

                // action bar
                val message = "Camera controls: $name"
                player.displayClientMessage(Component.literal(message), true)
            }
        }

        // Render camera pivot plane when adjusting pivot
        val renderType = RenderType.create(
            "camera_pivot_plane",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(false)
        )

        WorldRenderEvents.AFTER_ENTITIES.register { context ->
            if (!isAdjustingPivot) return@register

            val camera = context.camera()
            val player = Minecraft.getInstance().player ?: return@register
            val delta = context.tickCounter()

            val position = player.getEyePosition(delta.getGameTimeDeltaPartialTick(true))
            val rotation = player.getCameraPivot()
            val rectCenter = position.subtract(camera.position).toVector3f()

            // color
            val r = 0
            val g = 204
            val b = 204
            val a = 50

            // rect corners
            val size = 2f
            val r1 = rotation.transform(Vector3f(-size, -player.eyeHeight, -size)).add(rectCenter)
            val r2 = rotation.transform(Vector3f( size, -player.eyeHeight, -size)).add(rectCenter)
            val r3 = rotation.transform(Vector3f( size, -player.eyeHeight,  size)).add(rectCenter)
            val r4 = rotation.transform(Vector3f(-size, -player.eyeHeight,  size)).add(rectCenter)

            val buffer = context.consumers()?.getBuffer(renderType) ?: return@register
            buffer.addVertex(r1.x, r1.y, r1.z).setColor(r, g, b, a)
            buffer.addVertex(r2.x, r2.y, r2.z).setColor(r, g, b, a)
            buffer.addVertex(r3.x, r3.y, r3.z).setColor(r, g, b, a)
            buffer.addVertex(r4.x, r4.y, r4.z).setColor(r, g, b, a)
        }
    }
}

