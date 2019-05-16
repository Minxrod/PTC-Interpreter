package petitcomputer;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PTC-style 256 color palette.
 * @author minxr
 */
public class COL {
    public static final int PALETTE_SIZE_SMALL = 16,
                            PALETTE_SIZE_LARGE = 256,
                            PALETTE_COUNT = 16;
    
    private static final IndexColorModel INDEX_TO_COLOR_ADAPTER = createDefaultModel();
    
    private IndexColorModel icm256;
    private IndexColorModel[] icm16;
    short[][] colors; //RGB555 format
    boolean cm; //color mode
    
    private static IndexColorModel createDefaultModel(){
        IndexColorModel icmdef;
        byte r[], g[], b[];
        r = new byte[PALETTE_SIZE_SMALL];
        g = new byte[PALETTE_SIZE_SMALL];
        b = new byte[PALETTE_SIZE_SMALL];
        for (int i = 0; i < PALETTE_SIZE_SMALL; i++){
            r[i] = (byte)(16 * i);
            g[i] = 0;
            b[i] = 0;
        }
        icmdef = new IndexColorModel(4, PALETTE_SIZE_SMALL, r, g, b);
        return icmdef;
    }
    
    public static IndexColorModel getDefaultModel(){
        return INDEX_TO_COLOR_ADAPTER;
    }
    
    public static int getDefaultRGBFromIndex(int index){
        return new Color(index * 16, 0, 0).getRGB();
    }
    
    /**
     * Creates a new color object. Data must be set using setData.
     * @param is256ColorMode 
     */
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
    
    /**
     * Sets the color data all at once.
     * Intended to be used when loading data from a file.
     * @param data 
     */
    public void setData(short[][] data){
       colors = data; 
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
     * Creates IndexColorModel(s) for drawing with BufferedImages. 
     * Each can be loaded individually.
     * The 16 element models are intended for the palette of things like sprite, BG tiles, etc.
     * The full 256 color model is intended for images like the full BG screen or graphics page.
     */
    public void createICM(){
        icm16 = new IndexColorModel[PALETTE_COUNT]; //array of palettes. Does not apply when using GRPS.
        byte[] r,g,b;
        byte[] u,v,w; //rgb for icm256
        
        u = new byte[PALETTE_SIZE_LARGE];
        v = new byte[PALETTE_SIZE_LARGE];
        w = new byte[PALETTE_SIZE_LARGE];
        for (int p = 0; p < PALETTE_COUNT; p++){
            r = new byte[PALETTE_SIZE_SMALL];
            g = new byte[PALETTE_SIZE_SMALL];
            b = new byte[PALETTE_SIZE_SMALL];
            for (int c = 0; c < PALETTE_SIZE_SMALL; c++){
                short col = colors[p][c];
                r[c] = (byte) getRedFromRGB555(col);
                g[c] = (byte) getGreenFromRGB555(col);
                b[c] = (byte) getBlueFromRGB555(col);
                u[c+PALETTE_SIZE_SMALL*p] = r[c];
                v[c+PALETTE_SIZE_SMALL*p] = g[c];
                w[c+PALETTE_SIZE_SMALL*p] = b[c];
            }
            //needs a separate icm for each palette
            icm16[p] = new IndexColorModel(4, PALETTE_SIZE_SMALL, r, g, b, 0);
        }
        icm256 = new IndexColorModel(8, PALETTE_SIZE_LARGE, u, v, w, 0);
        
        Debug.print(Debug.COLOR_FLAG, "256C" + icm256.toString() + " 16C:" + Arrays.toString(icm16));
        //bits, size, r[] g[] b[] a
    }
    
    public IndexColorModel getICM16(int palette){
        return icm16[palette];
    }
    
    public IndexColorModel getICM256(){
        return icm256;
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
