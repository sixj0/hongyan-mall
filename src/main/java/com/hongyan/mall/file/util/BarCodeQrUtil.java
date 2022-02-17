package com.hongyan.mall.file.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 二维码生成工具
 *
 * @author tudou
 * @version v1.0
 * @date 2018年05月21日 10:43:39
 */
public class BarCodeQrUtil {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * 生成二维码，并输出到流中
     *
     * @param text
     * @param width
     * @param height
     * @param format
     * @param out
     * @throws Exception
     */
    public static void writeToStream(String text, int width, int height, String format,
                                     OutputStream out) throws Exception {
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        //内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width,
                height, hints);
        //生成二维码
        BufferedImage image = toBufferedImage(bitMatrix);
        ImageIO.write(image, format, out);
    }

    public static void mergeBgImageAndBarcodesToStream(File bgImageFile, String[] textArray,
                                                       int startX, int startY, int rowNum,
                                                       int colNum, int eachWidth, int eachHeight,
                                                       int colSpace, int rowSpace, String format,
                                                       OutputStream out) throws Exception {
        BufferedImage bgImage = ImageIO.read(bgImageFile);
        mergeBgImageAndBarcodesToStream(bgImage, textArray, startX, startY, rowNum, colNum,
                eachWidth, eachHeight, colSpace, rowSpace, format, out);
    }

    public static void mergeBgImageAndBarcodesToStream(BufferedImage bgImage, String[] textArray,
                                                       int startX, int startY, int rowNum,
                                                       int colNum, int eachWidth, int eachHeight,
                                                       int colSpace, int rowSpace, String format,
                                                       OutputStream out) throws Exception {
        //读取背景
        Graphics g = bgImage.getGraphics();

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        //内容所使用编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 2);
        BitMatrix[] bitMatrixArray = new BitMatrix[textArray.length];
        for (int i = 0; i < textArray.length; i++) {
            String text = textArray[i];
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,
                    eachWidth, eachHeight, hints);
            bitMatrixArray[i] = bitMatrix;
        }

        //生成二维码
        int x = 0;
        int y = 0 - eachHeight + startY;
        for (int i = 0; i < bitMatrixArray.length; i++) {
            if (i >= colNum * rowNum) {
                break;
            }
            if (i % colNum == 0) {
                x = startX;
                y = y + eachHeight + rowSpace;
            }
            BufferedImage image = toBufferedImage(bitMatrixArray[i]);
            g.drawImage(image, x, y, eachWidth, eachHeight, null);
            x = x + eachWidth + colSpace;
        }
        ImageIO.write(bgImage, format, out);
    }

    /**
     * BitMatrix 转化成 BufferedImage
     *
     * @param matrix
     * @return
     */
    private static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * 生成二维码，并保存到图片服务器
     *
     * @param matrix
     * @param format
     * @param file
     * @throws IOException
     */
    public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    /**
     * 生成二维码
     *
     * @param content
     * @throws Exception
     */
    public static File writeToFile(String content) throws Exception {
        String format = "jpg";
        String fileName = UniqueIdGenerator.generateRandomStr(20) + "." + format;
        File destFile = new File(fileName);

        int width = 300;
        int height = 300;
        Map<EncodeHintType, Object> hints = new HashMap<>(1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        writeToFile(bitMatrix, format, destFile);

        return destFile;
    }

    public static void main(String[] args) throws Exception {
        File file = writeToFile("http://www.hongshigroup.com/");
        System.out.println(file.getName());
    }

}
