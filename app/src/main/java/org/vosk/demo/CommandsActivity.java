package org.vosk.demo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.vosk.demo.databinding.ActivityCommandsBinding;

import java.util.ArrayList;

public class CommandsActivity extends AppCompatActivity {

    private ActivityCommandsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCommandsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());

        findViewById(R.id.btn_backToMenu).setOnClickListener(view -> goToMainMenu());
        findViewById(R.id.btn_addCommand).setOnClickListener(view -> addNewCommand());
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

        // Create an ArrayAdapter using the string array and a default spinner layout
        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                names., android.R.layout.simple_spinner_item);*/

        // Specify the layout to use when the list of choices appears
        /*adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);*/
    }


    private void goToMainMenu()
    {
        Intent intent = new Intent(this, VoskActivity.class);
        startActivity(intent);
    }

    private void addNewCommand()
    {

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

        Command com = new Command(name,phonetic,func);
        VoskActivity.addCommand(com);
        Toast.makeText(getApplicationContext(),"Создана команда "+name,Toast.LENGTH_SHORT).show();
    }
}