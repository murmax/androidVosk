// Copyright 2019 Alpha Cephei Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.vosk.demo;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;



public class VoskActivity extends Activity implements
        RecognitionListener {

    static public List<Command> commands =  new ArrayList<Command>();
    static public List<ExecutableFunction> functions = new ArrayList<ExecutableFunction>();
    static public List<Variable> vars = new ArrayList<Variable>();

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_FILE = 3;
    static private final int STATE_MIC = 4;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    private TextView resultView;
    private boolean onPause=false;


    public static DBHelper dbHelper;

    static public void addCommand(Command com)
    {
        if (findCommandByName(com.name)!=null)
        {
            return;
        }
        commands.add(com);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        cv.put("id", com.getId());
        cv.put("name", com.name);
        cv.put("phonetic", com.phonetic);
        cv.put("func", com.func.getId());
        // вставляем запись и получаем ее ID
        db.insert("commands", null, cv);
        dbHelper.close();
    }

    static public void updateCommand(Command com)
    {
        removeCommand(com);
        addCommand(com);
    }
    static public void removeCommand(Command com)
    {

        commands.remove(com);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("commands", "id = '" + com.getId()+"'", null);
        dbHelper.close();
    }
    static public Command findCommandByName(String name)
    {
        for (Command com: commands
             ) {
            if (com.name.equals(name)) return com;
        }
        return null;
    }



    static public void addExecutableFunction(ExecutableFunction func)
    {
        if (findExecutableFunctionById(func.getId())!=null)
        {
            return;
        }
        functions.add(func);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        cv.put("id", func.getId());
        cv.put("name", func.name);
        cv.put("descr", func.descr);
        cv.put("code", func.luaCode);
        // вставляем запись и получаем ее ID
        db.insert("funcs", null, cv);
        dbHelper.close();
    }

    static public void updateExecutableFunction(ExecutableFunction func)
    {
        removeExecutableFunction(func);
        addExecutableFunction(func);
    }
    static public void removeExecutableFunction(ExecutableFunction func)
    {

        functions.remove(func);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("funcs", "id = '" + func.getId()+"'", null);
        dbHelper.close();
    }
    static public ExecutableFunction findExecutableFunctionById(int id)
    {
        for (ExecutableFunction func: functions
        ) {
            if (func.getId()==id) return func;
        }
        return null;
    }


    static public ExecutableFunction findExecutableFunctionByName(String name)
    {
        for (ExecutableFunction func: functions
        ) {
            if (func.name.equals(name)) return func;
        }
        return null;
    }

    static public boolean addVariable(Variable var)
    {
        if (findVariableById(var.getId())!=null)
        {
            return false;
        }
        vars.add(var);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        cv.put("id", var.getId());
        cv.put("name", var.name);
        cv.put("type", var.type);
        cv.put("descr", var.descr);
        cv.put("value", var.value);
        // вставляем запись и получаем ее ID
        db.insert("variables", null, cv);
        dbHelper.close();
        return true;
    }

    static public void updateVariable(Variable var)
    {
        removeVariable(var);
        addVariable(var);
    }
    static public void removeVariable(Variable var)
    {

        vars.remove(var);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete("variables", "id = '" + var.getId()+"'", null);
        dbHelper.close();
    }
    static public Variable findVariableById(int id)
    {
        for (Variable var: vars
        ) {
            if (var.getId() == id) return var;
        }
        return null;
    }



    final String LOG_TAG = "myLogs";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);
        if (dbHelper == null) {
            dbHelper = new DBHelper(this);
        }

        // создаем объект для данных
        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("funcs", null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int codeColIndex = c.getColumnIndex("code");
            int descrColIndex = c.getColumnIndex("descr");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                ExecutableFunction f = new ExecutableFunction(
                        c.getString(nameColIndex),
                        c.getString(codeColIndex),
                        c.getString(descrColIndex),
                        c.getInt(idColIndex)
                );
                functions.add(f);
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        }
        c.close();


        c = db.query("commands", null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int phoneticColIndex = c.getColumnIndex("phonetic");
            int funcColIndex = c.getColumnIndex("func");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                commands.add(
                        new Command(
                                c.getString(nameColIndex),
                                c.getString(phoneticColIndex),
                                findExecutableFunctionById(
                                        c.getInt(funcColIndex)
                                ),
                                c.getInt(idColIndex)
                        )
                );

            } while (c.moveToNext());
        }
        c.close();


        c = db.query("variables", null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int typeColIndex = c.getColumnIndex("type");
            int descrColIndex = c.getColumnIndex("descr");
            int valueColIndex = c.getColumnIndex("value");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                vars.add(
                        new Variable(
                                c.getString(nameColIndex),
                                c.getString(typeColIndex),
                                c.getString(descrColIndex),
                                c.getString(valueColIndex),
                                c.getInt(idColIndex)

                        )
                );

            } while (c.moveToNext());
        }
        c.close();


        dbHelper.close();
        ExecutableFunction func = new ExecutableFunction("Пример","print( obj:method(msg) );","Пишет сообщение, принимает параметр <msg>",null);






        // Setup layout
        resultView = findViewById(R.id.result_text);
        setUiState(STATE_START);

        findViewById(R.id.recognize_file).setOnClickListener(view -> recognizeFile());
        findViewById(R.id.recognize_mic).setOnClickListener(view -> recognizeMicrophone());
        findViewById(R.id.pause).setOnClickListener(view -> pause(onPause));

        LibVosk.setLogLevel(LogLevel.INFO);

        // Check if user has given permission to record audio, init the model after permission is granted
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            initModel();
        }

        findViewById(R.id.btn_editCommands).setOnClickListener(view -> goToEditCommands());
        findViewById(R.id.btn_editFunctions).setOnClickListener(view -> goToEditFunctions());
        findViewById(R.id.btn_editVariables).setOnClickListener(view -> goToEditVariables());
        findViewById(R.id.btn_runScript).setOnClickListener(view -> runLuaFromBtn());




    }

    private String takeTextFromHypothesis(String hypo)
    {
        //String formatted = hypo.replace("\"text\"","");
        String[] dividedHypo = hypo.split("\"");

        if (dividedHypo.length==5)
        {
            return dividedHypo[3];
        } else return "";
    }

    private void goToEditCommands()
    {
        Intent intent = new Intent(this, CommandsActivity.class);
        startActivity(intent);
    }

    private void goToEditFunctions()
    {
        Intent intent = new Intent(this, FunctionsActivity.class);
        startActivity(intent);
    }

    private void goToEditVariables()
    {
        Intent intent = new Intent(this, VariablesActivity.class);
        startActivity(intent);
    }

    private void initModel() {
        StorageService.unpack(this, "model-small-ru", "model",
                (model) -> {
                    this.model = model;
                    setUiState(STATE_READY);
                },
                (exception) -> setErrorState("Failed to unpack the model" + exception.getMessage()));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                initModel();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }

        if (speechStreamService != null) {
            speechStreamService.stop();
        }
    }

    @Override
    public void onResult(String hypothesis) {
        String result = takeTextFromHypothesis(hypothesis);

        //Toast.makeText(getApplicationContext(),"Результат:"+result,Toast.LENGTH_SHORT).show();

        for (Command com: commands) {
            Command.ParsedCommand res = com.checkCommand(result);
            if (res.isSure)
            {
                resultView.append(com.name + " worked!" + "\n");
                //resultView.setText(com.name + " worked!");
                this.runLua(com.func.luaCode,res.args);
                //com.execute();
            }
            for (Map.Entry<String, String> entry : res.args.entrySet()) {
                resultView.append(entry.getKey() + " - "+ entry.getValue() + "\n");
            }
        }

        //resultView.append(hypothesis + "\n");
        resultView.setText(result);
    }

    @Override
    public void onFinalResult(String hypothesis) {
        //resultView.append(hypothesis + "\n");
        resultView.setText(takeTextFromHypothesis(hypothesis));
        setUiState(STATE_DONE);
        if (speechStreamService != null) {
            speechStreamService = null;
        }
    }

    @Override
    public void onPartialResult(String hypothesis) {
        //Toast.makeText(getApplicationContext(),"Hypo3:"+hypothesis,Toast.LENGTH_SHORT).show();
        //resultView.append(hypothesis + "\n");
        resultView.setText(takeTextFromHypothesis(hypothesis));
    }

    @Override
    public void onError(Exception e) {
        setErrorState(e.getMessage());
    }

    @Override
    public void onTimeout() {
        setUiState(STATE_DONE);
    }

    private void setUiState(int state) {
        switch (state) {
            case STATE_START:
                resultView.setText(R.string.preparing);
                resultView.setMovementMethod(new ScrollingMovementMethod());
                findViewById(R.id.recognize_file).setEnabled(false);
                findViewById(R.id.recognize_mic).setBackgroundResource(R.drawable.btn_mic);
                findViewById(R.id.recognize_mic).setEnabled(false);
                findViewById(R.id.pause).setBackgroundResource(R.drawable.pause_96_5_disabled);
                onPause=false;
                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_READY:
                resultView.setText(R.string.ready);
                //Распознать с микрофона
                findViewById(R.id.recognize_file).setEnabled(true);
                findViewById(R.id.recognize_mic).setBackgroundResource(R.drawable.btn_mic);
                findViewById(R.id.recognize_mic).setEnabled(true);
                findViewById(R.id.pause).setBackgroundResource(R.drawable.pause_96_5_disabled);
                onPause=false;
                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_DONE:
                ((Button) findViewById(R.id.recognize_file)).setText(R.string.recognize_file);
                //Распознать с микрофона
                findViewById(R.id.recognize_file).setEnabled(true);
                findViewById(R.id.recognize_mic).setBackgroundResource(R.drawable.btn_mic);
                findViewById(R.id.recognize_mic).setEnabled(true);
                findViewById(R.id.pause).setBackgroundResource(R.drawable.pause_96_5_disabled);
                onPause=false;
                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_FILE:
                ((Button) findViewById(R.id.recognize_file)).setText(R.string.stop_file);
                resultView.setText(getString(R.string.starting));
                findViewById(R.id.recognize_mic).setBackgroundResource(R.drawable.btn_stop);
                findViewById(R.id.recognize_mic).setEnabled(false);
                findViewById(R.id.recognize_file).setEnabled(true);
                findViewById(R.id.pause).setBackgroundResource(R.drawable.pause_96_5_disabled);
                onPause=false;
                findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_MIC:
                //Прекратить распознавание
                resultView.setText(getString(R.string.say_something));
                findViewById(R.id.recognize_file).setEnabled(false);
                findViewById(R.id.recognize_mic).setBackgroundResource(R.drawable.btn_stop);
                findViewById(R.id.recognize_mic).setEnabled(true);
                findViewById(R.id.pause).setBackgroundResource(R.drawable.btn_mic_pause);
                onPause=false;
                findViewById(R.id.pause).setEnabled((true));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    private void setErrorState(String message) {
        resultView.setText(message);
        //Toast.makeText(getApplicationContext(),"error:"+message,Toast.LENGTH_SHORT).show();
        //((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
        //Распознать с микрофона
        findViewById(R.id.recognize_file).setEnabled(false);
        findViewById(R.id.recognize_mic).setBackgroundResource(R.drawable.btn_mic);
        findViewById(R.id.recognize_mic).setEnabled(false);
    }

    private void recognizeFile() {
        if (speechStreamService != null) {
            setUiState(STATE_DONE);
            speechStreamService.stop();
            speechStreamService = null;
        } else {
            setUiState(STATE_FILE);
            try {
                Recognizer rec = new Recognizer(model, 16000.f, "[\"one zero zero zero one\", " +
                        "\"oh zero one two three four five six seven eight nine\", \"[unk]\"]");

                InputStream ais = getAssets().open(
                        "10001-90210-01803.wav");
                if (ais.skip(44) != 44) throw new IOException("File too short");

                speechStreamService = new SpeechStreamService(rec, ais, 16000);
                speechStreamService.start(this);
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }

    private void recognizeMicrophone() {
        if (speechService != null) {
            setUiState(STATE_DONE);
            speechService.stop();
            speechService = null;
            onPause=true;
        } else {
            setUiState(STATE_MIC);
            try {
                Recognizer rec = new Recognizer(model, 16000.0f);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.startListening(this);
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }


    private void pause(boolean isPause) {
        if (speechService != null) {
            if(!isPause) findViewById(R.id.pause).setBackgroundResource(R.drawable.btn_mic_play);
            else findViewById(R.id.pause).setBackgroundResource(R.drawable.btn_mic_pause);
            speechService.setPause(!isPause);
            onPause = !isPause;
        }
    }


    public void runLua(String script, Map<String, String> args) {
        Charset charset = StandardCharsets.UTF_8;

        Globals globals = JsePlatform.standardGlobals();
//
//        Bubble bubble = new Bubble(this);
//        globals.set("bubble", CoerceJavaToLua.coerce(bubble));
//        String mytext = "mytext";
//        globals.set("mytext", CoerceJavaToLua.coerce(mytext));

        for (Map.Entry<String, String> entry : args.entrySet()) {
            globals.set(entry.getKey(), CoerceJavaToLua.coerce(entry.getValue()));
        }

        for (Variable var: vars) {
            globals.set(var.name, CoerceJavaToLua.coerce(var.value));
        }

        LuaValue instance = CoerceJavaToLua.coerce(new MyClass(this));
        globals.set("obj", instance);

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream outPrintStream = null;
        try {
            outPrintStream = new PrintStream(outStream, true, charset.name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        globals.STDOUT = outPrintStream;
        globals.STDERR = outPrintStream;


        TextView scriptOutput = findViewById(R.id.lb_result);
        try {
            globals.load(script).call();

            scriptOutput.setTextColor(Color.WHITE);
            scriptOutput.setText(scriptOutput.getText() + "\n" + new String(outStream.toByteArray(), charset));


        } catch (LuaError e) {
            scriptOutput.setTextColor(Color.RED);
            scriptOutput.setText(scriptOutput.getText() + "\n" + e.getMessage());
        } finally {
            outPrintStream.close();
        }
    }


    private void runLuaFromBtn()
    {
        runLua("print( obj );" +
                "print( obj.variable );" +
                "print( obj.field );" +
                "print( obj.func );" +
                "print( obj.method );" +
                "print( obj:method() );" + // same as 'obj.method(obj)'
                "print( obj.method(obj) );",new HashMap<>());
    }

    public static class MyClass {
        public static String variable = "variable-value";
        public String field = "field-value";
        public Context context;
        MyClass(Context context)
        {
            this.context = context;
        }

        public static String func() {
            return "function-result";
        }

        public String method(String text) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            return "method-result"+text;
        }
    }




    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "murmaxDB", null, 3);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // создаем таблицу с полями
            db.execSQL("create table commands ("
                    + "id integer primary key,"
                    + "name varchar,"
                    + "phonetic varchar,"
                    + "func int" + ");");

            db.execSQL("create table funcs ("
                    + "id integer primary key,"
                    + "name varchar,"
                    + "descr varchar,"
                    + "code varchar" + ");");

            db.execSQL("create table variables ("
                    + "id integer primary key,"
                    + "name varchar,"
                    + "type varchar,"
                    + "descr varchar,"
                    + "value varchar" + ");");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


            if (oldVersion==1) {
                db.execSQL("drop table varsString;");

                db.execSQL("create table variables ("
                        + "id integer primary key,"
                        + "name varchar,"
                        + "type varchar,"
                        + "descr varchar,"
                        + "value varchar" + ");");
                oldVersion=2;
            }
            if (oldVersion==2){
                db.execSQL("drop table commands;");
                db.execSQL("drop table funcs;");


                db.execSQL("create table commands ("
                        + "id integer primary key,"
                        + "name varchar,"
                        + "phonetic varchar,"
                        + "func int" + ");");

                db.execSQL("create table funcs ("
                        + "id integer primary key,"
                        + "name varchar,"
                        + "descr varchar,"
                        + "code varchar" + ");");

            }
        }
    }



}
