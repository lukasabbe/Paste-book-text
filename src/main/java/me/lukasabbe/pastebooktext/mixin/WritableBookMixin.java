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

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Mixin(BookEditScreen.class)
public abstract class WritableBookMixin extends Screen {
    @Shadow @Final private SelectionManager currentPageSelectionManager;

    protected WritableBookMixin(Text title) {
        super(title);
    }

    @Shadow protected abstract void openNextPage();

    @Shadow protected abstract String getClipboard();

    @Shadow private int currentPage;

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

            String[] strings;
            if(clipboard.length() > 1023){
                strings = getSubString(clipboard.substring(0,1023), currentPageContent, currentPageAccessors.selectionStartInt());

            }else{
                strings = getSubString(clipboard, currentPageContent, currentPageAccessors.selectionStartInt());
            }
            clipboard = clipboard.substring(Integer.parseInt(strings[1]));
            currentPageSelectionManager.insert(strings[0]);
            if(currentPage < 99){
                openNextPage();
            }else{
                clipboard = "";
            }
        }
    }
    @Unique
    String getTotalContent(final String currentPageContent, final int selectionInt, final String clipboard){
        return (new StringBuilder(currentPageContent)).insert(selectionInt, clipboard).toString();
    }
    @Unique
    String[] getSubString(String clipboard, String currentPageContent, int startInt){
        for(int i = clipboard.length() - 1 ; i >=0; i--){
            StringBuilder substring = new StringBuilder(clipboard.substring(0, i));
            if(textRenderer.getWrappedLinesHeight(getTotalContent(currentPageContent, startInt, substring.toString()),114) > 127) continue;
            List<String> substrings = Arrays.stream(substring.toString().split(" ")).toList();
            substring = new StringBuilder();
            String popedElemnt = "";
            for(int y = 0; y<substrings.size(); y++){
                if(substrings.size()-1 > y) {
                    substring.append(substrings.get(y));
                    substring.append(" ");
                }else{
                    popedElemnt = substrings.get(y);
                }
            }
            int popedSize = popedElemnt.length();
            if(popedElemnt.split(" ").length > 0)
                popedSize++;
            return new String[]{substring.toString(), Integer.toString(i-popedSize)};
        }
        return new String[]{null, null};
    }
}
