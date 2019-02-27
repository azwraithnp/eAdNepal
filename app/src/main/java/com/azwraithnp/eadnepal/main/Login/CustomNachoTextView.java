package com.azwraithnp.eadnepal.main.Login;

import android.content.Context;
import android.util.AttributeSet;

import com.hootsuite.nachos.NachoTextView;

public class CustomNachoTextView extends NachoTextView {
    public CustomNachoTextView(Context context) {
        super(context);
    }

    public CustomNachoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNachoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick()
    {
        super.performClick();
        this.showDropDown();
        return true;
    }
}
