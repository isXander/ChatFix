package dev.isxander.chatfix.mixins.keepmsg;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.chatfix.util.KeepMsgStorage;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow protected EditBox input;

    // Update last message when message is edited
    @Inject(method = "onEdited", at = @At("RETURN"))
    private void onMessageEdited(String message, CallbackInfo ci) {
        KeepMsgStorage.lastMessage = input.getValue();
    }

    // Clear last message when message is sent, the user wanted to do this
    @Inject(method = "handleChatInput", at = @At("RETURN"))
    private void onMessageSent(String text, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            KeepMsgStorage.lastMessage = null;
        }
    }

    // Clear last message when chat is closed, the user wanted to do this
    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void onEscapePressed(int key, int scancode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (key == InputConstants.KEY_ESCAPE)
            KeepMsgStorage.lastMessage = null;
    }
}
