package petitcomputer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * Holds all important resources and handles program loading/reading.
 * @author minxr
 */
public class ProcessII implements Runnable {
    private final int GROUP_PROCESS = 0;
    private final int GROUP_CONSOLE = 1;
    private final int GROUP_BACKGROUND = 2;
    private final int GROUP_PANEL = 3;
    private final int GROUP_SPRITE = 4;
    private final int GROUP_SOUND = 5; //probably needs major changes to work at all
    private final int GROUP_GRAPHICS = 6;
    private final int GROUP_RESOURCE = 7;
    private final int GROUP_FILE = 8; //will require modification
    private final int GROUP_LOCAL = 9; //will require modification
    private final int GROUP_DATA = 10;
    private final int GROUP_MATH = 11;
    private final int GROUP_INPUT = 12;
    private final int GROUP_VARIABLE = 13;
    private final int GROUP_STRING = 14;
    private final int GROUP_UNDEFINED = 99;
    
    volatile boolean pleaseLoad, pleaseRun; //uesd for cross-thread communication
    
    String fileName;
    File file;
    
    Errors error;
    Resources resource;
    VariablesII variables;
    
    Input input;
    Data data;
    Console console;
    Background bg;
    Panel panel;
    Sound sound;
    Graphic graphics;
    
    Code main;
    Evaluator eval;
    
    ArrayList<VariablePTC> items;
    
    byte[] program;
    
    /**
     * Creates a new Process and initializes some important resources.
     */
    public ProcessII(){
        eval = new Evaluator();        
        resource = new Resources();
        variables = new VariablesII(eval);
        input = new Input();
        data = new Data(variables, eval);
        
        console = new Console(resource.getBGFU(), resource.getCOL0(), eval);
        console.setIn(input);
        console.setVariables(variables);
        console.init();
        
        panel = new Panel(resource.getBGFL(), resource.getCOL0(), resource.getBGD(), eval);
        bg = new Background(resource.getBGUU(), resource.getCOL0(), eval);
        sound = new Sound(eval);
        graphics = new Graphic(eval, resource.getCOL2());

    }
    
    public void setFile(String filename){
        fileName = filename; //bad naming but ok
    }
    
    public void setFile(File file){
        this.file = file; //even worse naming, but ok
    }
    
    public void load(){
        loadProgram(file);
        readProgram();
        data.setProgramData(items); //items is only initialized after readProgram();
    }
    
    /**
     * Loads and executes the program. 
     */
    public void runProgram(){
        load();
        execute();
    }
    
    /**
     * Runs the program. Any errors will be returned as Errors, otherwise null is returned.
     * @return 
     */
    public Errors execute(){
        //Create an object to run a chunk of code
        main = new Code(items, 0);
        
        error = main.execute();
        
        Debug.print(Debug.PROCESS_FLAG, "ERROR: " + error + "\nVARS:\n" + variables.toString());
        
        return error;
    }
    
    public Errors act(StringPTC command, ArrayList<ArrayList> arguments){
        Debug.print(Debug.ACT_FLAG, "ACT PROCESS: " + command.toString());
        switch (command.toString().toLowerCase()){
            case "end":
                //jump to program end :)
                main.setLocation(items.size());
                break;
            case "wait":
                NumberPTC frames = (NumberPTC) eval.eval(arguments.get(0));
                
                wait(frames.getIntNumber());
                break;
            case "if":
                conditional(arguments);
                break;
            case "then":
                //will never run unless an IF is missing. So, return an error.
                main.setError(Errors.UNDEFINED_ERROR);
                break;
            case "else":
                //means that a then block has ended.
                //Skip to EOL.
                main.readUntil(items, new StringPTC(Character.toString((char)CharacterPTC.LINEBREAK)));
                break;
            case "goto":
                StringPTC label = (StringPTC) eval.eval(arguments.get(0));
                
                go_to(label);
                break;
            case "on":
                on(arguments);
                break;
            case "for":
                for_to(arguments);
                break;
            case "next":
                next();
                break;
        }
        return null;
    }
    
