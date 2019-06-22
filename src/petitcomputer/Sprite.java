/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petitcomputer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * An individual sprite object and it's data.
 * Has data such as angle, scale, animation frame.
 * Use the PTC commands to modify the sprite.
 * @author minxr
 */
class Sprite {
    int x, y;
    int width, height;
    int chr, pal;
    int horiz, vert;
    int priority;
    int homeX, homeY;
    NumberPTC[] vars;
    BufferedImage image;
    
    /**
     * Creates a sprite object from the given parameters, including the size..
     * @param c - character code
     * @param p - palette number
     * @param s - horizontal flip
     * @param v - vertical flip
     * @param o - priority order (0-3, 0=highest priority)
     * @param w - width (8, 16, 32, 64)
     * @param h - height (8,, 16, 32, 64)
     */
    public Sprite(int c, int p, int s, int v, int o, int w, int h){
        x = 0;
        y = 0;
        chr = c;
        pal = p;
        horiz = s;
        vert = v;
        width = w;
        height = h;
        priority = o;
        homeX = 0;
        homeY = 0;
    }
    
    /**
     * Creates a sprite object from the given parameters.
     * Width and height are set to 16.
     * @param c - character code
     * @param p - palette number
     * @param s - horizontal flip
     * @param v - vertical flip
     * @param o - priority order (0-3, 0=highest priority)
     */
    public Sprite(int c, int p, int h, int v, int o){
        x = 0;
        y = 0;
        chr = c;
        pal = p;
        horiz = h;
        vert = v;
        width = 16;
        height = 16;
        priority = o;
        homeX = 0;
        homeY = 0;
    }
    
    public void spofs(int newX, int newY){
        x = newX;
        y = newY;
    }
    
    public void spchr(int c){
        chr = c;
    }
    
    public Image createImage(CharsetPTC charset){
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        for (int chrY = 0; chrY < height/8; chrY++)
            for (int chrX = 0; chrX < width/8; chrX++)
                g.drawImage(charset.getImage(4 * chr + chrX + width / 8 * chrY, (byte)pal), 8 * chrX, 8 * chrY, null);
        
        return image;
    }
    
    public void draw(Graphics g){
        if (image != null)
            g.drawImage(image, x - homeX, y - homeY, null);
    }
}
