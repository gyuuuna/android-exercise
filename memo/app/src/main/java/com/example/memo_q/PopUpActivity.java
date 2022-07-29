package com.example.memo_q;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class PopUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        EditText editText = (EditText) findViewById(R.id.popup_text);
        editText.setText(name);

        Button yesButton = findViewById(R.id.popup_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                finishIntent.putExtra("name", editText.getText().toString());
                setResult(9001, finishIntent);
                finish();
            }
        });

        Button noButton = findViewById(R.id.popup_no);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                setResult(9000, finishIntent);
                finish();
            }
        });


    }
}