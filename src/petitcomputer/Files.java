package petitcomputer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File handler class. Does the initial file loading, as well as loads from within interpreted programs.
 * @author minxr
 */
public class Files {
    String directory;
       
    public void load(StringPTC name){
        String fileName = name.toString();
        
        
    }
        
    public ArrayList<VariablePTC> initProgram(File file){
        if (!file.exists()){
            System.err.println("Your file does not exist...");
            return null;
        }
        directory = file.getAbsoluteFile().getParent();
        
        System.out.println(directory + file.getName());
        return readProgram(loadProgram(file.getName()));
    }
    
    /**
     * Reads a program into a byte array.
     * @param filename
     * @return 
     */
    public byte[] loadProgram(String filename){
        byte[] program;
        try {
            File programFile = new File(directory + "/" + filename);
            if (!programFile.exists()){
                System.err.println("Oh, file does not exist.");
                return null;
            }

            FileInputStream reader;
            reader = new FileInputStream(programFile);
            byte[] header = new byte[60];
            reader.read(header);
            program = new byte[Byte.toUnsignedInt(header[56]) + (Byte.toUnsignedInt(header[57]) << 8) + (Byte.toUnsignedInt(header[58]) << 16)];
            reader.read(program);
            return program;
        } catch (FileNotFoundException ex) {
            System.err.print(Arrays.toString(ex.getStackTrace()));
        } catch (IOException ex) {
            System.err.print(Arrays.toString(ex.getStackTrace()));
        }
        return null;
    }
    
    /**
     * Given a byte array, read the array into program tokens for use by the interpreter.
     * @param program 
     * @return  
     */
    public ArrayList<VariablePTC> readProgram(byte[] program){
        byte character;
        int position = 0;
        ArrayList<VariablePTC> items = new ArrayList();
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
        return items;
    }
    
    /**
     * Reads a header from the file.
     * @param file
     * @return 
     */
    private byte[] readHeader(File file){ 
        byte header[] = new byte[48];
        try {
            if (!file.exists()){
                System.err.println("Oh, file does not exist.");
                return null;
            }
            FileInputStream reader;
            reader = new FileInputStream(file);
            reader.read(header);
        } catch (FileNotFoundException ex) {
            System.err.print(Arrays.toString(ex.getStackTrace()));
        } catch (IOException ex) {
            System.err.print(Arrays.toString(ex.getStackTrace()));
        }
        return header;
    }
    
    private byte[] readData(File file, int offset, int size){
        byte data[] = new byte[size];
        try {
            if (!file.exists()){
                System.err.println("Oh, file does not exist.");
                return null;
            }
            FileInputStream reader;
            reader = new FileInputStream(file);
            reader.read(data, offset, size);
        } catch (FileNotFoundException ex) {
            System.err.print(Arrays.toString(ex.getStackTrace()));
        } catch (IOException ex) {
            System.err.print(Arrays.toString(ex.getStackTrace()));
        }
        return data;
    }
    
    /**
     * Method to check if a given string is a command name. Used during program file reading..
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
     * Method to check if a given string is a function name. Used during program file reading..
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
     * Simple method to check whether a given name is a logical operator or not. Used during program file reading..
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
}
