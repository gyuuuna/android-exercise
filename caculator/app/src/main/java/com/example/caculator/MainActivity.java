package com.example.caculator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Calculator calculator = new Calculator();
    List<Calculator> history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView degreeText = (TextView)findViewById(R.id.textView4);
        degreeText.setText("RAD");

        history.add(calculator);
    }

    private void printExpression(){
        EditText t = (EditText)findViewById(R.id.editTextTextMultiLine3);
        t.setText(calculator.getExpressionString());
        printAnswerToPreview();
    }

    private void printAnswerToPreview(){
        TextView t = (TextView)findViewById(R.id.editTextTextMultiLine4);
        t.setText(calculator.getCalculationResult());
    }

    private void printAnswer(){
        EditText t = (EditText) findViewById(R.id.editTextTextMultiLine3);
        t.setText(calculator.getCalculationResult());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void write(View view){
        if(view.getTransitionName().equals("^2")){
            calculator.addUnitExpression("^");
            calculator.addUnitExpression("2");
        }
        else calculator.addUnitExpression(view.getTransitionName());
        printExpression();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writeBracket(View view){
        calculator.addBracket();
        printExpression();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void writeFunction(View view){
        if(view.getTransitionName().equals("e^")){
            calculator.addUnitExpression("e");
            calculator.addUnitExpression("^");
        }
        else if(view.getTransitionName().equals("10^")){
            calculator.addUnitExpression("10");
            calculator.addUnitExpression("^");
        }
        else{
            calculator.addUnitExpression(view.getTransitionName());
            calculator.addUnitExpression("(");
        }
        printExpression();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void delete(View view){
        calculator.removeRecentUnitExpression();
        printExpression();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteAll(View view){
        calculator.removeAllUnitExpression();
        printExpression();
    }

    public void result(View view){
        printAnswer();
        TextView t = (TextView) findViewById(R.id.editTextTextMultiLine4);
        t.setText("");

        String answer = calculator.getCalculationResult();
        calculator = new Calculator();
        calculator.addUnitExpression(answer);
        history.add(calculator);
    }

    @SuppressLint("SetTextI18n")
    public void toggleDegree(View view){
        calculator.degree = !calculator.degree;
        TextView degreeText = (TextView)findViewById(R.id.textView4);

        if(view.getTransitionName().equals("DEG")){
            view.setTransitionName("RAD");
            Button DEG = (Button) findViewById(R.id.button216);
            DEG.setText("RAD");
            degreeText.setText("DEG");
        }
        else{
            view.setTransitionName("DEG");
            Button DEG = (Button) findViewById(R.id.button216);
            DEG.setText("DEG");
            degreeText.setText("RAD");
        }
        printExpression();
    }

    @SuppressLint("SetTextI18n")
    public void inv(View view){
        Button button;

        // sin
        button = (Button) findViewById(R.id.button253);
        if(button.getText().equals("sin")){
            button.setText("asin");
            button.setTransitionName("asin");
        }
        else{
            button.setText("sin");
            button.setTransitionName("sin");
        }

        // cos
        button = (Button) findViewById(R.id.button254);
        if(button.getText().equals("cos")){
            button.setText("acos");
            button.setTransitionName("acos");
        }
        else{
            button.setText("cos");
            button.setTransitionName("cos");
        }

        // tan
        button = (Button) findViewById(R.id.button255);
        if(button.getText().equals("tan")){
            button.setText("atan");
            button.setTransitionName("atan");
        }
        else{
            button.setText("tan");
            button.setTransitionName("tan");
        }

        // ln
        button = (Button) findViewById(R.id.button250);
        if(button.getText().equals("ln")){
            button.setText("e^x");
            button.setTransitionName("e^");
        }
        else{
            button.setText("ln");
            button.setTransitionName("ln");
        }


        // log
        button = (Button) findViewById(R.id.button251);
        if(button.getText().equals("log")){
            button.setText("10^x");
            button.setTransitionName("10^");
        }
        else{
            button.setText("log");
            button.setTransitionName("log");
        }

        // √
        button = (Button) findViewById(R.id.button60);
        if(button.getText().equals("√")){
            button.setText("x^2");
            button.setTransitionName("^2");
        }
        else{
            button.setText("√");
            button.setTransitionName("√");
        }

        printExpression();
    }

}