package org.vosk.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FunctionsActivity extends AppCompatActivity {
    private ImageButton imageButton_AddFunction;
    private TableLayout tableLayout_Functions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);
        tableLayout_Functions = findViewById(R.id.tableLayout_Functions);
        imageButton_AddFunction = findViewById(R.id.imageButton_AddFunction);
        imageButton_AddFunction.setOnClickListener(view -> addNewRow());
        findViewById(R.id.imageButton_closeFunctionsActivity).setOnClickListener(view -> goBack());
    }


    @Override
    protected void onResume() {
        super.onResume();
        fillRows();
    }

    private void fillRows()
    {
        tableLayout_Functions.removeViews(1,tableLayout_Functions.getChildCount()-2);
        //tableLayout_Functions.removeAllViews();
        for (ExecutableFunction func: VoskActivity.functions
             ) {
            addTableRow(func);
        }
    }

    private void addTableRow(ExecutableFunction func){
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow tr = (TableRow) inflater.inflate(R.layout.table_row_2_cells, null);
        tr.setClickable(true);
        tr.setId(func.getId());
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutableFunction foundFunc = null;
                for (ExecutableFunction func : VoskActivity.functions
                ) {
                    if (func.getId() == v.getId())
                    {
                        foundFunc = func;
                        break;
                    }
                }
                if (foundFunc!=null) {
                    Intent intent = new Intent(FunctionsActivity.this, EditFunctionActivity.class);
                    intent.putExtra("func", foundFunc.getId());
                    startActivity(intent);
                }
            }
        });
        TextView tv0 = (TextView) tr.findViewById(R.id.col0);
        int index = tableLayout_Functions.indexOfChild(tableLayout_Functions.findViewById(R.id.plusRowFunctions));
        tv0.setText(func.name);
        TextView tv1 = (TextView) tr.findViewById(R.id.col1);
        tv1.setText(func.descr);
        //tv0.setOnClickListener(view -> openEditFunctionActivity());
        //tv1.setOnClickListener(view -> openEditFunctionActivity());
        tableLayout_Functions.addView(tr, index);
    }

    private void addNewRow()
    {
        String name = "Новая функция";
        while (VoskActivity.findExecutableFunctionByName(name)!=null)
        {
            name = name+"1";
        }
        VoskActivity.addExecutableFunction(new ExecutableFunction(name,"","",null));
        fillRows();
    }



    private void openEditFunctionActivity(View v){
        Intent intent = new Intent(this, EditFunctionActivity.class);
        startActivity(intent);
    }

    private void goBack()
    {
        finish();
    }
}