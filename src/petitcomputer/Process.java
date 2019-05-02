package petitcomputer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Class to handle interaction with the GUI.
 * Has methods to set input obtained from the GUI,
 * and get image output for the GUI. 
 * @author minxr
 */
public class Process implements Runnable {
    private final VirtualDevice device;
    
    /**
     * Creates a new process that will be run to start the PTC program.
     * @param file - program file to initialize a VirtualDevice with.
     */
    public Process(File file){
        device = new VirtualDevice(file);
    }
    
    @Override
    public void run(){
        device.execute();
    }
    
    public void setInput(int buttons, int keyboard){
        device.setInput(buttons, keyboard);
    }
    
    public void advFrame(){
        device.setSysVars();
    }
    
    public Image getImage(String screenName){
        if (device != null)
            return device.getImage(screenName.equals("upper"));
        else{
            BufferedImage i = new BufferedImage(256, 192, BufferedImage.TYPE_INT_ARGB);
            Graphics g = i.createGraphics(); 
            g.setColor(Color.BLACK);
            g.drawString("ERROR: VirtualDevice is null.", 20, 20);
            
            return i;
        }
    }
    
}