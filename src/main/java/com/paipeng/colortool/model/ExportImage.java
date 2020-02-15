package com.paipeng.colortool.model;

import javafx.scene.paint.Color;

import java.awt.image.BufferedImage;

public class ExportImage {
    private Color brightColor;
    private Color darkColor;
    private double[] cmyk;
    private BufferedImage bufferedImage;
    private BufferedImage coloredBufferedImage;

    public Color getBrightColor() {
        return brightColor;
    }

    public void setBrightColor(Color brightColor) {
        this.brightColor = brightColor;
    }

    public Color getDarkColor() {
        return darkColor;
    }

    public void setDarkColor(Color darkColor) {
        this.darkColor = darkColor;
    }

    public double[] getCmyk() {
        return cmyk;
    }

    public void setCmyk(double[] cmyk) {
        this.cmyk = cmyk;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getColoredBufferedImage() {
        return coloredBufferedImage;
    }

    public void setColoredBufferedImage(BufferedImage coloredBufferedImage) {
        this.coloredBufferedImage = coloredBufferedImage;
    }
}
