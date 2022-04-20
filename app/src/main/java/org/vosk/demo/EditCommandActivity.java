package org.vosk.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditCommandActivity extends AppCompatActivity {
    EditText le_commandName;
    EditText le_commandPhonetics;
    Button btn_SaveCommand;
    Button btn_DeleteCommand;
    Spinner spinner_FunctionChoice;
    List<String> strList_FuncNames;
    int commandId;
    Command currentCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent.hasExtra("commandId")) {
            commandId = intent.getIntExtra("commandId", 0);
        }
        /*//Альтернативный способ получения переданных параметров, если есть вероятность, что они могут быть не переданы.
        Bundle arguments = getIntent().getExtras();

        if(arguments!=null){
            String name = arguments.get("name").toString();
            String company = arguments.getString("company");
            int age = arguments.getInt("age");
            textView.setText("Name: " + name + "\nCompany: " + company +
                    "\nAge: " + age);
        }
        */
        setContentView(R.layout.activity_edit_command);
        le_commandName = findViewById(R.id.le_commandName);
        le_commandPhonetics = findViewById(R.id.le_CommandPhonetics);
        btn_SaveCommand = findViewById(R.id.btn_SaveCommand);
        btn_DeleteCommand = findViewById(R.id.btn_DeleteCommand);
        spinner_FunctionChoice = findViewById(R.id.spinner_FunctionChoice);
        btn_SaveCommand.setOnClickListener(view -> saveChanges());
        btn_DeleteCommand.setOnClickListener(view -> deleteCommand());
        findViewById(R.id.imageButton_closeEditCommandActivity).setOnClickListener(view -> goBack());

        strList_FuncNames = new ArrayList<String>();
        for (ExecutableFunction func : VoskActivity.functions){
            strList_FuncNames.add(func.name);
        }
        ArrayAdapter<String> adapterFunctionNames = new ArrayAdapter(this, android.R.layout.simple_spinner_item, strList_FuncNames);
        adapterFunctionNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_FunctionChoice.setAdapter(adapterFunctionNames);

        currentCommand=null;
        if(commandId!=0){
            for (Command com : VoskActivity.commands){
                if(com.getId()==commandId){
                    currentCommand = com;
                    le_commandName.setText(com.name);
                    le_commandPhonetics.setText(com.phonetic);
                    for(int i =0 ; i<adapterFunctionNames.getCount();i++){
                        spinner_FunctionChoice.setSelection(adapterFunctionNames.getPosition(com.func.name));
                    }
                    break;
                }
            }
        }
        else{
            ((LinearLayout)findViewById(R.id.layout_EditCommandButtons)).removeView(btn_DeleteCommand);
            ((LinearLayout)findViewById(R.id.layout_EditCommandButtons)).removeView(findViewById(R.id.space_rightOfDeleteCommandButton));
            btn_SaveCommand.setText("Создать");
        }
    }


    private void saveChanges()
    {
        String commandName = le_commandName.getText().toString();
        String commandPhonetics = le_commandPhonetics.getText().toString();
        String selectedFunctionName = spinner_FunctionChoice.getSelectedItem().toString();
        if(commandId==0){
            for (Command com : VoskActivity.commands){
                if (commandName.equals(com.name)) {
                    Toast.makeText(getApplicationContext(), "Команда '" + commandName + "' уже существует.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            for (ExecutableFunction func : VoskActivity.functions) {
                if (func.name.equals(selectedFunctionName)) {
                    currentCommand = new Command(commandName, le_commandPhonetics.getText().toString(), func, null);
                    VoskActivity.addCommand(currentCommand);
                    Toast.makeText(getApplicationContext(), "Создана команда " + commandName, Toast.LENGTH_SHORT).show();
                    goBack();
                }
            }
        }
        else{
            for (ExecutableFunction func : VoskActivity.functions) {
                if (func.name.equals(selectedFunctionName)) {
                    currentCommand.name = commandName;
                    currentCommand.func = func;
                    currentCommand.phonetic = commandPhonetics;
                    Toast.makeText(getApplicationContext(), "Команда '" + commandName + "' отредактирована.", Toast.LENGTH_SHORT).show();
                    VoskActivity.updateCommand(currentCommand);
                    goBack();
                }
            }
        }
    }

    private void deleteCommand() {
        //Диалоговое окно "Вы уверены, что вы уверены?"
        if(commandId!=0) VoskActivity.removeCommand(currentCommand);
        goBack();
    }

    private void goBack()
    {
        finish();
    }
}