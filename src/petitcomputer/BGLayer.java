package petitcomputer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * A single layer of background tiles.
 * @author minxr
 */
public class BGLayer {
    CharsetPTC chars;
    int offsetX, offsetY;
    final int size;
    short tiles[][];
    
    public BGLayer(CharsetPTC charset){
        this.chars = charset;
        size = 64;
        tiles = new short[size][size];
        
        offsetX = 0;
        offsetY = 0;
    }
    
    /*public BGLayer(CharsetPTC charset, int size){
        this.chars = charset;
        this.size = size;
        tiles = new short[size][size];
    }*/
    
    /**
     * Places a BG tile at the given location using PTC format numbers.
     * @param x
     * @param y
     * @param tile 
     */
    public void bgput(NumberPTC x, NumberPTC y, NumberPTC tile){
        bgput(x.getIntNumber(), y.getIntNumber(), tile.getIntNumber());
    }
    
    /**
     * Places a BG tile at the given location.
     * @param x
     * @param y
     * @param tile
     */
    public void bgput(int x, int y, int tile){
        tiles[x][y] = (short) tile;
    }
    
    /**
     * Fills in the range of tiles from (StartX, StartY) to (EndX, EndY) in a rectangle with (tile). All data provided is in PTC format.
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param tile 
     */
    public void bgfill(NumberPTC startX, NumberPTC startY, NumberPTC endX, NumberPTC endY, NumberPTC tile){
        bgfill(startX.getIntNumber(), startY.getIntNumber(), endX.getIntNumber(), endY.getIntNumber(), tile.getIntNumber());
    }
    
    /**
     * Fills in the range of tiles from (StartX, StartY) to (EndX, EndY) in a rectangle with (tile).
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param tile 
     */
    public void bgfill(int startX, int startY, int endX, int endY, int tile){
        for (int x = startX; x <= endX; x++)
            for (int y = startY; y <= endY; y++)
                bgput(x, y, tile);
    }
    
    /**
     * Clears the layer of tiles.
     */
    public void bgclr(){
        tiles = new short[size][size];
    }
    
    /**
     * Offsets the BG screen using PTC formatted numbers.
     * @param x
     * @param y 
     */
    public void bgofs(NumberPTC x, NumberPTC y){
        bgofs(x.getIntNumber(), y.getIntNumber());
    }
    
    /**
     * Offsets the BG screen.
     * @param x
     * @param y 
     */
    public void bgofs(int x, int y){
        offsetX = x;
        offsetY = y;
    }
    
    public VariablePTC bgchk(){
        return new NumberPTC(0);
    }
    
    public Image createImage(){
        BufferedImage image = new BufferedImage(256, 192, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                if (x * 8 - offsetX < 256 && x * 8 - offsetX >= 0 && y * 8 - offsetY < 192 && y * 8 - offsetY >= 0)
                g.drawImage(chars.getImage(tiles[x][y] & 0x03FF, (byte) ((tiles[x][y] & 0xC000)>>14)), 8 * x - offsetX, 8 * y - offsetY, null);
            }
        }
        
        return image;
    }
}
