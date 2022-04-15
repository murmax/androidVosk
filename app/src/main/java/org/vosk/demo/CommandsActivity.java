package org.vosk.demo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.vosk.demo.databinding.ActivityCommandsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommandsActivity extends AppCompatActivity {
    private ImageButton imageButton_AddCommand;
    private TableLayout tableLayout_Commands;
    private HashMap<Integer,Integer> mapRowIdToCommandId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commands);
        mapRowIdToCommandId = new HashMap<>();
        tableLayout_Commands = findViewById(R.id.tableLayout_Commands);
        imageButton_AddCommand = findViewById(R.id.imageButton_AddCommand);
        //imageButton_AddCommand.setOnClickListener(view -> addTableRow());
        //imageButton_AddCommand.setOnClickListener(view -> addNewCommand());

        imageButton_AddCommand.setOnClickListener(view -> openEditCommandActivity());
        //addTableRow(new Command("com1","phonetic com1", VoskActivity.functions.get(0),null));
        findViewById(R.id.imageButton_closeCommandsActivity).setOnClickListener(view -> goBack());

        /*findViewById(R.id.btn_addCommand).setOnClickListener(view -> addNewCommand());
        Spinner spinner = (Spinner) findViewById(R.id.cb_executable);
        ArrayList<String> names = new ArrayList<>();
        for (ExecutableFunction func: VoskActivity.functions
             ) {
            names.add(func.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Функция:");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
		*/
        updateTable();
    }

    private void addTableRow(Command com){
        LayoutInflater inflater = LayoutInflater.from(this);
        TableRow tr = (TableRow) inflater.inflate(R.layout.table_row, null);
        tr.setClickable(true);
        mapRowIdToCommandId.put(tr.getId(), com.getId());
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Command foundCom = null;
                int comId = mapRowIdToCommandId...
                String vName = ((TextView)((TableRow)v).findViewById(R.id.col0)).getText().toString();
                for (Command com : VoskActivity.commands
                ) {
                    if (com.name == vName)
                    {
                        foundCom = com;
                        break;
                    }
                }
                */

                /*for (Command com : VoskActivity.commands
                ) {
                    if (com.getId() == v.getId())
                    {
                        foundCom = com;
                        break;
                    }
                }*/
                /*if (foundCom!=null) {
                    Intent intent = new Intent(CommandsActivity.this, EditCommandActivity.class);
                    intent.putExtra("commandId", foundCom.getId());
                    startActivity(intent);
                }*/
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
        mapRowIdToCommandId.clear();
        for (Command com : VoskActivity.commands){
            addTableRow(com);
        }
    }

    private void openEditCommandActivity(){
        Intent intent = new Intent(CommandsActivity.this, EditCommandActivity.class);
        intent.putExtra("commandId", 0);
        startActivity(intent);
    }

	/*private void addNewCommand(){
        EditText nameEdit   = (EditText)findViewById(R.id.le_commandName);
        EditText phoneticEdit   = (EditText)findViewById(R.id.le_commandPhonetic);
        String name = nameEdit.getText().toString();
        String phonetic = phoneticEdit.getText().toString();
        ExecutableFunction func = null;
        Spinner spinner = (Spinner) findViewById(R.id.cb_executable);
        func = VoskActivity.findExecutableFunctionByName(spinner.getSelectedItem().toString());

        if (func==null)
        {
            Toast.makeText(getApplicationContext(),"Необходимо выбрать функцию для данной команды.",Toast.LENGTH_SHORT).show();
            return;
        }

        Command com = new Command(name,phonetic,func,null);
        VoskActivity.addCommand(com);
        Toast.makeText(getApplicationContext(),"Создана команда "+name,Toast.LENGTH_SHORT).show();
	}*/
	
    private void goBack() {
        finish();
    }
}