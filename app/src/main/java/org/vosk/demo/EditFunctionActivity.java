package org.vosk.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;

public class EditFunctionActivity extends AppCompatActivity {
    TableLayout tableLayout_Args;
    Button btn_SaveFunction;
    Button btn_RunFunction;
    Button btn_deleteExec;
    ExecutableFunction func = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_function);

        btn_SaveFunction = findViewById(R.id.btn_SaveFunction);
        btn_RunFunction = findViewById(R.id.btn_RunFunction);
        btn_deleteExec = findViewById(R.id.btn_deleteExec);
        tableLayout_Args = findViewById(R.id.tableLayout_Args);
        btn_SaveFunction.setOnClickListener(view -> saveChanges());
        btn_RunFunction.setOnClickListener(view -> runFunction());
        btn_deleteExec.setOnClickListener(view -> deleteFunction());
        findViewById(R.id.imageButton_closeEditFunctionActivity).setOnClickListener(view -> goBack());

        int b = (int) getIntent().getExtras().get("func");
        for (ExecutableFunction f : VoskActivity.functions
        ) {
            if (f.getId() == b)
            {
                this.func = f;
                break;
            }
        }
        if (this.func == null) {
            return;
        }


        fillData();
    }

    private void fillData(){

        EditText le_name = findViewById(R.id.le_functionName);
        le_name.setText(func.name);
        EditText le_code = findViewById(R.id.le_222);
        le_code.setText(func.luaCode);

        /*for (Map.Entry<String, String> entry : func.args.entrySet()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            TableRow tr = (TableRow) inflater.inflate(R.layout.table_row, null);
            TextView tv0 = tr.findViewById(R.id.col0);
            tv0.setText(entry.getKey());
            TextView tv1 = tr.findViewById(R.id.col1);
            tv1.setText("String");
            tableLayout_Args.addView(tr);
        }*/


    }

    private void saveChanges()
    {

        EditText le_name = findViewById(R.id.le_functionName);
        EditText le_code = findViewById(R.id.le_222);
        func.name = le_name.getText().toString();
        func.luaCode = le_code.getText().toString();
        VoskActivity.updateExecutableFunction(func);
        finish();
    }


    private void deleteFunction()
    {

        VoskActivity.removeExecutableFunction(func);
        finish();

    }

    private void runFunction()
    {

        finish();
    }

    private void goBack()
    {
        finish();
    }
}