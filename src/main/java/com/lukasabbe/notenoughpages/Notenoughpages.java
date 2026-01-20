package com.lukasabbe.notenoughpages;

import com.lukasabbe.notenoughpages.mixin.BookEditScreenAccessor;
import com.lukasabbe.notenoughpages.mixin.MultiLineEditBoxAccessor;
import com.lukasabbe.notenoughpages.mixin.MultilineTextFieldMixin;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.util.StringUtil;

public class Notenoughpages implements ModInitializer {
    @Override
    public void onInitialize() {}

    public static void handlePaste(String content){
        Screen guiScreen = Minecraft.getInstance().screen;
        if(guiScreen == null) return;
        if(guiScreen instanceof BookEditScreen bookEditScreen){
            MultiLineEditBox page = ((BookEditScreenAccessor) bookEditScreen).getPage();
            MultilineTextField textField = ((MultiLineEditBoxAccessor) page).getTextField();

            if(((MultilineTextFieldMixin) textField).getSelecting()) return;
            if(textField.cursor() != ((MultilineTextFieldMixin) textField).getSelectCursor()) return;
            if(IsContentCorrectLength(content, textField)) return;

            while(true){
                System.out.println(((BookEditScreenAccessor) bookEditScreen).getPagesAmount());
                if(((BookEditScreenAccessor) bookEditScreen).getPagesAmount() == 99){
                    return;
                }

                if(IsContentCorrectLength(content, textField)){
                    textField.insertText(content);
                    return;
                }

                String[] split = splitString(content, textField);

                textField.insertText(split[0]);
                content = split[1];

                ((BookEditScreenAccessor) bookEditScreen).nextPage();
                page = ((BookEditScreenAccessor) bookEditScreen).getPage();
                textField = ((MultiLineEditBoxAccessor) page).getTextField();
            }

        }
    }

    public static String[] splitString(String content, MultilineTextField field){
        int low = 0;
        int high = content.length();
        int splitIndex = 0;

        while(low <= high){
            int mid = low + (high - low) / 2;

            String sub = content.substring(0, mid);

            if (((MultilineTextFieldMixin)field).overflowsLineLimitInvoker(sub)){
                high = mid - 1;
            } else {
                splitIndex = mid;
                low = mid + 1;
            }
        }

        String insertString = content.substring(0, splitIndex);
        String overflow = content.substring(splitIndex);
        return new String[] { insertString, overflow };
    }

    public static boolean IsContentCorrectLength(String content, MultilineTextField field){
        String truncated = ((MultilineTextFieldMixin) field).truncateInsertionTextInvoker(StringUtil.filterText(content, true));
        int selectCursor = ((MultilineTextFieldMixin) field).getSelectCursor();
        int cursor = field.cursor();
        int beginIndex = Math.min(selectCursor, cursor);
        int endIndex  = Math.max(selectCursor, cursor);
        String bookContent = (new StringBuilder(((MultilineTextFieldMixin)field).getValue())).replace(beginIndex, endIndex, truncated).toString();

        return !((MultilineTextFieldMixin) field).overflowsLineLimitInvoker(bookContent);
    }
}
