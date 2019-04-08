package petitcomputer;

import java.awt.Color;
import java.awt.image.BufferedImage;

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
        image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);//BufferedImage.TYPE_BYTE_INDEXED, col.getICM(pal));
        
        for (int y = 0; y < 8; y++){
            for(int x = 0; x < 4; x++){
                int colorIndex1 = (data[x + 4 * y] & 0xF0) >>> 4;
                int colorIndex2 = data[x + 4 * y] & 0x0F;
                
                short color555_1 = col.getColor(pal, colorIndex1);
                short color555_2 = col.getColor(pal, colorIndex2);
                int trans1, trans2; //transparency for index == 0
                if (colorIndex1 == 0)
                    trans1 = 0; //if index is 0, transparent,
                else
                    trans1 = 255; //if index is 1-15, opague;
                
                if (colorIndex2 == 0)
                    trans2 = 0;
                else
                    trans2 = 255;
                
                
                //System.out.println(col.getColor(c1, 15));
                //pal.getColorData(c1));
                //System.out.println("(" + color555_1 + "," + color555_2 + ")");
                Color color1 = new Color(COL.getRedFromRGB555(color555_1), COL.getGreenFromRGB555(color555_1), COL.getBlueFromRGB555(color555_1), trans1);
                Color color2 = new Color(COL.getRedFromRGB555(color555_2), COL.getGreenFromRGB555(color555_2), COL.getBlueFromRGB555(color555_2), trans2);
                
                image.setRGB(2 * x, y, color1.getRGB());
                image.setRGB(2 * x + 1, y, color2.getRGB());
            }
        }
        
        savedPal = pal;
        return image;
    }
    
    /**
     * Gets the stored character image created with createImage(); if the image is the wrong palette, returns null and relies on calling class to call createImage() with new palette.
     * @param pal
     * @return 
     */
    public BufferedImage getImage(int pal){
        if (pal == savedPal)
            return image;
        return null;
    }
    
}
