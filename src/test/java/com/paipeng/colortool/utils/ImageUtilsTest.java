package com.paipeng.colortool.utils;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtilsTest {
    @Test
    public  void testCreateCMYK() throws IOException {
        String image = "/Users/paipeng/Downloads/NanoGrid_0165000000040001.tif";

        String outputFile = "/Users/paipeng/cmyk-test.tif";
        BufferedImage bufferedImage = ImageIO.read(new File(image));

        double[] brightCMYK = new double[4];
        double[] darkCMYK = new double[4];

        brightCMYK[0] = 1.0;
        brightCMYK[1] = 0.0;
        brightCMYK[2] = 0.0;
        brightCMYK[3] = 0.0;

        darkCMYK[0] = 0.0;
        darkCMYK[1] = 1.0;
        darkCMYK[2] = 0.0;
        darkCMYK[3] = 0.0;


        BufferedImage cmykBufferedImage = ImageUtils.copyBUfferedImageRGBToCMYK(bufferedImage, brightCMYK, darkCMYK);

        File desitnationFileName = new File(outputFile);
        ImageUtils.saveBufferedImageToTiff(cmykBufferedImage, 600, desitnationFileName);
    }
}
