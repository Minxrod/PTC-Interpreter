package petitcomputer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

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
        grp = new BufferedImage(256, 192, BufferedImage.TYPE_BYTE_INDEXED, colors.getICM256());
        g = grp.createGraphics();
        
        g.setColor(colors.getColor(gcolor));
        g.fillRect(0, 0, 256, 192);
    }
    
    public void gcolor(int c){
        gcolor = c;
    }
    
    public void gcls(){
        g.setColor(colors.getColor(gcolor));
        g.fillRect(0, 0, 256, 192);
    }
    
    public void gcls(int c){
        g.setColor(colors.getColor(c));
        g.fillRect(0, 0, 256, 192);
    }
    
    public void gpset(int x, int y){
        g.setColor(colors.getColor(gcolor));
        g.drawLine(x, y, x, y);
    }
    
    public void gpset(int x, int y, int c){
        g.setColor(colors.getColor(c));
        g.drawLine(x, y, x, y);
    }
    
    public void gline(int x, int y, int x2, int y2){
        g.setColor(colors.getColor(gcolor));
        g.drawLine(x, y, x2, y2);
    }
    
    public void gline(int x, int y, int x2, int y2, int c){
        g.setColor(colors.getColor(c));
        g.drawLine(x, y, x2, y2);
    }
    
    public void gfill(int x, int y, int x2, int y2){
        g.setColor(colors.getColor(gcolor));
        g.fillRect(x, y, x2 - x, y2 - y);
    }
    
    public void gfill(int x, int y, int x2, int y2, int c){
        g.setColor(colors.getColor(c));
        g.fillRect(x, y, x2 - x, y2 - y);
    }
    
    public void gbox(int x, int y, int x2, int y2){
        g.setColor(colors.getColor(gcolor));
        g.drawRect(x, y, x2 - x, y2 - y);
    }
    
    public void gbox(int x, int y, int x2, int y2, int c){
        g.setColor(colors.getColor(c));
        g.drawRect(x, y, x2 - x, y2 - y);
    }
    
    public BufferedImage getImage(){
        return grp;
    }
}
