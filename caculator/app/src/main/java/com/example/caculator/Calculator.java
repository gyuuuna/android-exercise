package com.example.caculator;

import android.widget.EditText;
import android.widget.Switch;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class Calculator {
    private static int idCount = 0;
    int id;
    private List<String> expression = new ArrayList<>();
    public boolean degree = false;

    Calculator(){ id = idCount++; }

    String getExpressionString(){
        String expressionString = "";
        for(String unitExpression: expression){
            expressionString+=unitExpression;
        }
        return expressionString;
    }

    private boolean isDouble(String string){
        try{
            Double.parseDouble(string);
            return true;
        } catch(Exception e){
            switch(string) {
                case "π": case "e":
                    return true;
                default:
                    return false;
            }
        }
    }

    private double toDouble(String string){
        try{
            return Double.parseDouble(string);
        } catch(Exception e){
            switch(string) {
                case "π":
                    return Math.PI;
                case "e":
                    return Math.E;
                default:
                    return 0;
            }
        }
    }

    private List<String> clean(List<String> expression){
        List<String> cleanedExpression = new ArrayList<>();
        cleanedExpression.addAll(expression);
        boolean cleaned = false;
        for(int i = cleanedExpression.size()-1; i>=0 && !cleaned; i--){
            if(isDouble(cleanedExpression.get(i))) break;
            switch(cleanedExpression.get(i)){
                case ")": case "!": case "%":
                    cleaned = true;
                    break;
                default:
                    cleanedExpression.remove(i);
            }
        }
        return cleanedExpression;
    }


    String getCalculationResult(){
        try{
            return Double.toString(calculationResult(toPostfix(clean(expression))));
        } catch(Exception e){
            return "연산 오류!";
        }
    }

    private List<String> toPostfix(List<String> expression){
        Stack<String> opStack = new Stack<>();
        List<String> postfixExpression = new ArrayList<>();

        for(int i=0; i<expression.size(); i++){
            if(isDouble(expression.get(i))){
                postfixExpression.add(expression.get(i));
                if(!opStack.empty()){
                    if(opStack.peek().equals("√")){
                        postfixExpression.add(opStack.peek());
                        opStack.pop();
                    }
                }
            }
            else if(opStack.empty())
                opStack.add(expression.get(i));
            else if(!expression.get(i).equals(")") && !expression.get(i).equals("(") && !opStack.peek().equals("(")){
                switch (expression.get(i)){
                    case "!": case "%":
                        postfixExpression.add(expression.get(i));
                        break;
                    case "*": case "/": case "^":
                        if (!opStack.peek().equals("+") && !opStack.peek().equals("-")) {
                            while (!opStack.empty() && !opStack.peek().equals("+") && !opStack.peek().equals("-")) {
                                postfixExpression.add(opStack.peek());
                                opStack.pop();
                            }
                        }
                        opStack.add(expression.get(i));
                        break;
                    case "+": case "-":
                        while (!opStack.empty()) {
                            postfixExpression.add(opStack.peek());
                            opStack.pop();
                        }
                        opStack.add(expression.get(i));
                        break;
                    default:
                        opStack.add(expression.get(i));
                }
            }
            else if(expression.get(i).equals("("))
                opStack.add(expression.get(i));
            else if(opStack.peek().equals("("))
                opStack.add(expression.get(i));
            else if(expression.get(i).equals(")")){
                while(!opStack.peek().equals("(")){
                    postfixExpression.add(opStack.peek());
                    opStack.pop();
                }
                opStack.pop();
                if(!opStack.empty()){
                    switch(opStack.peek()){
                        case "sin": case "cos": case "tan":
                        case "asin": case "acos": case "atan":
                        case "ln": case "log": case "√":
                            postfixExpression.add(opStack.peek());
                            opStack.pop();
                            break;
                    }
                }
            }


        }
        while(!opStack.empty()){
            postfixExpression.add(opStack.peek());
            opStack.pop();
        }
        return postfixExpression;
    }

    private boolean isInteger(double d){
        String doubleString = Integer.toString((int)d);
        try{
            Integer.parseInt(doubleString);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    private double factorial(double n){
        if(n==0) return 1;
        else return n*factorial(n-1);
    }

    private double calculationResult(List<String> expression) throws Exception {
        Stack<Double> doubleStack = new Stack<>();

        for(String unitExpression: expression){
            if(isDouble(unitExpression)){
                doubleStack.add(toDouble(unitExpression));
                continue;
            }
            switch(unitExpression){
                case "!":
                    double d1 = doubleStack.peek(), d2;
                    doubleStack.pop();
                    if(isInteger(d1)){
                        doubleStack.add(factorial(d1));
                    }
                    else{
                        throw new Exception();
                        // 도메인 오류 예외처리 !!!!!!!!!!!!!!!
                    }
                    break;
                case "%":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    doubleStack.add(d1*0.01);
                    break;
                case "sin":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    if(degree) doubleStack.add(Math.sin(Math.toRadians(d1)));
                    else doubleStack.add(Math.sin(d1));
                    break;
                case "cos":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    if(degree) doubleStack.add(Math.cos(Math.toRadians(d1)));
                    else doubleStack.add(Math.cos(d1));
                    break;
                case "tan":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    if(degree) doubleStack.add(Math.tan(Math.toRadians(d1)));
                    else doubleStack.add(Math.tan(d1));
                    break;
                case "asin":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    if(degree) doubleStack.add(Math.toDegrees(Math.asin(d1)));
                    else doubleStack.add(Math.asin(d1));
                    break;
                case "acos":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    if(degree) doubleStack.add(Math.toDegrees(Math.acos(d1)));
                    else doubleStack.add(Math.acos(d1));
                    break;
                case "atan":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    doubleStack.add(Math.atan(d1));
                    break;
                case "ln":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    doubleStack.add(Math.log(d1));
                    break;
                case "log":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    doubleStack.add(Math.log10(d1));
                    break;
                case "√":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    doubleStack.add(Math.sqrt(d1));
                    break;
                case "^":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    d2 = doubleStack.peek();
                    doubleStack.pop();
                    doubleStack.add(Math.pow(d2, d1));
                    break;
                case "+":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    d2 = doubleStack.peek();
                    doubleStack.pop();
                    doubleStack.add(d2+d1);
                    break;
                case "-":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    d2 = doubleStack.peek();
                    doubleStack.pop();
                    doubleStack.add(d2-d1);
                    break;
                case "*":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    d2 = doubleStack.peek();
                    doubleStack.pop();
                    doubleStack.add(d2*d1);
                    break;
                case "/":
                    d1 = doubleStack.peek();
                    doubleStack.pop();
                    d2 = doubleStack.peek();
                    doubleStack.pop();
                    doubleStack.add(d2/d1);
                    break;
            }
        }
        if(doubleStack.empty()) return 0.0;
        double result = doubleStack.peek();
        doubleStack.pop();
        if(doubleStack.empty()) return result;
        else throw new Exception();
    }

    void addUnitExpression(String unitExpression){
        // 숫자 또는 . 인지 구분!!!!
        if(expression.isEmpty()) expression.add(unitExpression);
        else if(isDouble(unitExpression) || unitExpression.equals(".")) {
            String recentUnitExpression = getRecentUnitExpression();
            if(isDouble(recentUnitExpression) || recentUnitExpression.equals(".")) {
                removeRecentUnitExpression();
                expression.add(recentUnitExpression + unitExpression);
            }
            else expression.add(unitExpression);
        }
        else expression.add(unitExpression);
    }

    void addBracket(){
        // 숫자 또는 . 인지 구분!!!!
        String recentUnitExpression = getRecentUnitExpression();
        if(recentUnitExpression.equals("empty"))
            expression.add("(");
        else if(isDouble(recentUnitExpression) || recentUnitExpression.equals(")") || recentUnitExpression.equals("%") ||  recentUnitExpression.equals("!")) {
            expression.add(")");
        }
        else expression.add("(");

    }

    String getRecentUnitExpression(){
        if(expression.isEmpty()) return "empty";
        return expression.get(expression.size()-1);
    }

    void removeRecentUnitExpression(){
        if(expression.isEmpty()) return;
        expression.remove(expression.size()-1);
    }

    void removeAllUnitExpression(){
        expression.clear();
    }
}
