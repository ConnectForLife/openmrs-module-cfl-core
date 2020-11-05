package org.openmrs.module.cfl.api.contract;

public class VisitInformation {
    private int doseNumber;
    private String nameOfDose;
    private int midPointWindow;
    private int lowWindow;
    private int upWindow;
    private int numberOfFutureVisit;

    public int getDoseNumber() {
        return doseNumber;
    }

    public void setDoseNumber(int doseNumber) {
        this.doseNumber = doseNumber;
    }

    public String getNameOfDose() {
        return nameOfDose;
    }

    public void setNameOfDose(String nameOfDose) {
        this.nameOfDose = nameOfDose;
    }

    public int getMidPointWindow() {
        return midPointWindow;
    }

    public void setMidPointWindow(int midPointWindow) {
        this.midPointWindow = midPointWindow;
    }

    public int getLowWindow() {
        return lowWindow;
    }

    public void setLowWindow(int lowWindow) {
        this.lowWindow = lowWindow;
    }

    public int getUpWindow() {
        return upWindow;
    }

    public void setUpWindow(int upWindow) {
        this.upWindow = upWindow;
    }

    public int getNumberOfFutureVisit() {
        return numberOfFutureVisit;
    }

    public void setNumberOfFutureVisit(int numberOfFutureVisit) {
        this.numberOfFutureVisit = numberOfFutureVisit;
    }
}