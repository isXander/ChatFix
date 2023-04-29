package dev.isxander.chatfix.mixins.keepmsg;

import dev.isxander.chatfix.util.KeepMsgStorage;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @ModifyArg(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;openChatScreen(Ljava/lang/String;)V", ordinal = 0))
    private String modifyInitialInputForChat(String input) {
        if (KeepMsgStorage.lastMessage != null)
            return KeepMsgStorage.lastMessage;
        return input;
    }

    @ModifyArg(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;openChatScreen(Ljava/lang/String;)V", ordinal = 1))
    private String modifyInitialInputForCommand(String input) {
        if (KeepMsgStorage.lastMessage != null) {
            if (KeepMsgStorage.lastMessage.startsWith("/")) {
                return KeepMsgStorage.lastMessage;
            } else {
                KeepMsgStorage.lastMessage = null;
            }
        }
        return input;
    }
}
