/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petitcomputer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * An individual sprite object and it's data.
 * Has data such as angle, scale, animation frame.
 * Use the PTC commands to modify the sprite.
 * @author minxr
 */
class Sprite {
    private int width, height;
    private final int chrSize;
    private int chr, pal;
    private int horiz, vert;
    private int priority;
    
    private int homeX, homeY;
    
    private double x, y;
    private double moveX, moveY; int moveTime;
    private int frame, animTime, animFrames, animMaxTime, animLoop;
    private double angle, angleStep; int angleTime;
    private double scale, scaleStep; int scaleTime;
    
    private int hitboxX, hitboxY, hitboxW, hitboxH;
    private int hitboxDX, hitboxDY; //displacement xy. I really don't know how this works.
    private boolean scaleAdjustment;
    private int hitboxMask;
    
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
        chr = c;
        pal = p;
        horiz = s;
        vert = v;
        width = w;
        height = h;
        priority = o;
        
        chrSize = w * h / 64;
        
        initSpriteData();
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
        chr = c;
        pal = p;
        horiz = h;
        vert = v;
        width = 16;
        height = 16;
        priority = o;
        
        chrSize = 4;
        
        initSpriteData();
    }
    
    /**
     * Initializes various items of sprite data shared between constructors.
     */
    private void initSpriteData(){
        homeX = 0;
        homeY = 0;
        
        scale = 100;
        scaleTime = 0;
        scaleStep = 0;
        
        angle = 0;
        angleTime = 0;
        angleStep = 0;
        
        frame = 0;
        animFrames = 1;
        animTime = 0;
        animMaxTime = 0;
        animLoop = 0;
        
        hitboxX = 0;
        hitboxY = 0;
        hitboxW = width;
        hitboxH = height;
        hitboxDX = 0; //really
        hitboxDY = 0; //what is this??
        hitboxMask = 0xFFFFF;
        scaleAdjustment = true; //I don't know what this defaults to.
        
        vars = new NumberPTC[8];
    }
    
    /**
     * Update sprite as if one frame had passed. Used for interpolation.
     */
    public boolean update(){
        boolean needsUpdate = false;
        
        if (moveTime > 0){
           x += moveX;
           y += moveY;
           moveTime--;
        }
        
        if (animFrames > 1){
            animTime--;
            if (animTime == 0){ //single frame end
                animTime = animMaxTime;
                
                needsUpdate = true;
                frame++;
                if (frame == animFrames){ //loop complete
                    animLoop--;
                    if (animLoop == 0){ //all loops complete
                        animFrames = 1; //no more animation; frame remains last frame
                    } else
                        frame = 0; //reset loop,
                }
            }
        }
        
        if (scaleTime > 0){
            scale += scaleStep;
            scaleTime--;
            needsUpdate = true;
        }
        
        if (angleTime > 0){
            angle += angleStep;
            angleTime--;
        }
        
        return needsUpdate;
    }
    
    public void spofs(double newX, double newY){
        x = newX;
        y = newY;
    }
    
    public void spofs(double newX, double newY, int time){
        moveX = (newX - x) / time;
        moveY = (newY - y) / time;
        moveTime = time;
    }
    
    public void spchr(int c){
        chr = c;
    }
    
    public void spscale(int sc){
        scale = sc;
    }
    
    public void spscale(int sc, int time){
        scaleStep = (sc - scale) / time;
        scaleTime = time;
    }
    
    public void spangle(double ang){
        angle = ang;
    }
    
    public void spangle(double ang, int time){
        angleStep = (ang - angle) / time;
        angleTime = time;
    }
    
    public void spanim(int frames, int time, int loop){
        animFrames = frames;
        frame = 0;
        animMaxTime = time;
        animTime = animMaxTime;
        animLoop = loop;
    }
    
    public Image createImage(CharsetPTC charset){
        if (scale == 0)
            return null;
        double scalePercent = scale / 100;
        image = new BufferedImage((int)(width * scalePercent), (int) (height * scalePercent), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics(); 
        for (int chrY = 0; chrY < height/8; chrY++)
            for (int chrX = 0; chrX < width/8; chrX++)
                g.drawImage(charset.getImage(
                        4 * chr + chrX + width / 8 * chrY + frame * chrSize, (byte)pal //all 1st argument: character code
                ).getScaledInstance((int) (8 * scalePercent), (int) (8 * scalePercent), Image.SCALE_FAST),
                        (int) (scalePercent * 8 * chrX), (int) (scalePercent * 8 * chrY), null);
        
        return image;
    }
    
    public void draw(Graphics2D g){
        if (image != null){
            int drawX = (int) (x - homeX); //upper left corner of sprite
            int drawY = (int) (y - homeY); //note: homex, homey should be x and y
            
            //used as source: https://gamedev.stackexchange.com/questions/62196/rotate-image-around-a-specified-origin
            AffineTransform backup = g.getTransform();
            AffineTransform rotate = new AffineTransform();
            rotate.rotate(Math.toRadians(angle), x, y);
            
            g.setTransform(rotate);
            g.clipRect(drawX, drawY, 128, 128);
            g.drawImage(image, drawX, drawY, null);
            g.setTransform(backup);
            g.setClip(null);
        }
    }
}
