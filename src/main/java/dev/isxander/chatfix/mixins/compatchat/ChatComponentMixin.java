package dev.isxander.chatfix.mixins.compatchat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.isxander.chatfix.util.DuplicateComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.*;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Shadow @Final private List<GuiMessage> allMessages;
    @Shadow @Final private List<GuiMessage.Line> trimmedMessages;

    @Unique private boolean replacingMessage = false;
    @Unique private final Map<GuiMessage, Collection<GuiMessage.Line>> messageToLines = new HashMap<>();

    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private Component changeAddedMessage(Component message, Component dontuse, @Nullable MessageSignature signature, int ticks, @Nullable GuiMessageTag tag, boolean refresh) {
        if (replacingMessage) {
            replacingMessage = false;
            return message;
        }

        if (allMessages.isEmpty())
            return message;

        var iterator = allMessages.listIterator();
        int maxLookBack = 1;
        int i = 0;
        int duplicated = -1;
        while (iterator.hasNext() && i < maxLookBack) {
            var msg = iterator.next();
            var content = msg.content();
            int duplicate = 1;
            if (content instanceof DuplicateComponent duplicateComponent) {
                content = duplicateComponent.getActualContent();
                duplicate = duplicateComponent.getDuplicateCount();
            }
            if (content.getString().equals(message.getString()) && content.getStyle().equals(message.getStyle())) {
                if (Objects.equals(msg.tag(), tag)) {
                    messageToLines.remove(msg).forEach(line -> trimmedMessages.remove(line));
                    iterator.remove();
                    duplicated = duplicate + 1;
                    break;
                }
            }
            maxLookBack++;
        }

        if (duplicated != -1)
            return DuplicateComponent.create(message.copy().append(" ").append(Component.literal("(%s)".formatted(duplicated)).withStyle(ChatFormatting.GRAY)), message, duplicated);
        return message;
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0))
    private Object hookMessageLineAdd(Object line, @Share("lines") LocalRef<List<GuiMessage.Line>> linesRef) {
        if (linesRef.get() == null)
            linesRef.set(new ArrayList<>());
        linesRef.get().add((GuiMessage.Line) line);
        return line;
    }

    @ModifyArg(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 1))
    private Object hookMessageAdd(Object message, @Share("lines") LocalRef<List<GuiMessage.Line>> linesRef) {
        messageToLines.put((GuiMessage) message, linesRef.get());
        return message;
    }

    @ModifyExpressionValue(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(I)Ljava/lang/Object;", ordinal = 1))
    private Object removeMessageToLineCache(Object message) {
        messageToLines.remove((GuiMessage) message);
        return message;
    }
}
