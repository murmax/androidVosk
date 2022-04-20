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
        imageButton_AddFunction.setOnClickListener(view -> openEditFunctionActivity());
        findViewById(R.id.imageButton_closeFunctionsActivity).setOnClickListener(view -> goBack());
        updateTable();
    }

    private void addTableRow(ExecutableFunction func){
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow tr = (TableRow) inflater.inflate(R.layout.table_row_2_cells, null);
        tr.setClickable(true);
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionsActivity.this, EditFunctionActivity.class);
                intent.putExtra("functionId", func.getId());
                startActivity(intent);
            }
        });

        TextView tv0 = (TextView) tr.findViewById(R.id.col0);
        int index = tableLayout_Functions.indexOfChild(tableLayout_Functions.findViewById(R.id.plusRowFunctions));
        tv0.setText(func.name);
        TextView tv1 = (TextView) tr.findViewById(R.id.col1);
        tv1.setText(func.descr);
        tableLayout_Functions.addView(tr, index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTable();
    }

    private void updateTable()
    {
        tableLayout_Functions.removeViews(1,tableLayout_Functions.getChildCount()-2);
        fillTable();
    }

    private void fillTable() {
        for (ExecutableFunction func : VoskActivity.functions){
            addTableRow(func);
        }
    }

    private void openEditFunctionActivity(){
        Intent intent = new Intent(FunctionsActivity.this, EditFunctionActivity.class);
        intent.putExtra("functionId", 0);
        startActivity(intent);
    }

    private void goBack() {
        finish();
    }
}