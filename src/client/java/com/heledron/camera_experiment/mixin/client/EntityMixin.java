package com.heledron.camera_experiment.mixin.client;

import com.heledron.camera_experiment.client.UtilitiesKt;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.heledron.camera_experiment.client.CameraControlsKt.getSelectedCameraControls;


@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private Entity vehicle;
    @Shadow private float xRot;
    @Shadow private float yRot;
    @Shadow private float xRotO;
    @Shadow private float yRotO;

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void turn(double horizontalMove, double verticalMove, CallbackInfo ci) {
        // ignore if no selected camera controls
        var selectedCameraControls = getSelectedCameraControls();
        if (selectedCameraControls == null) return;

        // ignore if not client player
        var player = UtilitiesKt.castEntityAsClientPlayer(UtilitiesKt.uncheckedCast(this));
        if (player == null) return;

        // track old values
        var oldX = this.xRot;
        var oldY = this.yRot;

        // move camera
        selectedCameraControls.invoke(
            player,
            (float)Math.toRadians(verticalMove * 0.15F),
            (float)Math.toRadians(horizontalMove * 0.15F)
        );

        // Match rotation to avoid first-person hand jitter
        // TODO: Remove this if/when we fix the rendering code
        yRot += 360 * Math.round((oldY - yRot) / 360);

        this.xRotO += xRot - oldX;
        this.yRotO += yRot - oldY;

        // update vehicle
        if (this.vehicle != null) {
            this.vehicle.onPassengerTurned((Entity) (Object) this);
        }

        // cancel default behaviour
        ci.cancel();
    }

    @Inject(method = "setXRot", at = @At("HEAD"), cancellable = true)
    private void setXRot(float f, CallbackInfo ci) {
        // same as overwritten function, but with clamping and wrapping behaviour removed.
        // same as the default setYRot function
        // it seems to be useless anyway as clamping is handled by other functions.

        if (!Float.isFinite(f)) {
            Util.logAndPauseIfInIde("Invalid entity rotation: " + f + ", discarding.");
        } else {
            xRot = f;
        }

        ci.cancel();
    }
}
