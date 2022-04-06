package org.vosk.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class FunctionsActivity extends AppCompatActivity {
    ImageButton imageButton_AddFunction;
    TableLayout tableLayout_Functions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_functions);
        tableLayout_Functions = findViewById(R.id.tableLayout_Functions);
        imageButton_AddFunction = findViewById(R.id.imageButton_AddFunction);
        imageButton_AddFunction.setOnClickListener(view -> addTableRow());
        findViewById(R.id.imageButton_close).setOnClickListener(view -> goToMainMenu());
    }

    private void addTableRow(){
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow tr = (TableRow) inflater.inflate(R.layout.table_row, null);
        TextView tv0 = (TextView) tr.findViewById(R.id.col0);
        int index = tableLayout_Functions.indexOfChild(tableLayout_Functions.findViewById(R.id.plusRow));
        tv0.setText(Integer.toString(index)+"Function name()");
        TextView tv1 = (TextView) tr.findViewById(R.id.col1);
        tv1.setText("Function description. Полностью попадает в таблицу. Пока что без сжатия.");
        tableLayout_Functions.addView(tr, index);
    }

    private void goToMainMenu()
    {
        //Intent intent = new Intent(this, VoskActivity.class);
        //startActivity(intent);
        finish();
    }
}