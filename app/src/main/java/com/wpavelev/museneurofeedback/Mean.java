package com.wpavelev.museneurofeedback;

public class Mean {

    final static String TAG = "Mean";

    private int counter = 0;
    private double sum = 0;
    private double mean = 0;


    public Mean() {

    }


    public void newValue(double value) {
        sum += value;
        counter++;
        mean = sum / counter;


    }

    public double getMean() {
        return mean;
    }

    public void resetValues() {
        counter = 0;
        sum = 0;
        mean = 0;
    }

}
