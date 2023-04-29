package dev.isxander.chatfix.mixins.dontoverlaparmor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void translateChatUpPre(PoseStack matrices, int tickDelta, int i, int j, CallbackInfo ci) {
        matrices.pushPose();
        matrices.translate(0f, -10f, 0f);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void translateChatUpPost(PoseStack matrices, int tickDelta, int i, int j, CallbackInfo ci) {
        matrices.popPose();
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private int modifyMouseY(int y) {
        return y + 10;
    }

    @ModifyVariable(method = {"handleChatQueueClicked", "getClickedComponentStyleAt", "getMessageTagAt"}, at = @At("HEAD"), argsOnly = true, ordinal = 1)
    private double modifyMouseYForClick(double y) {
        return y + 10;
    }
}
