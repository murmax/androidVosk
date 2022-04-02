package org.vosk.demo;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.vosk.demo.databinding.ActivityCommandsBinding;

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

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        findViewById(R.id.btn_backToMenu).setOnClickListener(view -> goToMainMenu());
        findViewById(R.id.btn_addCommand).setOnClickListener(view -> addNewCommand());
    }


    private void goToMainMenu()
    {
        Intent intent = new Intent(this, VoskActivity.class);
        startActivity(intent);
    }

    private void addNewCommand()
    {

        EditText mEdit   = (EditText)findViewById(R.id.le_commandPhonetic);
        String name = mEdit.getText().toString();
        Command com = new Command("'"+name+"'",name,VoskActivity.functions.get(0));
        VoskActivity.commands.add(com);
        Toast.makeText(getApplicationContext(),"Создана команда "+name,Toast.LENGTH_SHORT).show();
    }
}