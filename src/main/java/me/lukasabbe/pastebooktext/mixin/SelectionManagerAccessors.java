package me.lukasabbe.pastebooktext.mixin;

import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Predicate;
import java.util.function.Supplier;

@Mixin(SelectionManager.class)
public interface SelectionManagerAccessors {
    @Accessor("stringFilter")
    Predicate<String> getStringFilter();

    @Accessor("stringGetter")
    Supplier<String> getStringGetter();

    @Accessor("selectionStart")
    int selectionStartInt();
}
