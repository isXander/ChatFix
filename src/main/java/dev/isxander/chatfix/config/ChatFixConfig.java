package dev.isxander.chatfix.config;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.config.ConfigInstance;
import dev.isxander.yacl.config.GsonConfigInstance;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ChatFixConfig {
    public static final ConfigInstance<ChatFixConfig> INSTANCE = GsonConfigInstance.createBuilder(ChatFixConfig.class)
            .setPath(FabricLoader.getInstance().getConfigDir().resolve("chatfix.json"))
            .build();

    public boolean dontClearHistory = true;
    public int compactChat = 0;
    public int yOffset = 10;
    public boolean keepMsg = true;
    public int messageHistory = 5000;

    public static Screen getConfigScreen(Screen parent) {
        return YetAnotherConfigLib.create(INSTANCE, ((defaults, config, builder) -> builder
                .title(Component.translatable("chatfix.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("chatfix.title"))
                        .option(Option.createBuilder(boolean.class)
                                .name(Component.translatable("chatfix.opt.dont_clear_history"))
                                .tooltip(Component.translatable("chatfix.opt.dont_clear_history.tooltip"))
                                .binding(defaults.dontClearHistory, () -> config.dontClearHistory, v -> config.dontClearHistory = v)
                                .controller(TickBoxController::new)
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Component.translatable("chatfix.opt.compactchat"))
                                .tooltip(Component.translatable("chatfix.opt.compactchat.tooltip"))
                                .tooltip(Component.translatable("chatfix.opt.compactchat.warning").withStyle(ChatFormatting.RED))
                                .binding(defaults.compactChat, () -> config.compactChat, v -> config.compactChat = v)
                                .controller(opt -> new IntegerSliderController(opt, 0, 15, 1, v -> {
                                    if (v == 0) return CommonComponents.OPTION_OFF;
                                    if (v == 1) return Component.translatable("chatfix.opt.compactchat.only_last");
                                    return Component.literal(Integer.toString(v));
                                }))
                                .build())
                        .option(Option.createBuilder(boolean.class)
                                .name(Component.translatable("chatfix.opt.keep_message"))
                                .tooltip(Component.translatable("chatfix.opt.keep_message.tooltip"))
                                .binding(defaults.keepMsg, () -> config.keepMsg, v -> config.keepMsg = v)
                                .controller(TickBoxController::new)
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Component.translatable("chatfix.opt.chat_history_length"))
                                .tooltip(Component.translatable("chatfix.opt.chat_history_length.tooltip"))
                                .binding(defaults.messageHistory, () -> config.messageHistory, v -> config.messageHistory = v)
                                .controller(opt -> new IntegerSliderController(opt, 100, 50000, 100))
                                .build())
                        .option(Option.createBuilder(int.class)
                                .name(Component.translatable("chatfix.opt.y_offset"))
                                .tooltip(Component.translatable("chatfix.opt.y_offset.tooltip"))
                                .binding(defaults.yOffset, () -> config.yOffset, v -> config.yOffset = v)
                                .controller(opt -> new IntegerSliderController(opt, 0, 50, 2, v -> {
                                    if (v == 0) return Component.translatable("chatfix.vanilla");
                                    if (v == 10) return Component.translatable("chatfix.opt.y_offset.default");
                                    return Component.translatable("chatfix.format.pixels", v);
                                }))
                                .build())
                        .build())

        )).generateScreen(parent);
    }
}
