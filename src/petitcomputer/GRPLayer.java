package petitcomputer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Class to store a single screen of GRP pages.
 * @author minxr
 */
public class GRPLayer {
    private int gcolor;
    private BufferedImage grp;
    private Graphics g;
    private COL colors;
    
    public GRPLayer(COL col){
        colors = col;
        gcolor = 0;
        grp = new BufferedImage(256, 192, BufferedImage.TYPE_INT_ARGB);
        g = grp.createGraphics();
    }
    
    public void gcolor(int c){
        gcolor = c;
    }
    
    public void gcls(){
        g.clearRect(0, 0, 256, 192);
    }
    
    public void gpset(int x, int y){
        grp.setRGB(x, y, colors.getColor(gcolor).getRGB());
    }
    
    public void gpset(int x, int y, int c){
        grp.setRGB(x, y, colors.getColor(c).getRGB());
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