    public void for_to(ArrayList<ArrayList> args){
        ArrayList<VariablePTC> arguments = new ArrayList<>();
        
        for (ArrayList arg : args)
            arguments.addAll(arg);
        //arguments is more or less a list of code now
        
        ArrayList<VariablePTC> variable = new ArrayList<>();
        ArrayList<VariablePTC> expression = new ArrayList<>();
        ArrayList<VariablePTC> comparison = new ArrayList<>();
        ArrayList<VariablePTC> increment = new ArrayList<>();
        
        int i = 0;
        int toLocation = 0;     //ints not booleans
        int stepLocation = 0;   //in case it's useful later
        int equalLocation = 0;  //who knows if it helped
        
        do {
            String arg = arguments.get(i).toString().toLowerCase();
            
            switch (arg) {
                case "to":
                    toLocation = i + 1;
                    break;
                case "step":
                    stepLocation = i + 1;
                    break;
                case "=":
                    equalLocation = i;
                    expression.add(arguments.get(i));
                    break;
                default: //cool
                    if (equalLocation == 0){ //not end of variable
                        variable.add(arguments.get(i));
                        expression.add(arguments.get(i));
                    }else if (toLocation == 0) //hasn't found end of initialization expression
                        expression.add(arguments.get(i));
                    else if (stepLocation == 0) //hasn't found the STEP in (FOR...TO...STEP...)
                        comparison.add(arguments.get(i));
                    else //there's a step, read until end of line
                        increment.add(arguments.get(i));
                    break;
            }
            
            i++;//next element
        } while (i < arguments.size());
        
        if (increment.isEmpty())
            increment.add(new NumberPTC(1));
        
        //initialize variable
        Code init = new Code(expression, 0);
        init.execute();
        
        //exit condition
        StringPTC lessThan = new StringPTC("<=");
        lessThan.setType(VariablePTC.STRING_OPERATOR);
        
        ArrayList<VariablePTC> condition = new ArrayList<>();
        condition.addAll(variable);             //variable
        condition.add(lessThan);                //<=
        condition.addAll(comparison);           //end state
        
        //step equation
        StringPTC equals = new StringPTC("=");
        equals.setType(VariablePTC.STRING_OPERATOR);
        
        StringPTC plus = new StringPTC("+");
        plus.setType(VariablePTC.STRING_OPERATOR);
        
        StringPTC newLine = new StringPTC(Character.toString((char)CharacterPTC.LINEBREAK));
        newLine.setType(VariablePTC.LINE_SEPARATOR);
        
        ArrayList<VariablePTC> steps = new ArrayList<>();
        steps.addAll(variable);                 //variable
        steps.add(equals);                      //=
        steps.addAll(variable);                 //variable
        steps.add(plus);                        //+
        steps.addAll(increment);                //step
       // steps.add(newLine);                     //(endline)
        
        Code step = new Code(steps, 0);
        
        int endOfFor = main.getLocation(); //location is at the end of the FOR-TO[-STEP]. 
        
        while (((NumberPTC)eval.eval(condition)).getIntNumber() == 1){
            main.setLocation(endOfFor);
            Errors err = main.execute();
            //gets here after hitting "next"
            if (err == Errors.NEXT_WITHOUT_FOR)
                main.setError(null); //there's a FOR so it's actually fine.
            
            Debug.print(Debug.PROCESS_FLAG, "FOR LOOP DEBUG\nXXXXXXXXXXXXXXXXXXXXx\n" + expression.toString() + "\n" + condition.toString() + "\n" + steps.toString());
            step.setLocation(0); //reset increment
            step.execute(); //lincrement the var
        }
        //loop is done.
        
        //System.out.println(Evaluator.eval(condition).toString());
    }
    
    public void next(){
        main.setError(Errors.NEXT_WITHOUT_FOR); //main has encountered a next
    }
    
    /**
     * Return function.
     */
    public void ret(){
        main.setError(Errors.RETURN_WITHOUT_GOSUB);
    }
    
    /**
     * Depending on a given expression, jump to a set location.
     * @param args
     */
    public void on(ArrayList<ArrayList> args){
        ArrayList<VariablePTC> arguments = new ArrayList<>(); //converts multiarg to a single list of args
        
        for (ArrayList arg : args)
            arguments.addAll(arg);
        
        ArrayList<VariablePTC> expression = new ArrayList<>();
        ArrayList<VariablePTC> labels = new ArrayList<>();
        
        int i = 0;
        boolean isGosub = false;
        int goLocation = 0;
        do {
            String arg = arguments.get(i).toString().toLowerCase();
            
            switch (arg) {
                case "gosub":
                    isGosub = true;
                case "goto":
                    goLocation = i;
                    break;
                default: //cool
                    if (goLocation == 0) //hasn't found end of 'on' expression
                        expression.add(arguments.get(i));
                    else
                        labels.add(arguments.get(i));
                    break;
            }
            
            i++;//next element
        } while (i < arguments.size());
        
        Debug.print(Debug.PROCESS_FLAG, "ON expression: " + expression.toString());
        Debug.print(Debug.PROCESS_FLAG, "GOTO/SUB: " + labels.toString());
        
        //evaluate expression a
        NumberPTC result = (NumberPTC) eval.eval(expression);
        
        if (!isGosub)
            go_to((StringPTC) labels.get(result.getIntNumber()));
        else
            gosub((StringPTC) labels.get(result.getIntNumber()));
    }
    
