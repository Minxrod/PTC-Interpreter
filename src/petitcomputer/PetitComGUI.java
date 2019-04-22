/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petitcomputer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static java.awt.event.KeyEvent.*;
import java.io.File;

/**
 *
 * @author minxr
 */
public class PetitComGUI implements Runnable {
    public static final int WINDOW_WIDTH = 256;
    public static final int WINDOW_HEIGHT = 192;
    
    private final static Object[] BUTTON = new Object[]{
        KeyEvent.VK_W,  //up
        KeyEvent.VK_S,  //down
        KeyEvent.VK_A,  //left
        KeyEvent.VK_D,  //right
        
        KeyEvent.VK_RIGHT,  //a
        KeyEvent.VK_DOWN,   //b
        KeyEvent.VK_UP,     //x
        KeyEvent.VK_LEFT,   //y
        
        KeyEvent.VK_Q,      //l
        KeyEvent.VK_NUMPAD1,//r
        KeyEvent.VK_SPACE,  //start
        KeyEvent.VK_ESCAPE  //select
    };
    
    int button;
    int key;
    
    Thread process;
    Process processor;
    
    Timer t;
    JFrame frame;
    JPanel total;
    NPanel top;
    NPanel bot;
    
    boolean pleaseLoad = false;
    boolean pleaseRun = false;
    
    /**
     * Creates a new GUI window to display the results of a running PTC program.
     * The supplied file will be loaded and ran in a separate thread from the GUI.
     * The GUI thread itself just updates the GUI every frame,
     * at an intended 50FPS.
     * @param file - initial program file.
     */
    public PetitComGUI(File file){
        key = 0;
        button = 0;
        
        frame = new JFrame("PTC \"Emulator\"");
        top = new NPanel("upper");
        top.setBackground(Color.BLACK);
        
        bot = new NPanel("lower");
        bot.setBackground(Color.BLACK);
        
        total = new JPanel(new GridLayout(2,1));
        total.add(top);
        total.add(bot);
        total.addKeyListener(new Keyboard());
        total.setFocusable(true);
        //total.requestFocusInWindow();
        
        for (Object b : BUTTON){
            String press = b.toString() + " press";
            total.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke((int) b, 0, true), press);
            total.getActionMap().put(press, new KeyAction(b, true));
            
            String release = b.toString() + " release";
            total.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke((int) b, 0, false), release);
            total.getActionMap().put(release, new KeyAction(b, false));
        }
        
        total.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "ZOOM IN");
        total.getActionMap().put("ZOOM IN", new ZoomAction(2));
        
        total.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "ZOOM OUT");
        total.getActionMap().put("ZOOM OUT", new ZoomAction(1));
        
        frame.add(total);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setAlwaysOnTop(true); //useful for testing
        
        //upon creation of the GUI, ensure Process is ready to run in it's own thread, able to launch a program if necessary.
        processor = new Process(file);
        
        process = new Thread(processor);
    }
        
    /**
     * Starts Process of PetitComGUI.
     */
    public void start(){
        process.start();
    }
    
    public Process getProcess(){
        return processor;
    }

    /**
     * Called when this GUI's thread is started. Updates the screen and current inputs.
     */
    @Override
    public void run() {
        t = new Timer(20, new UpdateAction());
        t.setRepeats(true);
        t.setCoalesce(false);
        t.start();
    }

    private class ZoomAction extends AbstractAction {

        int zoomFactor;
        public ZoomAction(int zFactor) {
            zoomFactor = zFactor;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            top.setScale(zoomFactor);
            bot.setScale(zoomFactor);
            frame.pack();
        }
    }
    
    private class UpdateAction implements ActionListener {

        @Override
        public synchronized void actionPerformed(ActionEvent e) {
            frame.repaint();
            processor.setInput(button, key);
        }
    }
    
    private class KeyAction extends AbstractAction {
        
        private final Object keyst;
        private int buttonMask;
        private final boolean press;
        
        public KeyAction(Object keystr, boolean push){
            keyst = keystr;
            press = push;
            buttonMask = 0;
            for (int i = 0; i < BUTTON.length; i++)
                if (keyst.equals(BUTTON[i]))
                    buttonMask = 1 << i;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!press)
                button = button | buttonMask; 
            else
                button = button & ~buttonMask;
            
            Debug.print(Debug.GUI_FLAG, "Button: " + button + "key down: " + press);
            //processor.setInputs(button, key);
        }
    }
    
    /**
     * Keyboard class to handle key input for the PTC window. Indirectly stores to the system variable KEYBOARD and the function INKEY$()
     */
    private class Keyboard implements KeyListener {
        private final int[] keycode;
        
        public Keyboard(){
            keycode = new int[]{
                0,
                VK_ESCAPE, //1
                VK_1, //2
                VK_2,
                VK_3,
                VK_4,
                VK_5,
                VK_6,
                VK_7,
                VK_8,
                VK_9,
                VK_0, //11
                VK_MINUS,
                VK_PLUS,
                VK_EQUALS,
                VK_BACK_SPACE, // 15
                VK_DOLLAR, //16, begin 2nd row
                VK_QUOTEDBL, //not QUOTE
                VK_Q,
                VK_W,
                VK_E,
                VK_R,
                VK_T,
                VK_Y,
                VK_U,
                VK_I,
                VK_O,
                VK_P, //27
                VK_AT,
                VK_ASTERISK,
                VK_LEFT_PARENTHESIS,
                VK_RIGHT_PARENTHESIS,
                VK_TAB, //32, 3rd row
                VK_EXCLAMATION_MARK,
                VK_A,
                VK_S,
                VK_D,
                VK_F,
                VK_G,
                VK_H,
                VK_J,
                VK_K,
                VK_L, //42
                VK_SEMICOLON,
                VK_COLON,
                VK_LESS,
                VK_GREATER, //46, end row
                VK_SHIFT, //47, start 4th row
                VK_QUOTE, //48
                VK_Z,
                VK_X,
                VK_C,
                VK_V,
                VK_B,
                VK_N,
                VK_M, //55
                VK_COMMA,
                VK_PERIOD,
                VK_SLASH,
                VK_SHIFT + VK_5, //59, percent key. doesn't exist as KeyEvent.VK_PERCENT or any similar
                VK_ENTER,
                VK_CAPS_LOCK, //61, 5th and final row begins
                VK_F1,
                VK_F2,
                VK_F3,
                VK_SPACE, //65
                VK_INSERT,
                VK_DELETE,
                0, //68. Search key. As far as I know nobody uses this in running programs, so for now I'm not going to replace it.
            };
        }
        
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int kc = e.getKeyCode();
            for (int k = 0; k < keycode.length; k++)
                if (kc == keycode[k])
                    key = k;
            Debug.print(Debug.GUI_FLAG, "Key:" + key + " KC:" + kc);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            key = 0;
        }
        
    }
    
    /**
     * Panel class with paintComponent override. Allows for a custom image to be generated and drawn.
     */
    private class NPanel extends JPanel{
        String panelName;
        int scale;
        
        public NPanel(String newName) {
            panelName = newName;
            scale = 1;
        }
        
        public void setScale(int zFactor){
            scale = zFactor;
        }
        
        @Override
        public Dimension getPreferredSize(){
            return new Dimension(scale * WINDOW_WIDTH, scale * WINDOW_HEIGHT);
        }
        
        @Override
        public void paintComponent(Graphics g){
            //draw crap
            g.drawImage(processor.getImage(panelName), 0, 0, scale * WINDOW_WIDTH,scale * WINDOW_HEIGHT, null);
        }
    }
}