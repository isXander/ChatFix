package dev.isxander.chatfix.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.List;

public class DuplicateComponent extends MutableComponent {
    private final Component actualContent;
    private final int duplicateCount;

    public DuplicateComponent(ComponentContents component, List<Component> siblings, Style style, Component actualContent, int duplicateCount) {
        super(component, siblings, style);
        this.actualContent = actualContent;
        this.duplicateCount = duplicateCount;
    }

    public Component getActualContent() {
        return actualContent;
    }

    public int getDuplicateCount() {
        return duplicateCount;
    }

    public static DuplicateComponent create(Component component, Component actualContent, int duplicateCount) {
        return new DuplicateComponent(component.getContents(), component.getSiblings(), component.getStyle(), actualContent, duplicateCount);
    }
}
