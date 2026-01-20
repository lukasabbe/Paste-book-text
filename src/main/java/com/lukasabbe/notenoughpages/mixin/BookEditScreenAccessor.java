package com.lukasabbe.notenoughpages.mixin;

import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BookEditScreen.class)
public interface BookEditScreenAccessor {
    @Accessor("page")
    MultiLineEditBox getPage();

    @Invoker("pageForward")
    void nextPage();

    @Invoker("getNumPages")
    int getPagesAmount();
}
