package dev.rism.eateries;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by risha on 18-04-2018.
 */


public class CanaroTextView extends android.support.v7.widget.AppCompatTextView {
    public CanaroTextView(Context context) {
        super(context);
        init();
    }

    public CanaroTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanaroTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
         init();
    }
    private void init()
    {
        Typeface typeface=Typeface.createFromAsset(getContext().getAssets(),"canaro_extra_bold.otf");
        setTypeface(typeface);
    }
}
