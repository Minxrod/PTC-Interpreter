package petitcomputer;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * A single character: component of sprite, background, etc.
 * @author minxr
 */
public class CHR {
    private byte[] data; //each byte is two pixels
    private BufferedImage image;
    int savedPal;
    
    public CHR(){
        data = new byte[4*8];
    }
    
    public void setData(byte[] newData){
        data = newData;
        image = null;
    }
    
    public byte[] getData(){
        return data;
    }
    
    /**
     * Generates the image data from a default palette.
     * Required to run only once to initialize character data,
     * or to update the image after new data has been set.
     * This is done to avoid issues where a palette has two or more
     * of the same color causing the indexes of the color to change.
     * @return 
     */
    public BufferedImage createImage(){
        image = new BufferedImage(8, 8, BufferedImage.TYPE_BYTE_INDEXED, COL.getDefaultModel());
        
        for (int y = 0; y < 8; y++){
            for(int x = 0; x < 4; x++){
                int colorIndex1 = (data[x + 4 * y] >> 4) & 0x0F;
                int colorIndex2 = data[x + 4 * y] & 0x0F;
                
                image.setRGB(2 * x, y, COL.getDefaultRGBFromIndex(colorIndex1));//color1.getRGB());
                image.setRGB(2 * x + 1, y, COL.getDefaultRGBFromIndex(colorIndex2));//color2.getRGB());
            }
        }
        
        /*
        //byte[] a = ((DataBufferByte)image.getData().getDataBuffer()).getData();
        //for (int i = 0; i < 8; i++){
        //    for (int j = 0; j < 8; j++)
        //        System.out.print(" " + "0123456789abcdef".charAt(a[i * 8 + j]));
        //    System.out.println();
        //}*/
        
        //savedPal = pal;
        return image;
    }
    
    /**
     * Gets the stored character image created with createImage(). 
     * Palette is swapped with the correct 16-color ICM.
     * Solution source: stackoverflow.com/a/29578717
     * @param col
     * @param pal
     * @return 
     */
    public BufferedImage getImage(COL col, byte pal){
        if (image == null)// || savedPal != pal)
            createImage(); //should only create a new image the first time
        WritableRaster wr = image.getRaster();
        boolean iAPM = image.isAlphaPremultiplied();
        
        image = new BufferedImage(col.getICM16(pal), wr, iAPM, null);
        //savedPal = pal;
        return image;
    }
    
}
