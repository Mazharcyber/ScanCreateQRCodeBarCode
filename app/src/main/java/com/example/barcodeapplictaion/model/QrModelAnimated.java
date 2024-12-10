package com.example.barcodeapplictaion.model;

public class QrModelAnimated {

    public QrModelAnimated(){
        ///////empty constructor////////////////
    }
    private int plate_imag;
    private String titleText;

    public QrModelAnimated(int plate_imag, String titleText) {
        this.plate_imag = plate_imag;
        this.titleText = titleText;
    }
    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }
    public int getPlate_imag() {
        return plate_imag;
    }

    public void setPlate_imag(int plate_imag) {
        this.plate_imag = plate_imag;
    }
}
