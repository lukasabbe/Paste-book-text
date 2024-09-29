package me.lukasabbe.pastebooktext.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(BookEditScreen.class)
public abstract class WritableBookMixin extends Screen {
    @Shadow @Final private SelectionManager currentPageSelectionManager;

    protected WritableBookMixin(Text title) {
        super(title);
    }

    @Shadow protected abstract void openNextPage();

    @Shadow protected abstract String getClipboard();

    @Inject(method = "keyPressedEditMode", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SelectionManager;paste()V"), cancellable = true)
    public void pasteInjects(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir){
        pasteText();
        cir.setReturnValue(true);
    }

    @Unique
    private void pasteText(){
        final SelectionManagerAccessors currentPageAccessors = (SelectionManagerAccessors) currentPageSelectionManager;
        String currentPageContent = currentPageAccessors.getStringGetter().get();
        String clipboard = getClipboard();
        final Predicate<String> test = currentPageAccessors.getStringFilter();

        final int startInt = currentPageAccessors.selectionStartInt();
        if(test.test(getTotalContent(currentPageContent, startInt,clipboard)) || currentPageAccessors.getStringGetter().get().length() != startInt){
            currentPageSelectionManager.paste();
            return;
        }

        while (!clipboard.isEmpty()){
            currentPageContent = currentPageAccessors.getStringGetter().get();
            if(test.test(getTotalContent(currentPageContent,currentPageAccessors.selectionStartInt(),clipboard))){
                currentPageSelectionManager.insert(clipboard);
                break;
            }

            for(int i = clipboard.length() - 1 ; i >=0; i--){
                final String substring = clipboard.substring(0, i);
                if(textRenderer.getWrappedLinesHeight(getTotalContent(currentPageContent, currentPageAccessors.selectionStartInt(), substring),114) > 127) continue;
                currentPageSelectionManager.insert(substring);
                clipboard = clipboard.substring(i);
                openNextPage();
                break;
            }

        }
    }
    @Unique
    String getTotalContent(final String currentPageContent, final int selectionInt, final String clipboard){
        return (new StringBuilder(currentPageContent)).insert(selectionInt, clipboard).toString();
    }
}
