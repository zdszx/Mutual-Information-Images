package cake.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
/*
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
*/

/**
 * @author Jean Vitor de Paulo Util class for helping on image processing
 */
public class ImageUtils {

    /**
     * Process a image to obtain its alpha channel
     *
     * @param BufferedImage originalImage - The image to be processed
     * @return BufferedImage originalImage - The alpha channel of the given
     * image
     */
    public static BufferedImage getAlpha(BufferedImage originalImage) {
        int RGBPixel;
        int alphaPixel;
        for (int i = 0; i <= originalImage.getWidth() - 1; i++) {
            for (int j = 0; j <= originalImage.getHeight() - 1; j++) {

                RGBPixel = originalImage.getRGB(i, j);
                alphaPixel = (RGBPixel >> 24) & 0xff;

                originalImage.setRGB(i, j, (alphaPixel << 24));
            }
        }
        return originalImage;
    }

    /**
     * Process a image to obtain a value of a pixel based on binary shifting
     *
     * @param BufferedImage originalImage - The image to be processed
     * @return BufferedImage originalImage - The blue channel of the given image
     */
    public static BufferedImage getChannel(BufferedImage localImage, int shiftValue) {
        int RGBPixel, channelPixel;
        for (int i = 0; i <= localImage.getWidth() - 1; i++) {
            for (int j = 0; j <= localImage.getHeight() - 1; j++) {

                RGBPixel = localImage.getRGB(i, j);
                channelPixel = RGBPixel >> shiftValue & 0xff;

                localImage.setRGB(i, j, (channelPixel << shiftValue));
            }
        }
        return localImage;
    }

    /**
     * Process a image to obtain its blue channel
     *
     * @param BufferedImage originalImage - The image to be processed
     * @return BufferedImage originalImage - The red channel of the given image
     */
    public static BufferedImage getBlue(BufferedImage originalImage) {
        return getChannel(copyImage(originalImage), 0);
    }

    /**
     * Process a image to obtain its red channel
     *
     * @param BufferedImage originalImage - The image to be processed
     * @return BufferedImage originalImage - The red channel of the given image
     */
    public static BufferedImage getRed(BufferedImage originalImage) {
        return getChannel(copyImage(originalImage), 16);
    }

    /**
     * Process a image to obtain its gren channel
     *
     * @param BufferedImage originalImage - The image to be processed
     * @return BufferedImage originalImage - The green channel of the given
     * image
     *
     */
    public static BufferedImage getGreen(BufferedImage originalImage) {
        return getChannel(copyImage(originalImage), 8);
    }

    /**
     * Process a image to obtain a 8 bit grayscale representation
     *
     * @param BufferedImage originalImage - The image to be processed
     * @return BufferedImage img - The 8bit grayscale of the given image
     */
    public static BufferedImage getGrayScale8bits(BufferedImage inputImage) {
        BufferedImage img = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = img.getGraphics();
        g.drawImage(inputImage, 0, 0, null);
        g.dispose();
        return img;
    }

    /**
     * Rescale an image
     *
     * @param BufferedImage originalImage - The image to be processed
     * @param int width - The new width
     * @param int height - The new height
     * @return BufferedImage resizedImage - The image with the new size
     *
     */
    public static Image getScaledImage(BufferedImage image, int width, int height) {
        Image im = image;
        double scale;
        double imwidth = image.getWidth();
        double imheight = image.getHeight();
        if (width > imwidth && height > imheight) {
            im = image;
        } else if (width / imwidth < height / imheight) {
            scale = width / imwidth;
            im = image.getScaledInstance((int) (scale * imwidth), (int) (scale * imheight), Image.SCALE_SMOOTH);
        } else if (width / imwidth > height / imheight) {
            scale = height / imheight;
            im = image.getScaledInstance((int) (scale * imwidth), (int) (scale * imheight), Image.SCALE_SMOOTH);
        } else if (width / imwidth == height / imheight) {
            scale = width / imwidth;
            im = image.getScaledInstance((int) (scale * imwidth), (int) (scale * imheight), Image.SCALE_SMOOTH);
        }
        return im;
    }

    /**
     * Create a array of ints, representing the histogram of a given image
     *
     * @param BufferedImage originalImage - The image to be processed
     * @param int numberOfBins - Number of histogram bins
     * @return int bins[] - The array containing the occurrence of each
     * intensity pixel (the histogram)
     */
    public static int[] buildHistogram(BufferedImage image, int numberOfBins) {
        int bins[] = new int[numberOfBins];
        int intensity;
        image = getGrayScale8bits(image);
        for (int i = 0; i <= image.getWidth() - 1; i++) {
            for (int j = 0; j <= image.getHeight() - 1; j++) {
                intensity = image.getRGB(i, j) & 0xFF;
                bins[intensity]++;
            }
        }
        return bins;
    }
// get image signal as a one-dim array and for now its grayscale
     public static int[] build1OneDimsignal(BufferedImage image) {
        int imagearray[] = new int[image.getWidth()*image.getHeight()];
        int intensity; int k=0;
        image = getGrayScale8bits(image);
        for (int i = 0; i <= image.getWidth() - 1; i++) {
            for (int j = 0; j <= image.getHeight() - 1; j++) {
                intensity = image.getRGB(i, j) & 0xFF;
                imagearray[k++]=intensity;
            }
        }
        return imagearray;
    }

    
    
