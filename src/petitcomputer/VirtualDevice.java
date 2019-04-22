package petitcomputer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Contains the various ComponentPTC objects used in program execution. Can be given commands to pass along to the correct object for handling.
 * @author minxr
 */
public class VirtualDevice implements ComponentPTC{
    private final int 
        GROUP_PROCESS = 0,
        GROUP_CONSOLE = 1,
        GROUP_BACKGROUND = 2,
        GROUP_PANEL = 3,
        GROUP_SPRITE = 4,
        GROUP_SOUND = 5,
        GROUP_GRAPHICS = 6,
        GROUP_RESOURCE = 7,
        GROUP_FILE = 8,
        GROUP_LOCAL = 9,
        GROUP_DATA = 10,
        GROUP_MATH = 11,
        GROUP_INPUT = 12,
        GROUP_VARIABLE = 13,
        GROUP_STRING = 14,
        GROUP_CODE = 15,
        GROUP_UNDEFINED = 99;
    
    //necessary info-storage classes
    Program program;
    VariablesII vars;
    Evaluator eval;
    
    //components and their resources
    Resources r;
    
    //things that require program-initialized data
    Data data;
    
    //self-contained resources
    Files files;
    Input input;
    Console console;
    Background bg;
    Panel panel;
    Sound sound;
    Graphic graphics;
    
    /**
     * Constructs a new VirtualDevice with all necessary components for a program run.
     * Accepts a file to load an initial program from.
     * @param file - program file to load
     */
    public VirtualDevice(File file){
        //program time
        files = new Files();
        program = new Program(files.initProgram(file));
        
        //resources used by all componenets
        eval = new Evaluator();
        r = new Resources();
        vars = new VariablesII(eval);
        
        //create and initialize many components
        data = new Data(vars, eval);
        
        input = new Input();
        
        console = new Console(r.getBGFU(), r.getCOL0(), eval);
        console.setVariables(vars);
        console.setIn(input);
        console.init();
        
        panel = new Panel(r.getBGFL(), r.getCOL0(), r.getBGD(), eval);
        
        bg = new Background(r.getBGUU(), r.getCOL0(), eval);
        
        graphics = new Graphic(r.getCOL2(), eval);
        
        sound = new Sound(eval);
    }
    
    public void execute(){
        program.setDevice(this);
        
        program.execute();
    }
    
