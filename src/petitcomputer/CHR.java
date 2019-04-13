package petitcomputer;

import java.awt.Color;
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
        //image = createImage(col, 0);
    }
    
    public byte[] getData(){
        return data;
    }
    
    /**
     * Generates an image from the given COL object and palette number.
     * @param col
     * @param pal
     * @return 
     */
    public BufferedImage createImage(COL col, byte pal){
        image = new BufferedImage(8, 8, BufferedImage.TYPE_BYTE_INDEXED, col.getICM16(pal));
        
        for (int y = 0; y < 8; y++){
            for(int x = 0; x < 4; x++){
                int colorIndex1 = (data[x + 4 * y] & 0xF0) >>> 4;
                int colorIndex2 = data[x + 4 * y] & 0x0F;
                
                Color color1 = col.getColor(colorIndex1 + 16 * pal);
                Color color2 = col.getColor(colorIndex2 + 16 * pal);
                
                image.setRGB(2 * x, y, color1.getRGB());
                image.setRGB(2 * x + 1, y, color2.getRGB());
            }
        }
        
        savedPal = pal;
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
            createImage(col, (byte) pal); //should only create a new image the first time
        WritableRaster wr = image.getRaster();
        boolean iAPM = image.isAlphaPremultiplied();
        
        return new BufferedImage(col.getICM16(pal), wr, iAPM, null);
    }
    
}
