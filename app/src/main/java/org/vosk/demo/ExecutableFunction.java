package org.vosk.demo;

import android.content.Context;
import android.widget.Toast;

public class ExecutableFunction {
    Context context;
    ExecutableFunction(Context c)
    {
        this.context = c;
    }


    void exec()
    {
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;

        Toast.makeText(context, text, duration).show();
    }

}
