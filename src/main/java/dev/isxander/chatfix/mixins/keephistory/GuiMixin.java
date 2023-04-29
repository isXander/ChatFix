package dev.isxander.chatfix.mixins.keephistory;

import dev.isxander.chatfix.config.ChatFixConfig;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Gui.class)
public class GuiMixin {
    @ModifyArg(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;clearMessages(Z)V"))
    private boolean shouldClearChatHistory(boolean clear) {
        return !ChatFixConfig.INSTANCE.getConfig().dontClearHistory;
    }
}
