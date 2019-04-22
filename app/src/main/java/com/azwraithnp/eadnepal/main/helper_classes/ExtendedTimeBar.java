package com.azwraithnp.eadnepal.main.helper_classes;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.exoplayer2.ui.DefaultTimeBar;

public class ExtendedTimeBar extends DefaultTimeBar {
    private boolean enabled;
    private boolean forceDisabled;

    /**
     * Creates a new time bar.
     *
     * @param context
     * @param attrs
     */
    public ExtendedTimeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        super.setEnabled(!this.forceDisabled && this.enabled);
    }

    public void setForceDisabled(boolean forceDisabled) {
        this.forceDisabled = forceDisabled;
        setEnabled(enabled);
    }
}
