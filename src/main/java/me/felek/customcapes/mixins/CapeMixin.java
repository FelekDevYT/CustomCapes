package me.felek.customcapes.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import me.felek.customcapes.CustomCapeModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.rusherhack.client.api.Globals.mc;

@Mixin(CapeLayer.class)
public abstract class CapeMixin extends RenderLayer<PlayerRenderState, PlayerModel> {
    public CapeMixin(RenderLayerParent<PlayerRenderState, PlayerModel> renderer) {
        super(renderer);
    }

    @Shadow @Final
    private HumanoidModel<PlayerRenderState> model;

    @Inject(
            method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRender(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, PlayerRenderState playerRenderState, float f, float g, CallbackInfo ci) {
        if (CustomCapeModule.INSTANCE != null && !CustomCapeModule.INSTANCE.isToggled()) {
            return;
        }

        ResourceLocation customCapeTexture = CustomCapeModule.INSTANCE.getSelectedCapeTexture();
        if (customCapeTexture == null) {
            return;
        }

        boolean isMe = playerRenderState.skin.texture().equals(mc.player.getSkin().texture());
        if (!CustomCapeModule.INSTANCE.isOnlyForMe() || isMe) {
            if (!playerRenderState.isInvisible && playerRenderState.showCape) {
                poseStack.pushPose();

                VertexConsumer vrtxCons = multiBufferSource.getBuffer(RenderType.entitySolid(customCapeTexture));
                this.getParentModel().copyPropertiesTo(this.model);

                this.model.setupAnim(playerRenderState);

                this.model.renderToBuffer(poseStack, vrtxCons, i, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();

                poseStack.popPose();
            }
        }

        ci.cancel();
    }
}
