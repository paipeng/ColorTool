package com.paipeng.colortool.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.image.BufferedImage;


public class ImageUtils {
    public static Image convertBufferedImageToImage(BufferedImage bf) {
        WritableImage wr = null;
        if (bf != null) {
            wr = new WritableImage(bf.getWidth(), bf.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bf.getWidth(); x++) {
                for (int y = 0; y < bf.getHeight(); y++) {
                    pw.setArgb(x, y, bf.getRGB(x, y));
                }
            }
        }

        return wr;
    }

    public static BufferedImage changeBufferedImageWithColor(BufferedImage bufferedImage, Color color, int selectedColorPixel) {
        BufferedImage copyBufferedImage = copyImage(bufferedImage);
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int pixelColor = bufferedImage.getRGB(x, y);
                int rgb = ((((int) color.getRed() * 255) & 0x0ff) << 16) | ((((int) color.getGreen() * 255) & 0x0ff) << 8) | (((int) color.getBlue() * 255) & 0x0ff);
                if (selectedColorPixel == 0) {
                    if (pixelColor > 122) {
                        copyBufferedImage.setRGB(x, y, rgb);
                    }
                } else {
                    if (pixelColor <= 122) {
                        copyBufferedImage.setRGB(x, y, rgb);
                    }
                }
                bufferedImage.setRGB(x, y, bufferedImage.getRGB(x, y));
            }
        }
        return copyBufferedImage;
    }


    public static BufferedImage changeBufferedImageWithColor(BufferedImage bufferedImage, Color brightColor, Color darkColor) {
        BufferedImage copyBufferedImage = copyImage(bufferedImage);
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int pixelColor = bufferedImage.getRGB(x, y);
                int rgb = 0;
                if (Color.rgb((pixelColor& 0xFFFFFF) >> 16, (pixelColor& 0xFFFF) >> 8, pixelColor& 0xFF).getBrightness() > 122.0/255) {
                    rgb = (((int) (brightColor.getRed() * 255) & 0x0ff) << 16) | (((int) (brightColor.getGreen() * 255) & 0x0ff) << 8) | ((int) (brightColor.getBlue() * 255) & 0x0ff);
                } else {
                    rgb = (((int) (darkColor.getRed() * 255) & 0x0ff) << 16) | (((int) (darkColor.getGreen() * 255) & 0x0ff) << 8) | ((int) (darkColor.getBlue() * 255) & 0x0ff);
                }
                copyBufferedImage.setRGB(x, y, rgb);

                bufferedImage.setRGB(x, y, bufferedImage.getRGB(x, y));
            }
        }
        return copyBufferedImage;
    }


    public static BufferedImage changeBufferedImageWithColor(BufferedImage bufferedImage, int selectedColorPixel, int red, int green, int blue) {
        BufferedImage copyBufferedImage = copyImage(bufferedImage);
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int pixelColor = bufferedImage.getRGB(x, y);

                if (selectedColorPixel == 0) {
                    if (Color.rgb(pixelColor& 0xFFFFFF >> 16, pixelColor& 0xFFFF >> 8, pixelColor& 0xFF).getBrightness() > 122.0/255) {
                        int rgb = ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
                        //int rgb = (blue << 16) | (green & 0x0ff << 8) | (red & 0x0ff);
                        copyBufferedImage.setRGB(x, y, rgb);
                    }
                }
                bufferedImage.setRGB(x, y, bufferedImage.getRGB(x, y));
            }
        }
        return copyBufferedImage;
    }

    public static Color getBufferedImageBrightColor(BufferedImage bufferedImage) {
        return Color.WHITE;
    }


    public static Color getBufferedImageDarkColor(BufferedImage bufferedImage) {
        return Color.BLACK;
    }


    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static Color convertCMYKToRGB(double cyan, double magenta, double yellow, double key) {
        // R = 255 × (1-C) × (1-K)
        // G = 255 × (1-M) × (1-K)
        // B = 255 × (1-Y) × (1-K)
        double red = (1-cyan) * (1-key);
        double green = (1-magenta) * (1-key);
        double blue = (1-yellow) * (1-key);
        return Color.color(red, green, blue);
    }


    public static double[] convertRGBToCMYK(Color color) {
        double[] cmyk = new double[4];
        double c, m, y, k;

        k = Math.max(color.getRed(), color.getGreen());
        k = 1 - Math.max(k, color.getBlue());

        if (1 - k != 0) {
            c = (1 - color.getRed() - k) / (1 - k);

            m = (1 - color.getGreen() - k) / (1 - k);

            y = (1 - color.getBlue() - k) / (1 - k);
        } else {
            c = 0;
            m = 0;
            y = 0;
        }
        cmyk[0] = c;
        cmyk[1] = m;
        cmyk[2] = y;
        cmyk[3] = k;

        return cmyk;
    }
}
