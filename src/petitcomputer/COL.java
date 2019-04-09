package petitcomputer;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PTC-style 256 color palette.
 * @author minxr
 */
public class COL {
    IndexColorModel[] icm; //for java Swing
    short[][] colors; //RGB555 format (?)
    boolean cm;
    
    public COL(boolean is256ColorMode){
        colors = new short[16][16];
        cm = is256ColorMode;
    }
    
    public short[] getPalette(int index){
        return colors[index];
    }
    
    public short getColor(int palette, int index){
        return colors[palette][index];
    }
    
    public Color getColor(int index){
        Color c;
        short s = getColor(index / 16, index % 16);
        int r, g, b;
        
        r = getRedFromRGB555(s);
        g = getGreenFromRGB555(s);
        b = getBlueFromRGB555(s);
        
        if (index % 16 == 0 && !cm)
            c = new Color(r, g, b, 0);
        else 
            c = new Color(r, g, b);
        
        return c;
    }
    
    public void setDefault(String filename){
        File defaultColorFile = new File(filename);
        FileInputStream in = null;
        try {
            in = new FileInputStream(defaultColorFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BGF.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!defaultColorFile.exists())
            System.err.println("Oh fricc you messed up...");
        
        byte[] header = new byte[48];
        //for (int i = 0; i < 48; i++){
        try {
            //System.out.println(i);
            in.read(header);
        } catch (IOException ex) {
            Logger.getLogger(BGF.class.getName()).log(Level.SEVERE, null, ex);
        }
        //}
        //System.out.println(Arrays.toString(header));
        
        //byte[] tempBytes;
        int tempCol;
        for (int i = 0; i < 256; i++){
            try {
                //tempBytes = new byte[2];
                tempCol = (short) ((in.read() << 8) | in.read());
                colors[i / 16][i % 16] = convertFromPTCFormat((short) tempCol);
            } catch (IOException ex) {
                Logger.getLogger(COL.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //System.out.println(Arrays.deepToString(colors));
        
    }
    
    /**
     * Creates an IndexColorModel[] for BufferedImages. Each can be loaded individually.
     */
    public void createICM(){
        icm = new IndexColorModel[16]; //array of palettes. Does not apply when using GRPS.
        byte[] r,g,b;

        for (int p = 0; p < 16; p++){
            r = new byte[16];
            g = new byte[16];
            b = new byte[16];
            for (int c = 0; c < 16; c++){
                short col = colors[c / 16][c % 16];
                r[c] = (byte) getRedFromRGB555(col);
                g[c] = (byte) getGreenFromRGB555(col);
                b[c] = (byte) getBlueFromRGB555(col);
            }
            icm[p] = new IndexColorModel(4, 16, r, g, b, 0);
        }
        
        //bits, size, r[] g[] b[] a[]
    }
    
    public IndexColorModel getICM(int palette){
        return icm[palette];
    }
    /**
     * Converts the native PTC format to this emulated form of the color.
     * @param col
     * @return 
     */
    static public short convertFromPTCFormat(short col){
        short red = (short) ((col & 0x1F00) >>> 8);
        short green = (short) (((col & 0xE000) >>> 13) | ((col & 0x0003) << 3)); 
        short blue = (short)((col & 0x007C) >>> 2);
        //System.out.print("(" + red + "," + green + "," + blue + ")");

        short newCol;
        
        newCol = (short) ((red << 10) + (green << 5) + blue); //?RRRRRGGGGGBBBBB
        return newCol; //not done;
    }
    
    static public int getRedFromRGB555(short color){
        return (color & 0x7C00) >> 7;
    }
    
    static public int getGreenFromRGB555(short color){
        return (color & 0x03E0) >> 2;
    }
        
    static public int getBlueFromRGB555(short color){
        return (color & 0x001F) << 3;
    }
}