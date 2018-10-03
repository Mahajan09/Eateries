package dev.rism.eateries;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by risha on 18-04-2018.
 */

public class FilterDialog extends Dialog {
    Spinner city,cuisine;
    Button bok;
    OnDialogResult mResult;
    Context context;
    public FilterDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_filter);
        setTitle("Filter");
        city=findViewById(R.id.city_spinner);
        cuisine=findViewById(R.id.type_spinner);
        bok=findViewById(R.id.button_ok);
       city.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,Constants.TOP_CITIES));
       cuisine.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,Constants.CUISINES));
        bok.setOnClickListener(new OkListener());
    }
    private class OkListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            if (mResult!=null)
            {
                mResult.finish(city.getSelectedItem().toString(),cuisine.getSelectedItem().toString());
            }
            FilterDialog.this.dismiss();
        }
    }
    public void setDialogResult(OnDialogResult result)
    {
        mResult=result;
    }
    public interface OnDialogResult
    {
        void finish(String city,String cuisine);
    }
}
