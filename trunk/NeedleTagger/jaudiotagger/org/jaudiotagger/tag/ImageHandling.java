package org.jaudiotagger.tag;

import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.valuepair.ImageFormats;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * User: paul
 * Date: 11-Dec-2008
 */
public class ImageHandling
{
    /**
     * Resize the image until the total size require to store the image is less than maxsize
     * @param artwork
     * @param maxSize
     * @throws IOException
     */
    public static void reduceQuality(Artwork artwork, int maxSize) throws IOException
    {
        while(artwork.getBinaryData().length > maxSize)
        {
            Bitmap srcImage = artwork.getImage();
            int w = srcImage.getWidth();
            int newSize = w /2;
            makeSmaller(artwork,newSize);
        }
    }
     /**
     * Resize image using Java 2D
      * @param artwork
      * @param size
      * @throws java.io.IOException
      */
    private static void makeSmaller(Artwork artwork,int size) throws IOException
    {
      Bitmap srcImage = artwork.getImage();

      int w = srcImage.getWidth();
      int h = srcImage.getHeight();

      // Determine the scaling required to get desired result.
      int scaleW = (int) size / (int) w;
      int scaleH = (int) size / (int) h;
      
      Bitmap bi = Bitmap.createScaledBitmap(srcImage, scaleW, scaleH, true);
      if(artwork.getMimeType()!=null && isMimeTypeWritable(artwork.getMimeType()))
      {
          artwork.setBinaryData(writeImage(bi,artwork.getMimeType()));
      }
      else
      {
          artwork.setBinaryData(writeImageAsPng(bi));
      }
    }

    public static boolean isMimeTypeWritable(String mimeType)
    {
//        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByMIMEType(mimeType);
//        return writers.hasNext();
    	
    	return true;
    }
    /**
     *  Write buffered image as required format
     *
     * @param bi
     * @param mimeType
     * @return
     * @throws IOException
     */
    public static byte[] writeImage(Bitmap bi,String mimeType) throws IOException
    {
    	final ByteArrayOutputStream output = new ByteArrayOutputStream();
    	String type = ImageFormats.getFormatForMimeType(mimeType);
    	bi.compress(CompressFormat.valueOf(type),100,output);
    	return output.toByteArray();
    }

    /**
     *
     * @param bi
     * @return
     * @throws IOException
     */
    public static byte[] writeImageAsPng(Bitmap bi) throws IOException
    {
    	final ByteArrayOutputStream output = new ByteArrayOutputStream();
    	bi.compress(CompressFormat.PNG,100,output);
    	return output.toByteArray();
    }

    /**
     * Show read formats
     *
     * On Windows supports png/jpeg/bmp/gif
     */
    public static void showReadFormats()
    {
//         String[] formats = ImageIO.getReaderMIMETypes();
//        for(String f:formats)
//        {
//            System.out.println("r"+f);
//        }
    }

    /**
     * Show write formats
     *
     * On Windows supports png/jpeg/bmp
     */
    public static void showWriteFormats()
    {
//         String[] formats = ImageIO.getWriterMIMETypes();
//        for(String f:formats)
//        {
//            System.out.println(f);
//        }
    }

}