    /**
     * Compute the entropy of an image based on the Shannon's Formula
     *
     * @param BufferedImage originalImage - The image to be processed
     * @param int maxValue - The maximum value of intensity pixels, the same
     * number as the histogram bins
     * @return int entropyValue - The entropy value
     */
    public static double getEntropy(BufferedImage image, int maxValue) {
        int bins[] = buildHistogram(image, maxValue);
        double entropyValue = 0, temp = 0;
        double totalSize = image.getHeight() * image.getWidth(); //total size of all symbols in an image

        for (int i = 0; i < maxValue; i++) { //the number of times a sybmol has occured
            if (bins[i] > 0) { //log of zero goes to infinity
                temp = (bins[i] / totalSize) * (Math.log(totalSize / bins[i]));
                entropyValue += temp;
            }
        }
        return entropyValue;
    }
    
    //compute entropy and output .csv of int bins[]
      public static double getEntropyWithCsv(BufferedImage image, int maxValue, int flag) {
        int bins[] = buildHistogram(image, maxValue);
        int imagearray[]=build1OneDimsignal(image);
     //   createCSV(bins,maxValue,flag);     //zds220310
        createCSV(imagearray, image.getHeight() * image.getWidth(),flag+1000);
        
        double entropyValue = 0, temp = 0;
        double totalSize = image.getHeight() * image.getWidth(); //total size of all symbols in an image

        for (int i = 0; i < maxValue; i++) { //the number of times a sybmol has occured
            if (bins[i] > 0) { //log of zero goes to infinity
                temp = (bins[i] / totalSize) * (Math.log(totalSize / bins[i]));
                entropyValue += temp;
            }
        }
        return entropyValue;
    }
    
    
    //zds220310
     public static void createCSV(int bins[],int maxValue,int flag){
        File csvFile = null;
        BufferedWriter csvWtriter = null;
        List<List<Object>> dataList = new ArrayList<List<Object>>();
        List<Object> rowList = null;
        
         for (int i = 0; i < maxValue; i++) { 
             rowList = new ArrayList<Object>();
             rowList.add( bins[i]); //System.out.printf("%d",bins[i]);System.out.printf(" ");
             dataList.add(rowList);
         }
         
        String fileName = "r"+flag+".csv";//????????????
        String filePath = ".\\"; //????????????
        
        try {
            csvFile = new File(filePath + fileName);
            File parent = csvFile.getParentFile();
           if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
           csvFile.createNewFile();
            // GB2312????????????????????????","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 1024);

            int num = 1;
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < num; i++) {
                buffer.append(" ,");
             }
          //   csvWtriter.write(buffer.toString() + fileName + buffer.toString());
          //csvWtriter.newLine();
           // ??????????????????
            for (List<Object> row : dataList) {
               writeRow(row, csvWtriter);
           }
            csvWtriter.flush();
         } catch (Exception e) {
            e.printStackTrace();
       } finally {
             try {
                csvWtriter.close();      System.out.println("csv close");
            } catch (IOException e) {
                 e.printStackTrace();
            }
       }

    }
    
    
    private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
         for (Object data : row) {
           StringBuffer sb = new StringBuffer();
           String rowStr = sb.append(data).toString();
           //String rowStr = sb.append("\"").append(data).append("\",").toString();
           csvWriter.write(rowStr);
       }
       csvWriter.newLine();
    }
    
   
    
    //zds220309
    public static double getMutualInformation(BufferedImage image, int maxValue) {
        int bins[] = buildHistogram(image, maxValue);
        double entropyValue = 0, temp = 0;
        double totalSize = image.getHeight() * image.getWidth(); //total size of all symbols in an image

        for (int i = 0; i < maxValue; i++) { //the number of times a sybmol has occured
            if (bins[i] > 0) { //log of zero goes to infinity
                temp = (bins[i] / totalSize) * (Math.log(totalSize / bins[i]));
                entropyValue += temp;
            }
        }
        return entropyValue;
    }
    

    
    
    /*
    public static double getEntropy(Image image, int maxValue) {
        return getEntropy(toBufferedImage(image), maxValue);
    }
*/
    
    
    /**
     * Make a copy of a buffered image based on arraycopy
     *
     * @param source the image
     * @return an copy of the source
     */
    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage bi = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        byte[] sourceData = ((DataBufferByte) source.getRaster().getDataBuffer()).getData();
        byte[] biData = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourceData, 0, biData, 0, sourceData.length);
        return bi;
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
}
