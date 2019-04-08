/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petitcomputer;

import java.awt.Image;
import java.util.ArrayList;

/**
 *
 * @author minxr
 */
public class Sound implements ComponentPTC {
    ProcessII.Evaluator eval;
    
    public Sound(ProcessII.Evaluator ev){
        eval = ev;
    }
    
    public Image createImage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * PLays a sound effect with varying effects based off of the number number of args.
     * @param args 
     */
    public void beep(ArrayList<ArrayList> args){
        switch (args.size()){
            case 1:
                NumberPTC s = (NumberPTC) eval.eval(args.get(0));
                
                beep(s);
                break;
            case 2:
                s = (NumberPTC) eval.eval(args.get(0));
                NumberPTC p = (NumberPTC) eval.eval(args.get(1)); //separate args because command. Why did I use two...?
                
                beep(s, p);
                break;
            default:
                System.err.println(args.toString());
        }
    } 
    
    /**
     * Plays the sound with the given ID and no modification.
     * @param s 
     */
    public void beep(NumberPTC s){
        System.out.println("Plays sound: " + s.toString());
    }
    
    public void beep(NumberPTC s, NumberPTC p){
        System.out.println("Plays sound: " + s.toString() + " with pitch change " + p.toString());
    }
    
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> args) {
        Debug.print(Debug.ACT_FLAG, "SOUND command: " + command.toString() + "ARGS: " + args.toString());
        switch (command.toString().toLowerCase()){
            case "beep":
                beep(args);
                break;
            default:
                System.out.println("SOUND ERROR "  + command.toString());
        }
        
        return null;
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        throw new UnsupportedOperationException("SOUND FUNCTIONS not supported yet."); 
    }
}