    /**
     * GOTO function - sets the main location of the program to the location of the given label.
     * @param argument
     */
    public void go_to(StringPTC argument){
        //takes a label
        String label = argument.toString().toLowerCase();
        //finds a label
        int location = items.size(); //default to end of program
        for (int i = 0; i < items.size(); i++)
            if (label.equals(items.get(i).toString().toLowerCase()))
                if (i == 0 || items.get(i-1).getType() == VariablePTC.LINE_SEPARATOR){
                    location = i;
                    break;
            }
        
        Debug.print(Debug.PROCESS_FLAG, "Location found: " + location + "\nLabel search for: " + label + "\nLabel found: " + items.get(location).toString());
        //sets main location to that label
        main.setLocation(location);
        //well shit, what if main isn't executing and instead it's a subobject like IF?
    }
    
    /**
     * GOSUB: jumps to a sub routine and saves the jump location. Returns to the location after hitting a RETURN.
     * @param argument
     * @return 
     */
    public Errors gosub(StringPTC argument){
        //do it later.
        String label = argument.toString().toLowerCase();
        //find label
        int location = items.size();
        for (int i = 0; i < items.size(); i++)
            if (label.equals(items.get(i).toString().toLowerCase()))
                if (i == 0 || items.get(i-1).getType() == VariablePTC.LINE_SEPARATOR){
                    location = i;
                    break;
            }
        
        Debug.print(Debug.PROCESS_FLAG, "Location found: " + location + "\nLabel search for: " + label + "\nLabel found: " + items.get(location).toString());
        
        int callLocation = main.getLocation(); //end of GOSUB command
        
        main.setLocation(location);
        Errors err = main.execute();
        
        if (err == Errors.RETURN_WITHOUT_GOSUB)
            main.setLocation(callLocation); //RETURN lends to here
        else
            return err;
        
        return null;
    }
    
