/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petitcomputer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import static petitcomputer.CharacterPTC.Char;
import static petitcomputer.CharacterPTC.Char.*;
import petitcomputer.VirtualDevice.Evaluator;

/**
 * Class to store and retrieve input data.
 * @author minxr
 */
public class Input implements ComponentPTC{
    /**
     * Number of hardware buttons emulated.
     */
    private static final int BUTTON_COUNT = 12; 
    
    /**
     * Keyboard backspace key.
     */
    public static final int BACKSPACE = 15;
    private final Char[][] chars;
    
    int oldButtons;
    volatile int buttons;
    
    volatile int keyboard;
    volatile boolean set, reset;
    
    ButtonInfo buttonInfo[];
    
    Evaluator eval;
    
    public Input(Evaluator ev){
        eval = ev;

        buttons = 0;
        keyboard = 0;
        
        chars = new Char[6][];
        //This is the default keyboard's character set.
        chars[0] = new Char[]{
            NULL, NULL, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8, NUM_9, NUM_0,
            DASH, PLUS, EQUALS, NULL, //15
            
            DOLLAR, QUOTE, Q, W, E, R, T, Y, U, I, O, P, AT, ASTERISK, OPEN_PAREN, CLOSE_PAREN, //31
            
            TAB, EXCLAMATION, A, S, D, F, G, H, J, K, L, SEMICOLON, COLON, LESS_THAN, GREATER_THAN, //46
            
            NULL, APOSTROPHE, Z, X, C, V, B, N, M, COMMA, PERIOD, SLASH, PERCENT, LINEBREAK, //60
            
            NULL, NULL, NULL, NULL, SPACE, NULL, NULL, STAR
        };
        Debug.print(Debug.INPUT_FLAG, "KEYS:" + Arrays.deepToString(chars));
        //Debug.print(Debug.INPUT_FLAG, "KEYCHARS:" + chars[0].getStringPTC().toString());
        
        //Initialize all buttons and their information
        buttonInfo = new ButtonInfo[BUTTON_COUNT];
        for (int i = 0; i < BUTTON_COUNT; i++){
            buttonInfo[i] = new ButtonInfo(1 << i);
        }
    }
    
    public void setButton(int b){
        oldButtons = buttons;
        buttons = b;
        //Debug.print(Debug.INPUT_FLAG, "setButton: " + b);
        for (ButtonInfo button : buttonInfo)
            if ((button.mask & buttons) != 0) //if button is pressed
                button.timeHeld++;
            else //button is released
                button.timeHeld = 0;
    }
    
    public void setKSC(int ksc){
        keyboard = ksc;
        if ((!set && ksc != 0)||(!reset && ksc == 0)){ //if button pushed and waiting for button OR waiting for no button and button released.
            set = ksc != 0; //button has been pushed
            reset = ksc == 0; //button not pushed
        }
        //Debug.print(Debug.INPUT_FLAG, "setButton: " + ksc);
    }
    
    /**
     * Sets the button repeat values.
     * @param id
     * @param start
     * @param repeat 
     */
    public void brepeat(int id, int start, int repeat){
        buttonInfo[id].repeatStart = start;
        buttonInfo[id].repeatInterval = repeat;
    }
    
    /**
     * Returns the value returned by the function BUTTON().
     * @return button
     */
    public NumberPTC button(){
        NumberPTC b = new NumberPTC(buttons);
        
        Debug.print(Debug.INPUT_FLAG, "button: " + b.toString());
        return b;
    }
    
    public NumberPTC button(int i){
        switch (i){
            case 0:
                return button();
            case 1:
                return btrig();
            case 2:
                
            case 3:
            default:
                return btrig();
        }
    }
    
    /**
     * BTRIG() as a function.
     * @return 
     */
    public NumberPTC btrig(){
        //returns button value if button time is on line with repeat or initial press.
        //button() value: buttons
        //btrig() button temp value: button
        int button = buttons;
        for (ButtonInfo b : buttonInfo){
            //check if button is active
            if (b.timeHeld > 0){ //button is pressed
                //BTRIG will return the button if...
                //timeHeld = 1
                //timeHeld = repeatStart
                //timeHeld = repeatStart + k * (repeatInterval + 1)
                
                if (b.timeHeld == 1)
                    continue;//instant pressed
                if (b.timeHeld == b.repeatStart + 1)
                    continue;
                if (b.timeHeld > b.repeatStart)
                    if ((b.timeHeld - b.repeatStart) % (b.repeatInterval + 1) == 0)
                        continue;
                
                button &= ~b.mask;
                
            }
        }
        Debug.print(Debug.INPUT_FLAG, "btrig: " + button);
        return new NumberPTC(button);
    }
    
    public int getButtons(){
        Debug.print(Debug.INPUT_FLAG, "Buttons: " + buttons);
        
        return buttons;
    }
    
    public StringPTC inkey(){
        StringPTC inkey = chars[0][keyboard].getStringPTC();
        
        Debug.print(Debug.INPUT_FLAG, "inkey: " + inkey.toString());
        return inkey;
    }
    
    /**
     * Returns the value of KEYBOARD as a Java integer.
     * @return 
     */
    public int keyboard(){
        Debug.print(Debug.INPUT_FLAG, "keyboard: " + keyboard);
        
        return keyboard;
    }
    
    public void waitForInput(){
        set = false;
        Debug.print(Debug.INPUT_FLAG, "Wait for INPUT");
        while (!set)
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Input.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void waitForReset(){
        reset = false;
        Debug.print(Debug.INPUT_FLAG, "Wait for RESET");
        while (!reset)
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Input.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void setSystemVariables(VariablesII vars){
        vars.setVariable(new StringPTC("KEYBOARD"), new NumberPTC(keyboard));
    }

    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> args) {
        Debug.print(Debug.ACT_FLAG, "ACT branch INPUT: " + command + "ARGS: " + args.toString());
        switch (command.toString().toLowerCase()){
            case "brepeat":
                NumberPTC buttonID = (NumberPTC) eval.eval(args.get(0));
                NumberPTC start = (NumberPTC) eval.eval(args.get(1));
                NumberPTC repeat = (NumberPTC) eval.eval(args.get(2));
                
                brepeat(buttonID.getIntNumber(), start.getIntNumber(), repeat.getIntNumber());
                break;
            default:
                Debug.print(Debug.ACT_FLAG, "ACT branch ERROR " + command);
        }
        return null;
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        Debug.print(Debug.ACT_FLAG, "FUNC branch INPUT: " + function.toString() + "ARGS: " + args.toString());
        switch (function.toString().toLowerCase()){
            case "button":
                NumberPTC mode = (NumberPTC) args.get(0);
                
                return button(mode.getIntNumber());
            case "btrig":
                return btrig();
            case "inkey$":
                return inkey();
            default:
                Debug.print(Debug.ACT_FLAG, "FUNC branch INPUT ERROR: " + function.toString());
                return null;
        }
    }
    
    /**
     * Class intended to be used like a struct to hold several arrays of data.
     */
    private class ButtonInfo {
        
        /**
         * Creates a ButtonInfo with the given button mask.
         * @param m button mask
         */
        public ButtonInfo(int m){
            mask = m;
        }
        
        /**
         * How long the button has been pressed
         */
        public int timeHeld; 
        /**
         * How long to wait before repeating inputs
         */
        public int repeatStart;
        /**
         * How long to wait before repeating after the first repeat
         */
        public int repeatInterval;
        /**
         * the button bitmask. Should only set set once
         */
        public final int mask;
    }
}

