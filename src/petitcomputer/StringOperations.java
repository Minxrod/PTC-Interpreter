/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petitcomputer;

import java.util.ArrayList;
import petitcomputer.CharacterPTC.Char;
import petitcomputer.VirtualDevice.Evaluator;

/**
 * The MathPTC equivalent for strings.
 * @author minxr
 */
public class StringOperations {
    public static Evaluator eval;
    public static VariablesII vars;
    
    public static void setEval(Evaluator e){
        eval = e;
    }
    public static void setVars(VariablesII v){
        vars = v;
    }
    
    public static void act(StringPTC command, ArrayList<ArrayList> args){
        Debug.print(Debug.ACT_FLAG, "COMMAND branch STRING_OPS " + command + "ARGS: " + args);
        switch (command.toString().toLowerCase()){
            case "dtread":
                StringPTC date = (StringPTC) eval.eval(args.get(0));
                ArrayList var1 = args.get(1);
                ArrayList var2 = args.get(2);
                ArrayList var3 = args.get(3);
                
                dtread(date, var1, var2, var3);
        }
    }
    
    public static VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        Debug.print(Debug.ACT_FLAG, "FUNCTION branch STRING_OPS " + function + " ARGS: " + args);
        StringPTC string;
        switch (function.toString().toLowerCase()){
            case "str$":
                NumberPTC n = (NumberPTC) args.get(0);
                
                return n.toStringPTC();
            case "left$":
                string = (StringPTC) args.get(0);
                NumberPTC fromLeft = (NumberPTC) args.get(1);
                
                return left$(string, fromLeft.getIntNumber());
            case "right$":
                string = (StringPTC) args.get(0);
                NumberPTC fromRight = (NumberPTC) args.get(1);
                
                return right$(string, fromRight.getIntNumber());
            case "mid$":
                string = (StringPTC) args.get(0);
                NumberPTC start = (NumberPTC) args.get(1);
                NumberPTC length = (NumberPTC) args.get(2);
                
                return mid$(string, start.getIntNumber(), length.getIntNumber());
            case "chr$":
                NumberPTC code = (NumberPTC) args.get(0);
                
                return chr$(code);
            case "asc":
                string = (StringPTC) args.get(0);
                
                return asc(string);
            case "val":
                string = (StringPTC) args.get(0);
                
                return string.getNumberFromString();
            case "len":
                string = (StringPTC) args.get(0);
                
                return new NumberPTC(string.getLength());
            default:
                return null;
        }
    }
    
    /**
     * Extracts the given number of characters from the left of the given string.
     * @param string
     * @param length
     * @return 
     */
    public static StringPTC left$(StringPTC string, int length){
        return string.getSubstring(0, length);
    }
    
    public static StringPTC right$(StringPTC string, int length){
        return string.getSubstring(string.getLength() - length, length);
    }
    
    /**
     * Redundant method to get a substring of a string.
     * @param string
     * @param begin
     * @param length
     * @return 
     */
    public static StringPTC mid$(StringPTC string, int begin, int length){
        return string.getSubstring(begin, length);
    }
    
    /**
     * Converts a NumberPTC to a StringPTC. Really just a wrapper for NumberPTC.toStringPTC()
     * @param n
     * @return 
     */
    public static StringPTC str$(NumberPTC n){
        return n.toStringPTC();
    }
    
    /**
     * Converts StringPTC to a NumberPTC. Also just a wrapper for StringPTC.getNumberFromString().
     * @param s
     * @return 
     */
    public static NumberPTC val(StringPTC s){
        return s.getNumberFromString();
    }
    
    /**
     * Returns the character from the given number.
     * @param n
     * @return 
     */
    public static StringPTC chr$(NumberPTC n){
        return Char.values()[n.getIntNumber()].getStringPTC();
    }
    
    /**
     * Returns the numeric index of a character.
     * @param s
     * @return 
     */
    public static NumberPTC asc(StringPTC s){
        return new NumberPTC(s.getCharacter(0));
    }

    /**
     * Reads a date's components into multiple variables.
     * @param date
     * @param var1
     * @param var2
     * @param var3 
     */
    public static void dtread(StringPTC date, ArrayList var1, ArrayList var2, ArrayList var3) {
        String c = date.toString();
        
        int y = Integer.parseInt(c.substring(0, 4));
        int m = Integer.parseInt(c.substring(4, 6));
        int d = Integer.parseInt(c.substring(6, 8));
        
        vars.setVariable(var1, new NumberPTC(y));
        vars.setVariable(var2, new NumberPTC(m));
        vars.setVariable(var3, new NumberPTC(d));
    }
}