    /**
     * IF statements, but using PTC logic. 
     * @param args 
     */
    public void conditional(ArrayList<ArrayList> args){
        ArrayList<VariablePTC> arguments = new ArrayList<>();
        
        for (ArrayList arg : args)
            arguments.addAll(arg);        
        //arguments roughly of form (expression THEN statement(s) [ELSE statements])
        
        //read until THEN or GOTO: from beginning to here is expression to evaluate.
        ArrayList<VariablePTC> expression = new ArrayList<>();
        ArrayList<VariablePTC> thenCode = new ArrayList<>();
        ArrayList<VariablePTC> elseCode = new ArrayList<>();
        
        int i = 0;
        int thenLocation = 0;
        int elseLocation = 0;
        do {
            String arg = arguments.get(i).toString().toLowerCase();
            
            switch (arg) {
                case "then":
                    thenLocation = i + 1;
                    break;
                case "goto":
                    if (thenLocation == 0) //prevent "...THEN <something>:GOTO..." from breaking and skipping something, right?
                        thenLocation = i;
                    break;
                case "else":
                    elseLocation = i + 1;
                    break;
                default: //obsolete, but good for debug and might be useful later
                    if (thenLocation == 0) //hasn't found end of expression
                        expression.add(arguments.get(i));
                    else if (elseLocation == 0) //hasn't found an ELSE yet
                        thenCode.add(arguments.get(i));
                    else  //at this point, it will go until it hits the end of the list, and is definitely an else block.
                        elseCode.add(arguments.get(i));
                    break;
            }
            
            i++;//next element
        } while (i < arguments.size());            
        
        Debug.print(Debug.PROCESS_FLAG, Arrays.toString(expression.toArray()));
        Debug.print(Debug.PROCESS_FLAG, Arrays.toString(thenCode.toArray()));
        Debug.print(Debug.PROCESS_FLAG, Arrays.toString(elseCode.toArray()));
        
        NumberPTC result = (NumberPTC) eval.eval(expression); //condition of IF: true or false? (really, 0 or !0)
        
        //Code then = new Code(thenCode, 0);
        //Code notThen = new Code(elseCode, 0);
        
        Debug.print(Debug.PROCESS_FLAG, "result:" + result.toString());
        
        StringPTC go = new StringPTC("GOTO");
        go.setType(VariablePTC.STRING_COMMAND);
        
        //THEN...LABEL        

        //THEN ...CODE... [ELSE]...
        if (result.getDoubleNumber() != 0){
            if (thenCode.get(0).getType() == VariablePTC.STRING_LABEL){
                go_to((StringPTC) thenCode.get(0));
            } else 
                main.setLocation(main.getLocation() - arguments.size() + thenLocation); //go from end of IF back to beginning and add the offset of the THEN.
            //(conditional(args) only has a piece of the whole program and thus locations are off by that much.)
        } else //ELSE ...CODE
            if (elseLocation != 0)
                main.setLocation(main.getLocation() - arguments.size() + elseLocation); //Same thing with the added condition that an else has to exist for it to be used. :D
    }
    /**
     * Waits the desired number of frames.
     * @param frames 
     */
    public void wait(int frames){
        try {
            Thread.sleep(frames * 1000 / 60); 
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessII.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Loads a program in .PTC format from a file with the given name. Reads the header into a temporary array, and the program data into a byte array.
     * @param filename 
     */
    public void loadProgram(String filename){
        try {
            File programFile = new File("programs/" + filename);
            if (!programFile.exists()){
                System.err.println("Oh, file does not exist.");
                return;
            }

            FileInputStream reader;
            reader = new FileInputStream(programFile);
            byte[] header = new byte[60];
            reader.read(header);
            program = new byte[Byte.toUnsignedInt(header[56]) + (Byte.toUnsignedInt(header[57]) << 8) + (Byte.toUnsignedInt(header[58]) << 16)];
            reader.read(program);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProcessII.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProcessII.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(Byte.toUnsignedInt(header[56])); //signed bytes reeeee
        //System.out.println(Byte.toUnsignedInt(header[57]) << 8);
        //System.out.println(Byte.toUnsignedInt(header[58]) << 16);
    }
    
    public void loadProgram(File actualFile){
        try {
            File programFile = actualFile;
            if (!programFile.exists()){
                System.err.println("Oh, file does not exist.");
                return;
            }

            FileInputStream reader;
            reader = new FileInputStream(programFile);
            byte[] header = new byte[60];
            reader.read(header);
            program = new byte[Byte.toUnsignedInt(header[56]) + (Byte.toUnsignedInt(header[57]) << 8) + (Byte.toUnsignedInt(header[58]) << 16)];
            reader.read(program);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProcessII.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProcessII.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Reads the program and converts all of the program into items to be processed.
     * Requires loadProgram() to be called first.
     */
    public void readProgram(){
        byte character;
        int position = 0;
        items = new ArrayList();
        StringPTC item;
        
        try {
        while (position < program.length){
            item = new StringPTC(0);
            character = program[position];
            //check character type
            if (CharacterPTC.isLetter(character)){
                do {
                    //add char
                    item.add(character);
                    
                    //get next char
                    position++;
                    character = program[position];
                } while (CharacterPTC.isLetter(character) || CharacterPTC.isNumber(character) || (character == CharacterPTC.DOLLAR));    
                int type = VariablePTC.STRING_REFERENCE;
                
                if (isCommand(item))
                    type = VariablePTC.STRING_COMMAND;
                if (isFunction(item))
                    type = VariablePTC.STRING_FUNCTION;
                if (isOperator(item))
                    type = VariablePTC.STRING_OPERATOR;
                
                item.setType(type);
                items.add(item);
            } else if (CharacterPTC.isNumber(character)){
                do {
                    //add char
                    item.add(character);
                    
                    //get next char
                    position++;
                    character = program[position];
                } while (CharacterPTC.isNumber(character));
                item.setType(VariablePTC.STRING_EXPRESSION); //pretty much useless
                NumberPTC num = item.getNumberFromString();
                num.setType(VariablePTC.NUMBER_LITERAL);
                items.add(num);//item);
            } else if (CharacterPTC.isSymbol(character)){
                if (character == CharacterPTC.QUOTE){
                    position++;
                    character = program[position];
                    while (character != CharacterPTC.QUOTE) {
                        //add char
                        item.add(character);

                        //get next char
                        position++;
                        character = program[position];
                    } //while (character != CharacterPTC.QUOTE);
                    position++; //move past end quote
                    item.setType(VariablePTC.STRING_LITERAL);
                    items.add(item);
                } else if (character == CharacterPTC.COMMENT){
                    do {
                        //add char
                        //item.add(character);

                        //get next char
                        position++;
                        character = program[position];
                    } while (character != CharacterPTC.LINEBREAK);
                } else if (character == CharacterPTC.COMMA) {
                    item.add(character);
                    item.setType(VariablePTC.ARG_SEPARATOR);
                    items.add(item);
                    position++;
                } else if (character == CharacterPTC.LABEL) {
                    do {
                        //add char
                        item.add(character);

                        //get next char
                        position++;
                        character = program[position];
                    } while (CharacterPTC.isLetter(character) || CharacterPTC.isNumber(character));    
                    item.setType(VariablePTC.STRING_LABEL);
                    items.add(item);
                } else if (CharacterPTC.isContainer(character)) { 
                    item.add(character);
                    item.setType(VariablePTC.STRING_OPERATOR);
                    items.add(item);
                    position++;                                    
                } else {
                    do {
                        //add char
                        item.add(character);

                        //get next char
                        position++;
                        character = program[position];
                    } while (CharacterPTC.isSymbol(character) && character != CharacterPTC.QUOTE);
                    item.setType(VariablePTC.STRING_OPERATOR);
                    items.add(item);
                }
            } else if (CharacterPTC.isReturn(character)){
                item.add(character);
                item.setType(VariablePTC.LINE_SEPARATOR);
                
                items.add(item);
                position++;
            } else {
                position++;
            }
        }
        } catch (Exception e){
            System.err.println(e.getMessage() + Arrays.toString(e.getStackTrace()) + e.getCause());
        }
    }
    
    public Image getImage(String screenName){
        if (screenName.equals("upper"))
            return getTopImage();
        else
            return getBottomImage();
    }
    public Image getTopImage(){
        BufferedImage image = new BufferedImage(256, 192, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        
        //actual draw order will be based on draw priority, but this is unfinished for now.
        g.fillRect(0, 0, PetitComGUI.WINDOW_WIDTH, PetitComGUI.WINDOW_HEIGHT);
        g.drawImage(graphics.createImage(0), 0, 0, null);
        g.drawImage(bg.createImage(0), 0, 0, null);
        g.drawImage(console.createImage(), 0, 0, null);
        
        return image;
    }
    public Image getBottomImage(){
        BufferedImage image = new BufferedImage(256, 192, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        
        //panel!
        g.drawImage(panel.createImage(), 0, 0, null);
        
        return image;
    }
    
    /**
     * Function to calculate the type of function.
     * @param function
     * @return 
     */
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
            case "if":
            case "then":
            case "else":
            case "goto":
            case "wait":
            case "end":
            case "on":
            case "for":
            case "next":
                group = GROUP_PROCESS;
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
    
    /**
     * Method to check if a given string is a command name. 
     * @param expression
     * @return 
     */
    private static boolean isCommand(StringPTC expression){
        String[] functions = new String[]{  "acls", "append", "beep", "bgclip", "bgclr", "bgcopy", "bgfill", "bgmclear", "bgmplay", "bgmprg", 
                                            "bgmset", "bgmsetd", "bgmsetv", "bgmstop", "bgmvol", "bgofs", "bgpage", "bgput", "bgread", "brepeat",
                                            "chrinit", "chrread", "chrset", "clear", "cls", "colinit", "color", "colread", "colset", "cont",
                                            "data", "delete", "dim", "dtread", "else", "end", "exec", "for", "gbox", "gcircle", "gcls", "gcolor",
                                            "gcopy", "gdrawmd", "gfill", "gline", "gosub", "goto", "gpage", "gpaint", "gpset", "gprio", "gputchr",
                                            "iconclr", "iconset", "if", "input", "key", "linput", "list", "load", "locate", "new", "next", "not",
                                            "on", "pnlstr", "pnltype", "print", "read", "reboot", "recvfile", "rem", "rename", "restore",
                                            "return", "rsort", "run", "save", "sendfile", "sort", "spangle", "spanim", "spchr", "spclr", "spcol",
                                            "spcolvec", "sphome", "spofs", "sppage", "spread", "spscale", "spset", "spsetv", "step", "stop",
                                            "swap", "then", "tmread", "to", "visible", "vsync", "wait"};
        String text = expression.toString().toLowerCase(); //reliable because of character similarities
        boolean isFunc = false;
        
        for (String function : functions) {
            isFunc |= text.equals(function);
        }
        
        return isFunc;
    }
    /**
     * Method to check if a given string is a function name. 
     * @param expression
     * @return 
     */
    private static boolean isFunction(StringPTC expression){
        String[] functions = new String[]{  "abs", "asc", "atan", "bgchk", "bgmchk", "bgmgetv", "btrig", "button", "chkchr", "chr$",
                                            "cos", "deg", "exp", "floor", "gspoit", "hex$", "iconchk", "inkey$", "instr", "left$",
                                            "len", "log", "mid$", "pi", "pow", "rad", "right$", "rnd", "sgn", "sin",
                                            "spchk", "spgetv", "sphit", "sphitrc", "sphitsp", "sqr", "str$", "subst$", "tan", "val"};
        String text = expression.toString().toLowerCase(); //reliable because of character similarities
        boolean isFunc = false;
        
        for (String function : functions) {
            isFunc |= text.equals(function);
        }
        
        return isFunc;
    }
    /**
     * Simple method to check whether a given name is a logical operator or not.
     * @param expression
     * @return 
     */
    private static boolean isOperator(StringPTC expression){
        String text = expression.toString().toLowerCase();
        boolean isOp = false;
        
        isOp |= text.equals("and");
        isOp |= text.equals("or");
        isOp |= text.equals("xor");
        isOp |= text.equals("not");
        
        return isOp;
    }
    
    public synchronized void pleaseLoad(){
        pleaseLoad = true;
    }
    public synchronized void pleaseRun(){
        pleaseRun = true;
    }
    
    public void setInputs(int buttons, int keyboard){
        input.setButton(buttons);
        input.setKSC(keyboard);
        //Debug.print(Debug.ALL, "B: " + buttons + " K: " + keyboard);
    }
    
    /**
     * Class to contain code and execute code pieces. Requires various pieces of ProcessII to function properly. 
     * @author minxr
     */
    private class Code {
        Errors error;
        ArrayList<VariablePTC> items;
        int location;

        public Code(ArrayList items, int startLocation){
            this.items = items;
            location = startLocation;
            error = null;
        }
        
        public Errors execute(){
            VariablePTC command;
            ArrayList<ArrayList> arguments;
            ArrayList<VariablePTC> singleArg;
            error = null;
            
            while (location < items.size() && error == null){
                VariablePTC item = items.get(location);
                Debug.print(Debug.CODE_FLAG, "Item# " + location + " = " + item.toString());

                switch (item.getType()) {
                    case VariablePTC.STRING_COMMAND:
                        command = item;
                        if (command.toString().toLowerCase().equals("if")){
                            //IF requires special reading of args. (to eol) //no, not really
                            
                            location++; //move past IF
                            singleArg = readUntil(items, new StringPTC(Character.toString((char)CharacterPTC.LINEBREAK)));
                            
                            /*multiArg = new ArrayList();
                            multiArg.add(singleArg);
                            
                            System.out.println("IF args++ -> " + multiArg.toString());
                            arguments = new ArrayList();
                            for (int i = 0; i < multiArg.size(); i++)
                               arguments.addAll(multiArg.get(i));*/
                            arguments = new ArrayList<>();
                            arguments.add(singleArg);
                            
                            Debug.print(Debug.CODE_FLAG, "IF arguments: " + Arrays.deepToString(arguments.toArray()));
                        } else {
                            //it's a command. Get the argument.
                            arguments = readArgs(items);//, location);

                            Debug.print(Debug.CODE_FLAG, command.toString() + " || " + arguments.toString());
                        }
                        
                        //in this case, arguments is an arraylist of arraylists.
                        //The outer array list groups by argument: each inner array list is a single expresion to be evaluated with Evaluator.eval() as necessary.
                        call((StringPTC) command, arguments);

                        break;
                    //case VariablePTC.NUMBER_LITERAL:
                    case VariablePTC.STRING_REFERENCE:
                        //new code:
                        ArrayList name = readUntil(items, new StringPTC("=")); //var name;
                        location++; //past '=' sign
                        ArrayList expression = readUntil(items, VariablePTC.LINE_SEPARATOR);
                        Debug.print(Debug.CODE_FLAG, "LET: "  + name.toString() + " = " + expression.toString());
                        
                        variables.setVariable(name, eval.eval(expression));
                        break;
                    default:
                        location++;
                        break;
                }
            }
            return error;
        }

        /**
         * Reads arguments of a function from the given starting location to the next newline.
         * @param items
         * @param startLocation
         * @return 
         */
        private ArrayList<ArrayList> readArgs(ArrayList<VariablePTC> items){
            ArrayList<ArrayList> fullArgs;
            ArrayList<VariablePTC> singleArg;

            fullArgs = new ArrayList();
            //int position = startLocation; //now is command location
            location++; //move past command to 1st arg (if exists)
            VariablePTC item = items.get(location);
            //if instant newline, no args, else read args
            //searcharg loop
            while (item.getType() != VariablePTC.LINE_SEPARATOR){
                item = items.get(location);
                singleArg = new ArrayList();
                //System.out.println("A: " + item.toString());
                int nest = 0;
                while (nest > 0 || (item.getType() != VariablePTC.LINE_SEPARATOR && item.getType() != VariablePTC.ARG_SEPARATOR)){
                    //repeat until comma or newline
                    singleArg.add(item);

                    location++;
                    item = items.get(location);
                    if (item.toString().equals("(") || item.toString().equals("["))
                        nest++;
                    if (item.toString().equals(")") || item.toString().equals("]"))
                        nest--;
                    //System.out.println("AC: " + item.toString());
                }
                fullArgs.add(singleArg);
                location++;
            }

            return fullArgs;
        }
        
        /**
         * Reads arguments into an ArrayList until the given variable data is found.
         * @param items
         * @param toMatch
         * @return 
         */
        public ArrayList<VariablePTC> readUntil(ArrayList<VariablePTC> items, VariablePTC toMatch){
            //ArrayList<ArrayList> fullArgs;
            ArrayList<VariablePTC> singleArg;

            //fullArgs = new ArrayList();
            VariablePTC item = items.get(location);
            //if instant newline, no args, else read args
            //searcharg loop
            singleArg = new ArrayList();
            while (!item.equals(toMatch)){
                //repeat until comma or newline
                singleArg.add(item);

                location++;
                if (location >= items.size())
                    break;
                
                item = items.get(location);
                //System.out.println("AC untilMatch: " + location + item.toString() + item.getType());
            }
            //System.out.println("final obj was" + item.toString() + item.equals(toMatch));
            return singleArg;
        }
        
        /**
         * Reads data into an ArrayList until the given type of data is found.
         * @param items
         * @param type
         * @return 
         */
        public ArrayList<VariablePTC> readUntil(ArrayList<VariablePTC> items, int type){
            //ArrayList<ArrayList> fullArgs;
            ArrayList<VariablePTC> singleArg;

            //fullArgs = new ArrayList();
            VariablePTC item = items.get(location);
            //if instant newline, no args, else read args
            //searcharg loop
            singleArg = new ArrayList();
            while (item.getType() != type){
                //repeat until comma or newline
                singleArg.add(item);

                location++;
                if (location >= items.size())
                    break;
                    
                item = items.get(location);
                //System.out.println("AC untilType: " + item.toString() + item.getType());
            }

            return singleArg;
        }
        
        /**
         * Calls the given function with the given arguments.
         * @param command
         * @param args 
         */
        private void call(StringPTC command, ArrayList<ArrayList> args){
            switch (getFunctionType(command.toString().toLowerCase())){
                case GROUP_CONSOLE:
                    console.act(command, args);
                    break;
                case GROUP_PROCESS:
                    act(command, args);
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
                    variables.act(command, args);
                    break;
                case GROUP_GRAPHICS:
                    graphics.act(command, args);
            }
        }

        public void setLocation(int newLocation){
            location = newLocation;
        }

        public void setError(Errors err){
            error = err;
        }
        
        public int getLocation(){
            return location;
        }

    }
    
    /**
     * Evaluates a string expression and converts it to the correct format: number, or string. Requires various pieces of ProcessII to function correctly.
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
                    VariablePTC var = variables.getVariable((StringPTC) item);
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
                        newItems.add(variables.getVariable(name));
                        ////OLD CODE
                        //get index, then get value.
                        /*i++; //move past NAME then BRACKET or paren
                        i++; //
                        int nest = 1; //for bracket that was passed
                        ArrayList<VariablePTC> index = new ArrayList<>();
                        
                        while (nest > 0){
                        item = items.get(i);
                        
                        if (item.equals(new StringPTC("]")) || item.equals(new StringPTC(")")))
                        nest--;
                        else if (item.equals(new StringPTC("[")) || item.equals(new StringPTC("(")))
                        nest++;
                        
                        index.add(item);
                        
                        i++;
                        if (i >= items.size())
                        break;
                        }
                        System.out.println(evaluate(index));
                        int idx = ((NumberPTC)evaluate(index).get(0)).getIntNumber();
                        //var's type is ARRAY already
                        newItems.add(((ArrayPTC) var).getElement(idx));*/

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
                    if (op.equals("not")){
                        items.set(location, MathPTC.not((NumberPTC)items.get(location+1))); //do a not on a num
                        items.remove(location+1); //it has been C O N S U M E D.
                    } else if (op.equals("!")){
                        items.set(location, MathPTC.logicalNot((NumberPTC)items.get(location+1)));
                        items.remove(location+1); //C  O  N  S  U  M  E
                    } else if (op.equals("-")){
                        if (items.get(location-1).getType() != VariablePTC.NUMBER_LITERAL){
                            items.set(location, MathPTC.negate((NumberPTC)items.get(location+1)));
                            items.remove(location+1); //all will be E M U S N O C;-;-;-;
                        } else
                            location--;
                    } else if (op.equals(";")){
                        //create a STRING from whatever was here, and make sure no newlines.
                        StringPTC temp = items.get(location-1).toStringPTC();
                        temp.setLine(false);
                        items.set(location, temp);
                        items.remove(location-1);
                        location--;
                    } else
                        location--;
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
                    args = eval.evaluate(args);
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
        
        public ArrayList evaluateMDMod(ArrayList<VariablePTC> items){
            int location = 0;

            while (location < items.size()){
                VariablePTC item = items.get(location);
                if (item.getType() == StringPTC.STRING_OPERATOR){
                    String op = item.toString();
                    //System.out.println("MDM?" + op);
                    if (op.equals("*")){
                        items.set(location, MathPTC.mult((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)));
                        items.remove(location-1);
                        items.remove(location); //they have been C O N S U M E
                    } else if (op.equals("/")){
                        items.set(location, MathPTC.div((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)));
                        items.remove(location-1);
                        items.remove(location);
                    } else if (op.equals("%")){
                        items.set(location, MathPTC.mod((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)));
                        items.remove(location-1);
                        items.remove(location);
                    } else
                        location++;
                } else
                    location++;
            }       

            return items;
        }

        public ArrayList evaluatePM(ArrayList<VariablePTC> items){
            int location = 0;

            while (location < items.size()){
                VariablePTC item = items.get(location);
                if (item.getType() == StringPTC.STRING_OPERATOR){
                    String op = item.toString();
                    //System.out.println("AS?" + op);
                    if (op.equals("+")){
                        if (items.get(location-1).getType() == VariablePTC.NUMBER_LITERAL &&
                            items.get(location+1).getType() == VariablePTC.NUMBER_LITERAL){
                            items.set(location, MathPTC.add((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)));
                            items.remove(location-1);
                            items.remove(location); //they have been C O N S U M E
                        } else if (items.get(location-1).getType() == VariablePTC.STRING_LITERAL &&
                                   items.get(location+1).getType() == VariablePTC.STRING_LITERAL){
                            //do some math or something
                            Debug.print(Debug.ALL, "Please remind the developer to implement string addition. Thanks!");
                        }
                    } else if (op.equals("-")){
                        items.set(location, MathPTC.sub((NumberPTC)items.get(location-1), (NumberPTC)items.get(location+1)));
                        items.remove(location-1);
                        items.remove(location);
                    } else
                        location++;
                } else
                    location++;
            }       

            return items;
        }

        public ArrayList evaluateCompare(ArrayList<VariablePTC> items){
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

        /**
         * Method to check if a given string is a function name. 
         * @param expression
         * @return 
         */
        /*private boolean isFunction(StringPTC expression){
            String[] functions = new String[]{  "abs", "asc", "atan", "bgchk", "bgmchk", "bgmgetv", "btrig", "button", "chkchr", "chr$",
                                                "cos", "deg", "exp", "floor", "gspoit", "hex$", "iconchk", "inkey$", "instr", "left$",
                                                "len", "log", "mid$", "pi", "pow", "rad", "right$", "rnd", "sgn", "sin",
                                                "spchk", "spgetv", "sphit", "sphitrc", "sphitsp", "sqr", "str$", "subst$", "tan", "val"};
            String text = expression.toString(); //reliable because of character similarities
            boolean isFunc = false;

            for (String function : functions) {
                isFunc |= text.equals(function);
            }

            return isFunc;
        }*/

        /**
         * Simple method to check whether a given name is a logical operator or not.
         * @param expression
         * @return 
         */
        /*private boolean isOperator(StringPTC expression){
            String text = expression.toString().toLowerCase();
            boolean isOp = false;

            isOp |= text.equals("and");
            isOp |= text.equals("or");
            isOp |= text.equals("xor");
            isOp |= text.equals("not");

            return isOp;
        }*/

        /*public static ArrayList toLiterals(ArrayList<VariablePTC> items){
            for (int i = 0; i < items.size(); i++){ //replace any references with their actual values
                if (items.get(i).getType() == VariablePTC.STRING_REFERENCE){
                    if (i >= items.size() && items.get(i+1).toString().equals("[")){
                        ArrayPTC refToVal = (ArrayPTC) vars.getVariable((StringPTC) items.get(i)); //find array
                        i++; //move past var name

                        items.remove(i);  //remove open bracket
                        ArrayList<VariablePTC> indexExpression = new ArrayList<>();
                        do {
                            indexExpression.add(items.get(i));
                            items.remove(i);
                        } while (!items.get(i).equals(new StringPTC("]"))); //repeat until closing bracket is found. Currently doesn't account for nesting, unfortunately
                        items.remove(i); //remove closing bracket

                        NumberPTC index = (NumberPTC) evaluate(toLiterals(indexExpression)).get(0);
                        items.set(i, refToVal.getElement(index.getIntNumber())); //ok ////gets the element at given index derived from given expression within brackets //////or something like that
                        System.out.println("Array: " + refToVal.getName() + " Index: " + index.toString() + " Value: " + items.get(i).toString());
                    } else {
                        VariablePTC refToVal = vars.getVariable((StringPTC) items.get(i));
                        refToVal.setName(items.get(i).toString()); //ensure that while the value is now active, the name reference still exists so the variable can be used for such as INPUT. tl;dr hacky workaround
                        items.set(i, refToVal);
                        System.out.println("Literal:" + refToVal.toString() + " Type:" + refToVal.getType());
                    }
                }
            }

            return items;
        }*/

        /**
        * Reads arguments into an ArrayList until the given variable data is found.
        * @param items
        * @param toMatch
        * @return 
        */
        public ArrayList<VariablePTC> readUntil(ArrayList<VariablePTC> items, VariablePTC toMatch){

            ArrayList<VariablePTC> readItems;

            int location = 0; 
            VariablePTC item = items.get(location);
            readItems = new ArrayList();
            while (!item.equals(toMatch)){
                readItems.add(item);

                location++;
                item = items.get(location);
            }

            return readItems;
        }
    }
    
    @Override
    public void run(){
        pleaseLoad = false;
        pleaseRun = false;
        boolean running = true;
        
        while (running){
            if (pleaseLoad){
                load();
                pleaseLoad = false;
            }
            if (pleaseRun){
                execute();
                pleaseRun = false;
                console.print(new StringPTC("OK"));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProcessII.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}