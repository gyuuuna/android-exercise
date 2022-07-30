package com.example.memo_q;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class DirNamePopupActivity extends AppCompatActivity {

    private void setNameEditText(String name){
        EditText editText = findViewById(R.id.popup_text);
        editText.setText(name);
    }

    private void setOnClickOfApproveBtn(){
        Button approveButton = findViewById(R.id.popup_yes);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.popup_text);
                Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                finishIntent.putExtra("name", editText.getText().toString());
                setResult(ResultCode.APPROVE, finishIntent);
                finish();
            }
        });
    }

    private void setOnClickOfCancelBtn(){
        Button cancelButton = findViewById(R.id.popup_no);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent finishIntent = new Intent(getApplicationContext(), MainActivity.class);
                setResult(ResultCode.CANCEL, finishIntent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        setNameEditText(name);

        setOnClickOfApproveBtn();
        setOnClickOfCancelBtn();
    }
}