package petitcomputer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Class to store a single screen of GRP pages.
 * @author minxr
 */
public class GRPLayer {
    private int gcolor;
    private final BufferedImage grp;
    private final Graphics g;
    private final COL colors;
    
    public GRPLayer(COL col){
        colors = col;
        gcolor = 0;
        grp = new BufferedImage(256, 192, BufferedImage.TYPE_BYTE_INDEXED, COL.getDefaultModel256());
        g = grp.createGraphics();
        
        g.setColor(COL.getDefaultColorFromIndex(gcolor));
        g.fillRect(0, 0, 256, 192);
    }
    
    public void gcolor(int c){
        gcolor = c;
    }
    
    public void gcls(){
        g.setColor(COL.getDefaultColorFromIndex(gcolor));
        g.fillRect(0, 0, 256, 192);
    }
    
    public void gcls(int c){
        g.setColor(COL.getDefaultColorFromIndex(c));
        g.fillRect(0, 0, 256, 192);
    }
    
    public void gpset(int x, int y){
        g.setColor(COL.getDefaultColorFromIndex(gcolor));
        g.drawLine(x, y, x, y);
    }
    
    public void gpset(int x, int y, int c){
        g.setColor(COL.getDefaultColorFromIndex(c));
        g.drawLine(x, y, x, y);
    }
    
    public void gline(int x, int y, int x2, int y2){
        g.setColor(COL.getDefaultColorFromIndex(gcolor));
        g.drawLine(x, y, x2, y2);
    }
    
    public void gline(int x, int y, int x2, int y2, int c){
        g.setColor(COL.getDefaultColorFromIndex(c));
        g.drawLine(x, y, x2, y2);
    }
    
    public void gfill(int x, int y, int x2, int y2){
        g.setColor(COL.getDefaultColorFromIndex(gcolor));
        g.fillRect(x, y, x2 - x, y2 - y);
    }
    
    public void gfill(int x, int y, int x2, int y2, int c){
        g.setColor(COL.getDefaultColorFromIndex(c));
        g.fillRect(x, y, x2 - x, y2 - y);
    }
    
    public void gbox(int x, int y, int x2, int y2){
        g.setColor(COL.getDefaultColorFromIndex(gcolor));
        g.drawRect(x, y, x2 - x, y2 - y);
    }
    
    public void gbox(int x, int y, int x2, int y2, int c){
        g.setColor(COL.getDefaultColorFromIndex(c));
        g.drawRect(x, y, x2 - x, y2 - y);
    }
    
    public void gcircle(int x, int y, int r){
        g.setColor(COL.getDefaultColorFromIndex(gcolor));
        g.drawOval(x-r, y-r, 2*r, 2*r);
    }
    
    public void gcircle(int x, int y, int r, int c){
        g.setColor(COL.getDefaultColorFromIndex(c));
        g.drawOval(x-r, y-r, 2*r, 2*r);
    }

    public VariablePTC gspoit(int x, int y){
        return new NumberPTC(new Color(grp.getRGB(x, y)).getRed());
    }
    
    public BufferedImage getImage(){
        WritableRaster wr = grp.getRaster();
        boolean iAPM = grp.isAlphaPremultiplied();
        
        BufferedImage bi = new BufferedImage(colors.getICM256(), wr, iAPM, null);
        
        return bi;
    }
}
