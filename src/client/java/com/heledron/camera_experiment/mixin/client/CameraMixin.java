package com.heledron.camera_experiment.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static com.heledron.camera_experiment.client.CameraOrientationKt.getCameraZRot;
import static com.heledron.camera_experiment.client.utilities.MiscKt.castEntityAsClientPlayer;
import static com.heledron.camera_experiment.client.utilities.MathsKt.toRadians;


@Mixin(Camera.class)
public abstract class CameraMixin  {
    @Shadow private Entity entity;

    @ModifyArg(
            method = "setRotation",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;",
                    remap = false
            ),
            index = 2
    )
    private float setRoll(float original) {
        var player = castEntityAsClientPlayer(this.entity);
        if (player == null) return original;
        return original + toRadians(getCameraZRot(player));
    }
}