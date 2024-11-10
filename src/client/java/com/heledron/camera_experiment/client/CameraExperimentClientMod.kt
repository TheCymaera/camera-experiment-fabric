@file:Suppress("UNCHECKED_CAST")

package com.heledron.camera_experiment.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand


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

//        HudRenderCallback.EVENT.register { context, _->
//            val tessellator = Tesselator.getInstance()
//            val buffer = tessellator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR)
//
//            val matrix = context.pose().last().pose()
//            buffer.addVertex(matrix, 20f, 20f, 5f).setColor(255,   0,   0, 255)
//            buffer.addVertex(matrix,  5f, 40f, 5f).setColor(  0, 255,   0, 255)
//            buffer.addVertex(matrix, 35f, 40f, 5f).setColor(  0,   0, 255, 255)
//            buffer.addVertex(matrix, 20f, 60f, 5f).setColor(255, 255,   0, 255)
//
//            RenderSystem.setShader(CoreShaders.POSITION_COLOR)
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
//
//            val mesh = buffer.build() ?: return@register
//            BufferUploader.drawWithShader(mesh)
//        }


//        WorldRenderEvents.END.register { context ->
//            val x = 0f//player.x.toFloat()
//            val y = -1f//player.y.toFloat()// - yOffset
//            val z = 0f//player.z.toFloat()
//            val size = .5f
//
//
////            val camera = context.camera()
//
////            val viewMatrix = Matrix4f().lookAt(
////                camera.position.toVector3f(),
////                camera.position.toVector3f().add(camera.lookVector),
////                camera.upVector,
////            )
//
//            val matrix = Matrix4f()
//                .mul(context.positionMatrix())
////                .mul(viewMatrix)
//                .mul(context.projectionMatrix())
//
//            val tessellator = RenderSystem.renderThreadTesselator()
//            val buffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)
//            buffer.addVertex(matrix, x - size, y, z - size).setColor(255, 0, 0, 255)
//            buffer.addVertex(matrix, x + size, y, z - size).setColor(0, 255, 0, 255)
//            buffer.addVertex(matrix, x + size, y, z + size).setColor(0, 0, 255, 255)
//            buffer.addVertex(matrix, x - size, y, z + size).setColor(255, 255, 0, 255)
//            val mesh = buffer.build() ?: return@register
//
//            // set up render state
//            RenderSystem.enableDepthTest()
//            RenderSystem.setShader(CoreShaders.POSITION_COLOR)
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
//            RenderSystem.depthFunc(GL11.GL_ALWAYS)
//
//            RenderSystem.depthFunc(GL11.GL_LEQUAL)
//
//            BufferUploader.drawWithShader(mesh)
//        }
    }
}

