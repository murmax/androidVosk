package org.vosk.demo;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ExecutableFunction {
    public int getId() {
        return id;
    }

    //VoskActivity activity;
    private int id;
    private static int maxId=1;
    String name;
    String descr;
    String luaCode;



    ExecutableFunction( String name, String luaCode,String descr,Integer id)
    {
        if (id==null) {
            maxId++;
            this.id = maxId;
        } else
        {
            this.id = id;
            if (id>maxId) maxId=id;
        }
        //this.activity = a;
        this.name = name;
        this.luaCode = luaCode;
        this.descr = descr;
    }


    void exec()
    {
        //activity.runLua(luaCode,args);
    }

}
