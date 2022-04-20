package org.vosk.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class EditVariableActivity extends AppCompatActivity {
    EditText le_VariableName;
    Spinner spinner_VariableTypeChoice;
    EditText le_VariableValue;
    EditText le_VariableDescription;
    Button btn_SaveVariable;
    Button btn_DeleteVariable;
    List<String> strList_VariableTypes;
    int variableId;
    Variable currentVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_variable);

        Intent intent = getIntent();
        if(intent.hasExtra("variableId")) {
            variableId = intent.getIntExtra("variableId", 0);
        }
        le_VariableName = findViewById(R.id.le_VariableName);
        spinner_VariableTypeChoice = findViewById(R.id.spinner_VariableTypeChoice);
        le_VariableValue = findViewById(R.id.le_VariableValue);
        le_VariableDescription = findViewById(R.id.le_VariableDescription);
        btn_SaveVariable = findViewById(R.id.btn_SaveVariable);
        btn_DeleteVariable = findViewById(R.id.btn_DeleteVariable);
        btn_SaveVariable.setOnClickListener(view -> saveChanges());
        btn_DeleteVariable.setOnClickListener(view -> deleteVariable());
        findViewById(R.id.imageButton_closeEditVariableActivity).setOnClickListener(view -> goBack());

        strList_VariableTypes = new ArrayList<String>();
        strList_VariableTypes.add("String");
        strList_VariableTypes.add("Int");

        ArrayAdapter<String> adapterVariableTypes = new ArrayAdapter(this, android.R.layout.simple_spinner_item, strList_VariableTypes);
        adapterVariableTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_VariableTypeChoice.setAdapter(adapterVariableTypes);

        currentVariable=null;
        if(variableId!=0){
            for (Variable va : VoskActivity.vars){
                if(va.getId()==variableId){
                    currentVariable = va;
                    le_VariableName.setText(va.name);
                    for(int i = 0; i<adapterVariableTypes.getCount();i++){
                        spinner_VariableTypeChoice.setSelection(adapterVariableTypes.getPosition(va.type));
                    }
                    le_VariableValue.setText(va.value);
                    le_VariableDescription.setText(va.descr);
                    break;
                }
            }
        }
        else{
            ((LinearLayout)findViewById(R.id.layout_EditVariableButtons)).removeView(btn_DeleteVariable);
            ((LinearLayout)findViewById(R.id.layout_EditVariableButtons)).removeView(findViewById(R.id.space_rightOfDeleteVariableButton));
            btn_SaveVariable.setText("Создать");
        }
    }



    private void saveChanges()
    {
        String variableName = le_VariableName.getText().toString();
        String variableType = spinner_VariableTypeChoice.getSelectedItem().toString();
        String variableValue = le_VariableValue.getText().toString();
        String variableDescription = le_VariableDescription.getText().toString();
        if(variableId==0){
            for (Variable va : VoskActivity.vars){
                if (variableName.equals(va.name)) {
                    Toast.makeText(getApplicationContext(), "Переменная '" + variableName + "' уже существует.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            currentVariable = new Variable(variableName, variableType, variableDescription, variableValue, null);//Если будет контейнер типов переменных, то облачить в for как в EditCommand
            VoskActivity.addVariable(currentVariable);
            Toast.makeText(getApplicationContext(), "Создана переменная " + variableName, Toast.LENGTH_SHORT).show();
            goBack();
        }
        else{
            for(Variable va : VoskActivity.vars){
                if (variableName.equals(va.name) && va!=currentVariable){
                    Toast.makeText(getApplicationContext(), "Переменная '" + variableName + "' уже существует.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            currentVariable.name = variableName;//Если будет контейнер типов переменных, то облачить в for как в EditCommand
            currentVariable.type = variableType;
            currentVariable.value = variableValue;
            currentVariable.descr = variableDescription;
            Toast.makeText(getApplicationContext(), "Переменная '" + variableName + "' отредактирована.", Toast.LENGTH_SHORT).show();
            VoskActivity.updateVariable(currentVariable);
            goBack();
        }
    }

    private void deleteVariable() {
        //Диалоговое окно "Вы уверены, что вы уверены?"
        if(variableId!=0) VoskActivity.removeVariable(currentVariable);
        goBack();
    }

    private void goBack()
    {
        finish();
    }
}