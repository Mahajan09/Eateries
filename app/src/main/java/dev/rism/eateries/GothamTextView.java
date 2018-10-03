package dev.rism.eateries;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by risha on 18-04-2018.
 */

public class GothamTextView extends android.support.v7.widget.AppCompatTextView {
    public GothamTextView(Context context) {
        super(context);
        init();
    }

    public GothamTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GothamTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init()
    {
        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),"gotham.otf");
        setTypeface(typeface);
    }
}