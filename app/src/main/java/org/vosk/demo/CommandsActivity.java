package org.vosk.demo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CommandsActivity extends AppCompatActivity {
    private ImageButton imageButton_AddCommand;
    private TableLayout tableLayout_Commands;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commands);
        tableLayout_Commands = findViewById(R.id.tableLayout_Commands);
        imageButton_AddCommand = findViewById(R.id.imageButton_AddCommand);
        imageButton_AddCommand.setOnClickListener(view -> openEditCommandActivity());
        findViewById(R.id.imageButton_closeCommandsActivity).setOnClickListener(view -> goBack());

        updateTable();
    }

    private void addTableRow(Command com){
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow tr = (TableRow) inflater.inflate(R.layout.table_row_2_cells, null);
        tr.setClickable(true);
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommandsActivity.this, EditCommandActivity.class);
                intent.putExtra("commandId", com.getId());
                startActivity(intent);
            }
        });

        TextView tv0 = (TextView) tr.findViewById(R.id.col0);
        int index = tableLayout_Commands.indexOfChild(tableLayout_Commands.findViewById(R.id.plusRowCommands));
        tv0.setText(com.name);
        TextView tv1 = (TextView) tr.findViewById(R.id.col1);
        tv1.setText(com.func.name);
        tableLayout_Commands.addView(tr, index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTable();
    }

    private void updateTable()
    {
        tableLayout_Commands.removeViews(1,tableLayout_Commands.getChildCount()-2);
        fillTable();
    }

    private void fillTable() {
        for (Command com : VoskActivity.commands){
            addTableRow(com);
        }
    }

    private void openEditCommandActivity(){
        Intent intent = new Intent(CommandsActivity.this, EditCommandActivity.class);
        intent.putExtra("commandId", 0);
        startActivity(intent);
    }
	
    private void goBack() {
        finish();
    }
}