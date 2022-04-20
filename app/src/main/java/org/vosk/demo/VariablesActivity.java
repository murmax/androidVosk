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

public class VariablesActivity extends AppCompatActivity {
    private ImageButton imageButton_AddVariable;
    private TableLayout tableLayout_Variables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variables);

        tableLayout_Variables = findViewById(R.id.tableLayout_Variables);
        imageButton_AddVariable = findViewById(R.id.imageButton_AddVariable);
        imageButton_AddVariable.setOnClickListener(view -> openEditVariableActivity());
        findViewById(R.id.imageButton_closeVariablesActivity).setOnClickListener(view -> goBack());

        updateTable();
    }

    private void addTableRow(Variable va){
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow tr = (TableRow) inflater.inflate(R.layout.table_row_3_cells, null);
        tr.setClickable(true);
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VariablesActivity.this, EditVariableActivity.class);
                intent.putExtra("variableId", va.getId());
                startActivity(intent);
            }
        });

        TextView tv0 = (TextView) tr.findViewById(R.id.col0);
        int index = tableLayout_Variables.indexOfChild(tableLayout_Variables.findViewById(R.id.plusRowVariables));
        tv0.setText(va.name);
        TextView tv1 = (TextView) tr.findViewById(R.id.col1);
        tv1.setText(va.type);
        TextView tv2 = (TextView) tr.findViewById(R.id.col2);
        tv2.setText(va.value);
        tableLayout_Variables.addView(tr, index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTable();
    }

    private void updateTable()
    {
        tableLayout_Variables.removeViews(1,tableLayout_Variables.getChildCount()-2);
        fillTable();
    }

    private void fillTable() {
        for (Variable va : VoskActivity.vars){
            addTableRow(va);
        }
    }

    private void openEditVariableActivity(){
        Intent intent = new Intent(VariablesActivity.this, EditVariableActivity.class);
        intent.putExtra("variableId", 0);
        startActivity(intent);
    }

    private void goBack() {
        finish();
    }
}