package me.felek.customcapes.mixins;

import me.felek.customcapes.CustomCapeModule;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.rusherhack.client.api.Globals.mc;

@Mixin(value = PlayerInfo.class)
public class MixinPlayerInfo {
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void updateCustomTextures(CallbackInfoReturnable<PlayerSkin> cir) {
        if (CustomCapeModule.INSTANCE == null || !CustomCapeModule.INSTANCE.isToggled()) {
            return;
        }

        ResourceLocation customCape = CustomCapeModule.INSTANCE.getSelectedCapeTexture();
        if (customCape == null) {
            return;
        }

        PlayerSkin oldSkin = cir.getReturnValue();
        PlayerInfo info = (PlayerInfo) (Object) this;
        boolean isMe = mc.player != null && info.getProfile().getId().equals(mc.player.getUUID());

        if (!CustomCapeModule.INSTANCE.isOnlyForMe() || isMe) {
            PlayerSkin newSkin = new PlayerSkin(
                    oldSkin.texture(),
                    oldSkin.textureUrl(),
                    customCape,
                    customCape,
                    oldSkin.model(),
                    oldSkin.secure()
            );

            cir.setReturnValue(newSkin);
        }
    }
}
