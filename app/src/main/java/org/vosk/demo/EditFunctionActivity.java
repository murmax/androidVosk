package org.vosk.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class EditFunctionActivity extends AppCompatActivity {
    TableLayout tableLayout_Args;
    Button btn_SaveFunction;
    Button btn_RunFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_function);

        btn_SaveFunction = findViewById(R.id.btn_SaveFunction);
        btn_RunFunction = findViewById(R.id.btn_RunFunction);
        tableLayout_Args = findViewById(R.id.tableLayout_Args);
        btn_SaveFunction.setOnClickListener(view -> saveChanges());
        btn_RunFunction.setOnClickListener(view -> runFunction());
        findViewById(R.id.imageButton_closeEditFunctionActivity).setOnClickListener(view -> goBack());

        addTableRow();
        addTableRow();
    }

    private void addTableRow(){
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow tr = (TableRow) inflater.inflate(R.layout.table_row, null);
        TextView tv0 = (TextView) tr.findViewById(R.id.col0);
        //int index = tableLayout_Args.indexOfChild(tableLayout_Args.findViewById(R.id.plusRow));
        tv0.setText("Arg name");
        TextView tv1 = (TextView) tr.findViewById(R.id.col1);
        tv1.setText("Arg type");
        tableLayout_Args.addView(tr);
    }

    private void saveChanges()
    {

    }

    private void runFunction()
    {

    }

    private void goBack()
    {
        finish();
    }
}