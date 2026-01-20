package com.lukasabbe.notenoughpages.mixin;

import com.lukasabbe.notenoughpages.Notenoughpages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultilineTextField.class)
public class PasteContentMixin {
    @Inject(method = "keyPressed",at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/MultilineTextField;insertText(Ljava/lang/String;)V", ordinal = 0))
    public void getPasteContent(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir){
        Notenoughpages.handlePaste(Minecraft.getInstance().keyboardHandler.getClipboard());
    }
}