    public void setInput(int buttons, int keyboard){
        input.setButton(buttons);
        input.setKSC(keyboard);
    }
    /**
     * Gets the image of the program at the current frame.
     * Depending on the boolean passed, will give the upper or lower screen
     * @param upper - upper screen if true, lower screen if false
     * @return 
     */
    public Image getImage(boolean upper){
        if (upper)
            return getTopImage();
        else
            return getBottomImage();
    }
    private Image getTopImage(){
        BufferedImage image = new BufferedImage(256, 192, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        
        //actual draw order will be based on draw priority, but this is unfinished for now.
        g.drawImage(graphics.createImage(0), 0, 0, null);
        g.drawImage(bg.createImage(0), 0, 0, null);
        g.drawImage(console.createImage(), 0, 0, null);
        
        return image;
    }
    private Image getBottomImage(){
        BufferedImage image = new BufferedImage(256, 192, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        
        //panel!
        g.drawImage(panel.createImage(), 0, 0, null);
        
        return image;
    }
    
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> args) {
        switch (getFunctionType(command.toString().toLowerCase())){
            case GROUP_CONSOLE:
                console.act(command, args);
                break;
            case GROUP_CODE:
                program.act(command, args);
                break;
            case GROUP_BACKGROUND:
                bg.act(command, args);
                break;
            case GROUP_PANEL:
                panel.act(command, args);
                break;
            case GROUP_SOUND:
                sound.act(command, args);
                break;
            case GROUP_DATA:
                data.act(command, args);
                break;
            case GROUP_INPUT:
                input.act(command, args);
                break;
            case GROUP_VARIABLE:
                vars.act(command, args);
                break;
            case GROUP_GRAPHICS:
                graphics.act(command, args);
        }
        
        return null;
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        switch (getFunctionType(function.toString())){
            case GROUP_CONSOLE:
                return console.func(function, args);
            case GROUP_PROCESS:
                //act(command, args);
                break;
            case GROUP_BACKGROUND:
                return bg.func(function, args);
            case GROUP_PANEL:
                return panel.func(function, args);
            case GROUP_SOUND:
                return sound.func(function, args);
            case GROUP_DATA:
                return data.func(function, args);
            case GROUP_MATH:
                return MathPTC.func(function, args);
            case GROUP_INPUT:
                return input.func(function, args);
            case GROUP_STRING:
                return StringOperations.func(function, args);
            default:
                return new NumberPTC(1336.5);
        }
        
        return null;
    }
    
    public int getFunctionType(String function){
        int group;
        
        switch (function.toLowerCase()){
            case "print":
            case "locate":
            case "cls":
            case "color":
            case "input":
            case "chkchr$":
                group = GROUP_CONSOLE;
                break;
            case "visible":
                group = GROUP_PROCESS;
                break;
            case "if":
            case "then":
            case "else":
            case "goto":
            case "wait":
            case "end":
            case "on":
            case "for":
            case "next":
                group = GROUP_CODE;
                break;
            case "bgput":
            case "bgfill":
            case "bgclr":
            case "bgofs":
                group = GROUP_BACKGROUND;
                break;
            case "pnlstr":
            case "pnltype":
                group = GROUP_PANEL;
                break;
            case "beep":
                group = GROUP_SOUND;
                break;
            case "abs"://
            case "atan"://
            case "cos"://
            case "deg"://
            case "exp":
            case "floor"://
            case "log"://
            case "pi"://
            case "pow":
            case "rad"://
            case "rnd"://
            case "sgn"://
            case "sin"://
            case "sqr":
            case "tan"://
                group = GROUP_MATH;
                break;
            case "button":
            case "brepeat":
            case "btrig":
            case "inkey$":
                group = GROUP_INPUT;
                break;
            case "data":
            case "read":
            case "restore":
                group = GROUP_DATA;
                break;
            case "dim":
                group = GROUP_VARIABLE;
                break;
            case "left$":
            case "right$":
            case "mid$":
            case "subst$":
            case "str$":
            case "val":
            case "hex$":
            case "chr$":
            case "asc":
                group = GROUP_STRING;
                break;
            case "gcls":
            case "gcolor":
            case "gpage":
            case "gpset":
            case "gline":
            case "gfill":
            case "gbox":
            case "gspoit":
                group = GROUP_GRAPHICS;
                break;
            default:
                group = GROUP_UNDEFINED;
                break;
        }
        return group;
    }
    
    public VariablesII getVars(){
        return vars;
    }
    public Evaluator getEval(){
        return eval;
    }
    
    /**
     * Evaluates a string expression and converts it to the correct format: number, or string. Requires device to call functions.
     * @author minxr
     */    
    public class Evaluator {
        public VariablePTC eval(ArrayList items){
            return evaluate(items).get(0);
        }

        public ArrayList<VariablePTC> evaluate(ArrayList items){
            ArrayList<VariablePTC> toEvaluate = (ArrayList<VariablePTC>) items.clone();
            toEvaluate = toLiterals(toEvaluate);

            Debug.print(Debug.EVALUATOR_FLAG, "evaluate init:" + toEvaluate.toString());
            toEvaluate = evaluateParens(toEvaluate);
            Debug.print(Debug.EVALUATOR_FLAG, "evaluate paren:" + toEvaluate.toString());
            toEvaluate = evaluateUnary(toEvaluate);
            Debug.print(Debug.EVALUATOR_FLAG, "evaluate unary:" + toEvaluate.toString());
            toEvaluate = evaluateFunction(toEvaluate);
            Debug.print(Debug.EVALUATOR_FLAG, "Evaluate function: " + toEvaluate.toString());
            toEvaluate = evaluateMDMod(toEvaluate);
            Debug.print(Debug.EVALUATOR_FLAG, "evaluate mdm:" + toEvaluate.toString());
            toEvaluate = evaluatePM(toEvaluate);
            Debug.print(Debug.EVALUATOR_FLAG, "evaluate pm:" + toEvaluate.toString());
            toEvaluate = evaluateCompare(toEvaluate);
            //ADD: logical ops

            Debug.print(Debug.EVALUATOR_FLAG, "Evaluate final: " + toEvaluate);
            return toEvaluate;
        }

        /**
         * Creates a new ArrayList of only literal values of variables. (Ensures that evaluation itself does not need to determine references.)
         * @param items
         * @return 
         */
        public ArrayList toLiterals(ArrayList<VariablePTC> items){
            ArrayList newItems = new ArrayList<>();

            int i = 0;
            while(i < items.size()){//VariablePTC item : items) {
                VariablePTC item = items.get(i);
                if (item.getType() == VariablePTC.STRING_REFERENCE){
                    VariablePTC var = vars.getVariable((StringPTC) item);
                    if (var.getType() == VariablePTC.ARRAY){
                        //current location: i or the variable name
                        ArrayList<VariablePTC> name = new ArrayList<>();
                        name.add(item);
                        i++; //advances past name
                        name.add(items.get(i)); //add bracket
                        i++; //advance past bracket
                        int nest = 1; //because 1st bracket
                        while (i < items.size() && nest > 0){ //nest > 0 until closing bracket is hit.
                            item = items.get(i);

                            if (item.equals(new StringPTC("]")) || item.equals(new StringPTC(")")))
                                nest--;
                            else if (item.equals(new StringPTC("[")) || item.equals(new StringPTC("(")))
                                nest++;

                            name.add(item);

                            i++;
                        } //name has been obtained
                        Debug.print(Debug.EVALUATOR_FLAG, name.toString());
                        newItems.add(vars.getVariable(name));

                    } else {
                        //it's just a normal variable.
                        newItems.add(var);
                    }
                } else {
                    newItems.add(item);
                }
                Debug.print(Debug.EVALUATOR_FLAG, "Literals: " + item + " " + newItems.get(newItems.size()-1));
                i++;
            }

            return newItems;
        }

        public ArrayList evaluateParens(ArrayList<VariablePTC> items){
            int location = 0;
            ArrayList miniItems;

            while (location < items.size()){
                VariablePTC item = items.get(location);
                miniItems = new ArrayList();
                if (item.getType() == StringPTC.STRING_OPERATOR && items.get(location-1).getType() != VariablePTC.STRING_FUNCTION){
                    String op = item.toString();
                    //System.out.println("Paren?: " + op);
                    if (op.equals("(")){
                        int nest = 0;
                        location++; //move past opening parenthesis
                        while (!op.equals(")") && nest <= 0){
                            item = items.get(location);
                            op = item.toString();
                            items.remove(location);
                            miniItems.add(item);

                            if (op.equals("(")) //|| op.equals("["))
                                nest++;
                            if (op.equals(")")) //|| op.equals("]"))
                                nest--;
                        }
                        //miniItems to be evaluated now. All items have been removed from original list.
                        items.set(location--, (VariablePTC) evaluate(miniItems).get(0)); //replace opening paren with evaluated miniitems
                    } /*else if (op.equals("[")){
                        int nest = 0;
                        location++; //move past opening parenthesis
                        while (!op.equals("]") && nest <= 0){
                            item = items.get(location);
                            op = item.toString();
                            items.remove(location);
                            miniItems.add(item);

                            if (op.equals("[") || op.equals("("))
                                nest++;
                            if (op.equals("]") || op.equals(")"))
                                nest--;
                        }
                        //miniItems to be evaluated now. All items have been removed from original list.
                        items.set(location--, (VariablePTC) evaluate(miniItems).get(0)); //replace opening paren with evaluated miniitems
                        //assign the value of the index to whatever string reference is before it 
                    } */ else
                        location++;
                } else
                    location++;
            }

            return items;
        }

        private ArrayList evaluateUnary(ArrayList<VariablePTC> items){
            int location = items.size() - 1;

            while (location >= 0){
                VariablePTC item = items.get(location);
                if (item.getType() == StringPTC.STRING_OPERATOR){
                    String op = item.toString();
                    //System.out.println("Unary?" + op);
                    switch (op) {
                        case "not":
                            items.set(location, MathPTC.not((NumberPTC)items.get(location+1))); //do a not on a num
                            items.remove(location+1); //it has been C O N S U M E D.
                            break;
                        case "!":
                            items.set(location, MathPTC.logicalNot((NumberPTC)items.get(location+1)));
                            items.remove(location+1); //C  O  N  S  U  M  E
                            break;
                        case "-":
                            if (items.get(location-1).getType() != VariablePTC.NUMBER_LITERAL){
                                items.set(location, MathPTC.negate((NumberPTC)items.get(location+1)));
                                items.remove(location+1); //all will be E M U S N O C;-;-;-;
                            } else
                                location--;
                            break;
                        case ";":
                            //create a STRING from whatever was here, and make sure no newlines.
                            StringPTC temp = items.get(location-1).toStringPTC();
                            temp.setLine(false);
                            items.set(location, temp);
                            items.remove(location-1);
                            location--;
                            break;
                        default:
                            location--;
                            break;
                    }
                } else
                    location--;
            }

            return items;
        }

        public ArrayList evaluateFunction(ArrayList<VariablePTC> items){
            int location = 0;

            while (location < items.size()){
                VariablePTC item = items.get(location);
                if (item.getType() == StringPTC.STRING_FUNCTION){
                    StringPTC func = (StringPTC) item;
                    location++; //move past func name;
                    items.remove(location); //remove opening parenthesis

                    ArrayList<VariablePTC> args = new ArrayList<>();

                    int nest = 0;
                    while (nest >= 0 && location < items.size()){
                        item = items.get(location);
                        String op = item.toString();
                        args.add(item);
                        items.remove(location);

                        if (op.equals("(")) //|| op.equals("["))
                            nest++;
                        if (op.equals(")")) //|| op.equals("]"))
                            nest--;
                    }

                    Debug.print(Debug.EVALUATOR_FLAG, "items: " + items.toString() + "args: " + args.toString());
                    if (!args.isEmpty())
                        args.remove(args.size()-1); //remove closing parenthesis
                    Debug.print(Debug.EVALUATOR_FLAG, args.toString());

                    //args is list of all items (within parens, not including outermost parens)
                    args = evaluate(args);
                    int i = 0;
                    while (i < args.size()){
                        if (args.get(i).getType() == VariablePTC.ARG_SEPARATOR)
                            args.remove(i);
                        else
                            i++;
                    }

                    //function args have been evaluated, commas removed, now call the required function and store the result back into the expression
                    Debug.print(Debug.EVALUATOR_FLAG, "Function called: " + func + "Function args: " + args.toString());
                    items.set(location-1, call(func, args)); //location-1 = func name; new location-1 = (whatever result of function was)
                } else
                    location++;

            }

            return items;
        }

        /**
         * Calls a function and returns the desired values. Format is arg1, arg2...
         * @param function
         * @param args
         * @return 
         */
        private VariablePTC call(StringPTC function, ArrayList<VariablePTC> args){
            //func() is part of the outer class, which calls a component's func().
            return func(function, args);
        }

        private ArrayList evaluateMDMod(ArrayList<VariablePTC> items){
            int location = 0;

            while (location < items.size()){
                VariablePTC item = items.get(location);
                if (item.getType() == StringPTC.STRING_OPERATOR){
                    String op = item.toString();
                    //System.out.println("MDM?" + op);
                    switch (op) {
                        case "*":
                            items.set(location, MathPTC.mult((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)));
                            items.remove(location-1);
                            items.remove(location); //they have been C O N S U M E
                            break;
                        case "/":
                            items.set(location, MathPTC.div((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)));
                            items.remove(location-1);
                            items.remove(location);
                            break;
                        case "%":
                            items.set(location, MathPTC.mod((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)));
                            items.remove(location-1);
                            items.remove(location);
                            break;
                        default:
                            location++;
                            break;
                    }
                } else
                    location++;
            }       

            return items;
        }

        private ArrayList evaluatePM(ArrayList<VariablePTC> items){
            int location = 0;

            while (location < items.size()){
                VariablePTC item = items.get(location);
                if (item.getType() == StringPTC.STRING_OPERATOR){
                    String op = item.toString();
                    //System.out.println("AS?" + op);
                    switch (op) {
                        case "+":
                            if (items.get(location-1).getType() == VariablePTC.NUMBER_LITERAL &&
                                    items.get(location+1).getType() == VariablePTC.NUMBER_LITERAL){
                                items.set(location, MathPTC.add((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)));
                                items.remove(location-1);
                                items.remove(location); //they have been C O N S U M E
                            } else if (items.get(location-1).getType() == VariablePTC.STRING_LITERAL &&
                                    items.get(location+1).getType() == VariablePTC.STRING_LITERAL){
                                //do some string math or something
                                StringPTC first = (StringPTC) items.get(location-1);
                                first.add((StringPTC) items.get(location+1));
                                items.set(location, first);
                                items.remove(location-1);
                                items.remove(location);
                            }   break;
                        case "-":
                            items.set(location, MathPTC.sub((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)));
                            items.remove(location-1);
                            items.remove(location);
                            break;
                        default:
                            location++;
                            break;
                    }
                } else
                    location++;
            }       

            return items;
        }

        private ArrayList evaluateCompare(ArrayList<VariablePTC> items){
            int location = 0;

            while (location < items.size()){
                VariablePTC item = items.get(location);
                if (item.getType() == StringPTC.STRING_OPERATOR){
                    String op = item.toString();
                    //System.out.println("CP?" + op);
                    switch (op) {
                        case "==":
                            int eq;
                            if (items.get(location-1).equals(items.get(location+1)))
                                eq = 1;
                            else
                                eq = 0;
                            items.set(location, new NumberPTC(eq));
                            items.remove(location-1);
                            items.remove(location); //they have been C O N S U M E
                            break;
                        case "!=":
                            int ieq;
                            if (items.get(location-1).equals(items.get(location+1)))
                                ieq = 0;
                            else
                                ieq = 1;
                            items.set(location, new NumberPTC(ieq));
                            items.remove(location-1);
                            items.remove(location);
                            break;
                        case "<":
                            int lt;
                            if (MathPTC.lessThan((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)).getIntNumber() == 1)
                                lt = 1;
                            else
                                lt = 0;

                            items.set(location, new NumberPTC(lt));
                            items.remove(location-1);
                            items.remove(location);
                            break;
                        case ">":
                            int gt;
                            if (MathPTC.moreThan((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)).getIntNumber() == 1)
                                gt = 1;
                            else
                                gt = 0;

                            items.set(location, new NumberPTC(gt));
                            items.remove(location-1);
                            items.remove(location);
                            break;
                        case ">=":
                            int gteq;
                            if (MathPTC.moreThan((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)).getIntNumber() == 1
                                    || items.get(location-1).equals(items.get(location+1)))
                                gteq = 1;
                            else
                                gteq = 0;

                            items.set(location, new NumberPTC(gteq));
                            items.remove(location-1);
                            items.remove(location);
                            break;
                        case "<=":
                            int lteq;
                            if (MathPTC.lessThan((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)).getIntNumber() == 1
                                    || items.get(location-1).equals(items.get(location+1)))
                                lteq = 1;
                            else
                                lteq = 0;

                            items.set(location, new NumberPTC(lteq));
                            items.remove(location-1);
                            items.remove(location);
                            break;
                        default:
                            location++;
                            break;
                    }
                } else
                    location++;
            }       

            return items;
        }
    }
}
