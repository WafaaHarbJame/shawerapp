package com.shawerapp.android.custom.views;

import android.content.Context;
import android.util.AttributeSet;

public class ArabicTextView extends androidx.appcompat.widget.AppCompatTextView {

    public ArabicTextView(Context context) {
        super(context);
    }

    public ArabicTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArabicTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(convertToEnglishDigits(text), type);
    }

    public static String convertToEnglishDigits(CharSequence value) {
        String newValue = value.toString().replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5")
                .replace("٦", "6").replace("٧", "7").replace("٨", "8").replace("٩", "9").replace("٠", "0")
                .replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4").replace("۵", "5")
                .replace("۶", "6").replace("۷", "7").replace("۸", "8").replace("۹", "9").replace("۰", "0");

        return newValue;
    }
}
