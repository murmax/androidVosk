package org.vosk.demo;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ExecutableFunction {
    //VoskActivity activity;
    String name;
    String luaCode;

    ExecutableFunction( String name, String luaCode)
    {
        //this.activity = a;
        this.name = name;
        this.luaCode = luaCode;
    }


    void exec()
    {
        //activity.runLua(luaCode,args);
    }

}
