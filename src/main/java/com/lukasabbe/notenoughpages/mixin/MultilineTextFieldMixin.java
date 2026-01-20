package com.lukasabbe.notenoughpages.mixin;

import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultilineTextField.class)
public interface MultilineTextFieldMixin {
    @Invoker("truncateInsertionText")
    String truncateInsertionTextInvoker(String string);

    @Invoker("overflowsLineLimit")
    boolean overflowsLineLimitInvoker(String string);

    @Accessor("value")
    String getValue();

    @Accessor("selectCursor")
    int getSelectCursor();

    @Accessor("selecting")
    boolean getSelecting();
}
