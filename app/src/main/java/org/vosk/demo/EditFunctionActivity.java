package org.vosk.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class EditFunctionActivity extends AppCompatActivity {
    EditText le_FunctionName;
    EditText le_FunctionDescription;
    EditText le_FunctionCode;
    Button btn_SaveFunction;
    Button btn_RunFunction;
    Button btn_DeleteFunction;
    int functionId;
    ExecutableFunction currentFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_function);

        Intent intent = getIntent();
        if(intent.hasExtra("functionId")) {
            functionId = intent.getIntExtra("functionId", 0);
        }

        le_FunctionName = findViewById(R.id.le_functionName);
        le_FunctionDescription = findViewById(R.id.le_FunctionDescription);
        le_FunctionCode = findViewById(R.id.le_functionCode);
        btn_SaveFunction = findViewById(R.id.btn_SaveFunction);
        btn_RunFunction = findViewById(R.id.btn_RunFunction);
        btn_DeleteFunction = findViewById(R.id.btn_DeleteFunction);
        btn_SaveFunction.setOnClickListener(view -> saveChanges());
        btn_RunFunction.setOnClickListener(view -> runFunction());
        btn_DeleteFunction.setOnClickListener(view -> deleteFunction());
        findViewById(R.id.imageButton_closeEditFunctionActivity).setOnClickListener(view -> goBack());
        findViewById(R.id.activity_edit_function).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.activity_edit_function).getWindowToken(),0);
                findViewById(R.id.activity_edit_function).clearFocus();
            }
        });

        currentFunction=null;

        fillData();
    }

    private void fillData(){
        if(functionId!=0){
            for (ExecutableFunction func : VoskActivity.functions){
                if(func.getId()==functionId){
                    currentFunction = func;
                    le_FunctionName.setText(func.name);
                    le_FunctionDescription.setText(func.descr);
                    le_FunctionCode.setText(func.luaCode);
                    break;
                }
            }
        }
        else{
            ((LinearLayout)findViewById(R.id.layout_EditFunctionButtons)).removeView(btn_DeleteFunction);
            ((LinearLayout)findViewById(R.id.layout_EditFunctionButtons)).removeView(btn_RunFunction);
            ((LinearLayout)findViewById(R.id.layout_EditFunctionButtons)).removeView(findViewById(R.id.space_rightOfDeleteFunctionButton));
            ((LinearLayout)findViewById(R.id.layout_EditFunctionButtons)).removeView(findViewById(R.id.space_rightOfRunFunctionButton));
            btn_SaveFunction.setText("Создать");
        }
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent event){
        VoskActivity.hideSoftKeyboard(this);
        return false;
    }*/
    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event){
        VoskActivity.hideSoftKeyboard(this);
        return super.dispatchTouchEvent(event);
    }*/

    private void saveChanges()
    {
        String functionName = le_FunctionName.getText().toString();
        String functionDescription = le_FunctionDescription.getText().toString();
        String functionCode = le_FunctionCode.getText().toString();
        if(functionId==0){
            for (ExecutableFunction func : VoskActivity.functions){
                if (functionName.equals(func.name)) {
                    Toast.makeText(getApplicationContext(), "Функция '" + functionName + "' уже существует.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            currentFunction = new ExecutableFunction(functionName, functionCode, functionDescription, null);
            VoskActivity.addExecutableFunction(currentFunction);
            Toast.makeText(getApplicationContext(), "Функция команда " + functionName, Toast.LENGTH_SHORT).show();
            goBack();
        }
        else{
            for(ExecutableFunction func : VoskActivity.functions){
                if (functionName.equals(func.name) && func!=currentFunction){
                    Toast.makeText(getApplicationContext(), "Функция '" + functionName + "' уже существует.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            currentFunction.name = functionName;
            currentFunction.luaCode = functionCode;
            currentFunction.descr = functionDescription;
            Toast.makeText(getApplicationContext(), "Функция '" + functionName + "' отредактирована.", Toast.LENGTH_SHORT).show();
            VoskActivity.updateExecutableFunction(currentFunction);
            goBack();
        }
    }

    private void deleteFunction()
    {
        //Диалоговое окно "Вы уверены, что вы уверены?"
        if(functionId!=0) VoskActivity.removeExecutableFunction(currentFunction);
        goBack();
    }

    private void runFunction()
    {
        //Run, Forest!
    }

    private void goBack()
    {
        finish();
    }
}