package com.paipeng.colortool.utils;

import com.twelvemonkeys.imageio.color.ColorSpaces;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.tiff.Rational;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.metadata.tiff.TIFFEntry;
import com.twelvemonkeys.imageio.plugins.tiff.TIFFImageMetadata;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


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


    public static BufferedImage copyBUfferedImageRGBToCMYK(BufferedImage bufferedImage, double[] brightCMYK, double[] darkCMYK) {
        // I'm using my own TwelveMonkeys ImageIO library for this,
        // but I think you can use the one you used above, like:
        // ColorSpace cmykCS = ColorSpaces.getDeviceCMYKColorSpace()
        ColorSpace cmykCS = ColorSpaces.getColorSpace(ColorSpaces.CS_GENERIC_CMYK);

        // Create CMYK color model, raster and image
        ColorModel colorModel = new ComponentColorModel(cmykCS, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        BufferedImage b = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(bufferedImage.getWidth(), bufferedImage.getHeight()), colorModel.isAlphaPremultiplied(), null);

        /*
        // Paint some sample rectangles on it

        try {
            g.setColor(new java.awt.Color(cmykCS, new float[] {0, 0, 0, 0}, 1.0f)); // All 0 (White)
            g.fillRect(0, 0, 25, 50);
            g.setColor(new java.awt.Color(cmykCS, new float[] {0, 0, 0, 1}, 1.0f)); // Key (Black)
            g.fillRect(25, 0, 25, 50);
            g.setColor(new java.awt.Color(cmykCS, new float[] {1, 0, 0, 0}, 1.0f)); // Cyan
            g.fillRect(50, 0, 50, 50);
            g.setColor(new java.awt.Color(cmykCS, new float[] {0, 1, 0, 0}, 1.0f)); // Magenta
            g.fillRect(0, 50, 50, 50);
            g.setColor(new java.awt.Color(cmykCS, new float[] {0, 0, 1, 0}, 1.0f)); // Yellow
            g.fillRect(50, 50, 50, 50);
        }
        finally {
            g.dispose();
        }
        */
        Graphics2D g = b.createGraphics();
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int pixelColor = bufferedImage.getRGB(x, y);

                if (Color.rgb(pixelColor& 0xFFFFFF >> 16, pixelColor& 0xFFFF >> 8, pixelColor& 0xFF).getBrightness() > 122.0/255) {
                    //int rgb = 0;//((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
                    //int rgb = (blue << 16) | (green & 0x0ff << 8) | (red & 0x0ff);
                    //b.setRGB(x, y, rgb);
                    g.setColor(new java.awt.Color(cmykCS, new float[] {(float) brightCMYK[0], (float) brightCMYK[1], (float) brightCMYK[2], (float) brightCMYK[3]}, 1.0f)); // All 0 (White)
                    g.fillRect(x, y, 1, 1);
                } else {
                    g.setColor(new java.awt.Color(cmykCS, new float[] {(float) darkCMYK[0], (float) darkCMYK[1], (float) darkCMYK[2], (float) darkCMYK[3]}, 1.0f)); // All 0 (White)
                    g.fillRect(x, y, 1, 1);
                }
               //bufferedImage.setRGB(x, y, bufferedImage.getRGB(x, y));
            }
        }
        g.dispose();
        return b;
    }

    public static void saveBufferedImageToTiff(BufferedImage bufferedImage, int printDpi, File desitnationFileName) throws IOException {
        final ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(desitnationFileName);

        if (imageOutputStream == null) {
            throw new IOException("Unable to obtain an output stream for file: " + desitnationFileName);
        }


        // set the byte order to little endian
        // our various imaging systems downstream will not be able to support the
        // big endian (motorola) byte order


        imageOutputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);

        final Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("tif");
        if (imageWriters == null) {
            throw new IOException("Unable to obtain an image writer for the tif file format!");
        }


        // select the first writer available


        final ImageWriter imageWriter = imageWriters.next();
        imageWriter.setOutput(imageOutputStream);
        final ImageWriteParam parameters = imageWriter.getDefaultWriteParam();
        //parameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        //parameters.setCompressionType("CCITT T.6"); // Group 4 Compression
        //parameters.setCompressionQuality(0.2f);

        final List<Entry> entries = new ArrayList<Entry>();
        entries.add(new TIFFEntry(TIFF.TAG_X_RESOLUTION, new Rational(printDpi)));
        entries.add(new TIFFEntry(TIFF.TAG_Y_RESOLUTION, new Rational(printDpi)));

        final IIOMetadata tiffImageMetadata = new TIFFImageMetadata(entries);

        //imageWriter.write(tiffImageMetadata, new IIOImage(exportImage.getColoredBufferedImage(), null, null), parameters);

        imageWriter.write(null, new IIOImage(bufferedImage, null, tiffImageMetadata), parameters);


        imageOutputStream.close();

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
