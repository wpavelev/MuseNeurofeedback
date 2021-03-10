package com.wpavelev.museneurofeedback;

/**
 * Created by Vlad on 29.05.2018.
 */

public class Constants {

    private static Constants instance = null;


    private int upperTime = -1;
    private int upperLimit = -1;
    private int lowerTime = -1;
    private int lowerLimit = -1;

    public final static int FRAMERATE = 60;
    private long lowerTimeCounter = 0;
    private long upperTimeCounter = 0;
    private int pretestTime = 300;
    private int sessionTime = 900;
    private long preTestTimeCounter = 0, preTestLength = FRAMERATE * pretestTime;
    private long sessionTimeCounter = 0, sessionLength = FRAMERATE * sessionTime;

    private boolean useShortTermMean = false;
    private double shortTermMean = 0;

    private boolean useAbsoluteAlpha = false;
    private boolean useManualBaseline = false;
    private double manualBaseline = 2;



   private Constants(){


   };


    public static Constants getInstance() {
        if(instance == null) {
            instance = new Constants();
        }

        return instance;
    }








    public int getUpperTime() {
        return upperTime;
    }

    public int getUpperLimit() {
        return upperLimit;
    }

    public int getLowerTime() {
        return lowerTime;
    }

    public int getLowerLimit() {
        return lowerLimit;
    }


    public long getLowerTimeCounter() {
        return lowerTimeCounter;
    }

    public long getUpperTimeCounter() {
        return upperTimeCounter;
    }

    public int getPretestTime() {
        return pretestTime;
    }

    public int getSessionTime() {
        return sessionTime;
    }

    public long getPreTestTimeCounter() {
        return preTestTimeCounter;
    }

    public long getPreTestLength() {
        return preTestLength;
    }

    public long getSessionTimeCounter() {
        return sessionTimeCounter;
    }

    public long getSessionLength() {
        return sessionLength;
    }

    public boolean useShortTermMean() {
        return useShortTermMean;
    }

    public double getShortTermMean() {
        return shortTermMean;
    }

    public boolean useAbsoluteAlpha() {
        return useAbsoluteAlpha;
    }

    public boolean isUseManualBaseline() {
        return useManualBaseline;
    }

    public double getManualBaseline() {
        return manualBaseline;
    }




    public void setUpperTime(int upperTime) {
        this.upperTime = upperTime;

        setUpperTimeCounter(upperTime * FRAMERATE);
    }

    public void setUpperLimit(int upperLimit) {
        this.upperLimit = upperLimit;
    }

    public void setLowerTime(int lowerTime) {
        this.lowerTime = lowerTime;

        setLowerTimeCounter(lowerTime * FRAMERATE);
    }

    public void setLowerLimit(int lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    private void setLowerTimeCounter(long lowerTimeCounter) {
        this.lowerTimeCounter = lowerTimeCounter;
    }

    private void setUpperTimeCounter(long upperTimeCounter) {
        this.upperTimeCounter = upperTimeCounter;
    }

    public void setPretestTime(int pretestTime) {
        this.pretestTime = pretestTime;
        setPreTestLength(pretestTime * FRAMERATE);

    }

    public void setSessionTime(int sessionTime) {
        this.sessionTime = sessionTime;

        setSessionLength(this.sessionTime * FRAMERATE);
    }

    public void setPreTestTimeCounter(long preTestTimeCounter) {
        this.preTestTimeCounter = preTestTimeCounter;
    }

    private void setPreTestLength(long preTestLength) {
        this.preTestLength = preTestLength;
    }

    public void setSessionTimeCounter(long sessionTimeCounter) {
        this.sessionTimeCounter = sessionTimeCounter;
    }

    private void setSessionLength(long sessionLength) {
        this.sessionLength = sessionLength;
    }

    public void setUseShortTermMean(boolean useShortTermMean) {
        this.useShortTermMean = useShortTermMean;
    }

    public void setShortTermMean(double shortTermMean) {
        this.shortTermMean = shortTermMean;
    }

    public void setUseAbsoluteAlpha(boolean useAbsoluteAlpha) {
        this.useAbsoluteAlpha = useAbsoluteAlpha;
    }

    public void setUseManualBaseline(boolean useManualBaseline) {
        this.useManualBaseline = useManualBaseline;
    }

    public void setManualBaseline(double manualBaseline) {
        this.manualBaseline = manualBaseline;
    }
}
