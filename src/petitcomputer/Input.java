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

/**
 * Class to store and retrieve input data.
 * @author minxr
 */
public class Input implements ComponentPTC{
    public static final int BACKSPACE = 15;
    private final Char[][] chars;
    
    volatile int buttons;
    volatile int keyboard;
    volatile boolean set, reset;
    
    public Input(){
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
    }
    
    public void setButton(int b){
        buttons = b;
        if ((!set && b != 0)||(!reset && b == 0)){ //if button pushed and waiting for button OR waiting for no button and button released.
            set = b != 0; //button has been pushed
            reset = b == 0; //button not pushed
        }
        //Debug.print(Debug.INPUT_FLAG, "setButton: " + b);
    }
    
    public void setKSC(int ksc){
        keyboard = ksc;
        if ((!set && ksc != 0)||(!reset && ksc == 0)){ //if button pushed and waiting for button OR waiting for no button and button released.
            set = ksc != 0; //button has been pushed
            reset = ksc == 0; //button not pushed
        }
        //Debug.print(Debug.INPUT_FLAG, "setButton: " + ksc);
    }
    
    public NumberPTC button(){
        NumberPTC b = new NumberPTC(buttons);
        
        Debug.print(Debug.INPUT_FLAG, "button: " + b.toString());
        return b;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        Debug.print(Debug.ACT_FLAG, "FUNC branch INPUT: " + function.toString() + "ARGS: " + args.toString());
        switch (function.toString().toLowerCase()){
            case "button":
                return button();
            case "btrig":
                return button();
            case "inkey$":
                return inkey();
            default:
                Debug.print(Debug.ACT_FLAG, "FUNC branch INPUT ERROR: " + function.toString());
                return null;
        }
    }
}

