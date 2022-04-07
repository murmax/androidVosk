package org.vosk.demo;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ExecutableFunction {
    VoskActivity activity;
    String name;
    String luaCode;
    Map<String, String> args = new HashMap<>();

    ExecutableFunction(VoskActivity a, String name, String luaCode, Map<String, String> args)
    {
        this.activity = a;
        this.name = name;
        this.luaCode = luaCode;
        this.args = args;
    }


    void exec()
    {
        activity.runLua(luaCode,args);
    }

}
