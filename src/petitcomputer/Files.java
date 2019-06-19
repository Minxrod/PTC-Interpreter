package petitcomputer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import static petitcomputer.COL.convertFromPTCFormat;

/**
 * File handler class. Does the initial file loading, as well as loads from within interpreted programs.
 * @author minxr
 */
public final class Files {
    public static final int HEADER_SIZE = 48;
    
    public static final int CHRBANK_SIZE = 256; //# characters
    public static final int CHR_SIZE = 32; //bytes
    public static final int COL_SIZE = 256; //# colors
    public static final int GRP_SIZE = 256*192; //width*height
    public static final int SCR_SIZE = 64*64*2; //width*height*bytes
    public static final int MEM_SIZE = 512; //bytes
    
    private static final byte[]
            TYPE_MEM = createType(2,"MEM"),
            TYPE_PRG = createType(3,"PRG"),
            TYPE_CHR = createType(1,"CHR"),
            TYPE_COL = createType(1,"COL"),
            TYPE_GRP = createType(1,"GRP"),
            TYPE_SCR = createType(1,"SCR");
    
    private static byte[] createType(int number, String resource){
        byte type[] = new byte[]{'P','E','T','C','0','X','0','0','R','X','X','X'};
        type[5] = (byte)(number + '0');
        int i = 9;
        for (char c : resource.toCharArray())
            type[i++] = (byte) c;
        
        return type;
    }
            
    private String directory;
        
    private final char[] ucs2;
    
    public Files(){
        directory = "src/resource/";
        ucs2 = new char[MEM_SIZE/2];
        byte tempUCS2[] = loadMEM("MCHRENC.PTC");
        for (int i = 0; i < MEM_SIZE; i+=2){
            ucs2[i/2] = (char) (tempUCS2[i] | (tempUCS2[i + 1] << 8));
        }
    }
    
    public ArrayList<VariablePTC> initProgram(File file){
        if (!file.exists()){
            System.err.println("Your file does not exist...");
            return null;
        }
        directory = file.getAbsoluteFile().getParent() + "/";
        
        //System.out.println(directory + file.getName());
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
            File programFile = new File(directory + filename);
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
                    } else if (CharacterPTC.isDash(character)) {
                        item.add(character);
                        item.setType(VariablePTC.STRING_OPERATOR);
                        items.add(item);
                        position++;
                    } else if (CharacterPTC.Char.QUESTION.getIndex() == character){ //replace question mark with PRINT
                        item = new StringPTC("PRINT");
                        item.setType(VariablePTC.STRING_COMMAND);
                        items.add(item);
                        position++;
                    } else if (CharacterPTC.Char.SEMICOLON.getIndex() == character) {
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
                        } while (CharacterPTC.isSymbol(character) && character != CharacterPTC.OPEN_PARENTHESIS && character != CharacterPTC.QUOTE && character != CharacterPTC.DASH);
                        //exceptions to == good =( bad
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
            System.err.println(e.getMessage() + Arrays.toString(e.getStackTrace()));
        }
        return items;
    }
    
    public short[][] loadColor(String filename){
        File defaultColorFile = new File(filename);
        FileInputStream in = null;
        try {
            in = new FileInputStream(defaultColorFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BGF.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!defaultColorFile.exists())
            System.err.println("Oh fricc you messed up...");
        
        byte[] header = new byte[48];
        //for (int i = 0; i < 48; i++){
        try {
            //System.out.println(i);
            in.read(header);
        } catch (IOException ex) {
            Logger.getLogger(BGF.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        short[][] tempBytes = new short[16][16];
        int tempCol;
        for (int i = 0; i < COL_SIZE; i++){
            try {
                //tempBytes = new byte[2];
                tempCol = (short) ((in.read() << 8) | in.read());
                tempBytes[i / 16][i % 16] = convertFromPTCFormat((short) tempCol);
            } catch (IOException ex) {
                Logger.getLogger(COL.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        return tempBytes;
    }
    
    public byte[] loadCHRBank(String filename, boolean fullPath){
        if (!fullPath) //If the full path isn't supplied use the default directory.
            filename = directory + filename;
        try {
            File file = new File(filename);
            if (!file.exists())
                System.err.println("ERROR: " + file.getName() + " not found.");
            
            FileInputStream in;
            in = new FileInputStream(file);
            byte[] header = new byte[HEADER_SIZE];
            in.read(header);
            
            byte[] tempBytes = new byte[CHRBANK_SIZE * CHR_SIZE];
            in.read(tempBytes);

            for (int j = 0; j < tempBytes.length; j++)
                tempBytes[j] = (byte) (((tempBytes[j] & 0x0F) << 4) | ((tempBytes[j] & 0xF0) >> 4)); //swap order of nibbles

            return tempBytes;
            
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        return null;
    }
    
    public byte[] loadMEM(String filename){
        String name = directory + filename;
        try {
            File file = new File(name);
            if (!file.exists()){
                System.out.println("ERROR: " + file.getName() + " not found.");
                saveMEM(filename, new StringPTC(0));
            }
            
            FileInputStream in;
            in = new FileInputStream(file);
            byte[] header = new byte[HEADER_SIZE];
            in.read(header);
            
            byte[] tempBytes = new byte[MEM_SIZE + 4]; //data + length of string
            in.read(tempBytes);
            
            //byte[] footer = new byte[4]; //number of bytes in string
            //in.read(footer);

            return tempBytes;
            
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
        return null;
    }
    
    /**
     * Converts file-loaded MEM string to a usable StringPTC.
     * @param data
     * @return 
     */
    public StringPTC readMEM(byte[] data){
        int size = data[MEM_SIZE + 0] | data[MEM_SIZE + 1] << 8; //string length
        StringPTC mem = new StringPTC(0);
        for (int i = 0; i < size; i++)
            mem.add(convertUCS2toPTC((char) (data[2 * i + 1] | (data[2 * i] << 8))));
        
        return mem;
    }
    
    public void saveMEM(String filename, StringPTC mem){
        try {
            System.out.println(Arrays.toString(Thread.currentThread().getStackTrace()));
            String name = directory + filename;
            
            File file = new File(name);
            if (!file.exists()){
                System.out.println("Creating file named " + filename);
                file.createNewFile();
            }
            
            FileOutputStream out;
            out = new FileOutputStream(file);
            
            byte[] dat = convertMEM(mem);
            out.write(createHeader(dat, filename.substring(0,8), TYPE_MEM));
            out.write(dat);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private byte[] convertMEM(StringPTC mem){
        byte[] data = new byte[MEM_SIZE+2];
        for (int i = 0; i < mem.getLength(); i++){
            int c = Byte.toUnsignedInt(mem.getCharacter(i));
            System.out.println(c);
            data[2 * i] = (byte)((ucs2[c] & 0xFF00) >>> 8);
            data[2 * i + 1] = (byte)(ucs2[c] & 0x00FF);
        }
        data[MEM_SIZE]=(byte) mem.getLength();
        data[MEM_SIZE+1]=(byte) ((mem.getLength() & 0xFF00) >>> 8);
        return data;
    }
    
    private byte convertUCS2toPTC(char c){
        for (int i = 0; i < 256; i++){
            if (c == ucs2[i])
                return (byte) i;
        }
        return -1;
    }
    
    /**
     * Creates a 48-byte header as would be generated in PTC.
     * The header requires the data of the file and the filetype to generate an
     * MD5 - so a byte array of the data must be provided.
     * @param data - array of file data for MD5 hash
     * @param filename - file name, all caps, no extension
     * @param type - file type string of format PETC00#0R###.
     * @return 
     */
    public byte[] createHeader(byte[] data, String filename, byte[] type){
        final byte[] petitcom = new byte[]{'P','E','T','I','T','C','O','M'};
        final byte[] px01 = new byte[]{'P','X','0','1'};
        /*  HEADER INFO
        *   bytes   data
         *  00-03   "PX01"
        *   04-07   length of <code>data</code> + 0x18
         *  08-0B   0x00000000
        *   0C-13   FILENAME
         *  14-23   MD5 of PETITCOM, file type string, and <code>data</code>
        *   24-2F   file type string
        */  
        
        byte[] head = new byte[HEADER_SIZE];
        
        int i = 0;
        for (byte b : px01)
            head[i++] = b;
        
        int length = data.length + 0x00000018;
        for (i = 0; i < 4; i++)
            head[4 + i] = (byte) ((length & (0x000000FF << i)) >>> i); 
        
        i=0x0c;
        for (char c : filename.toCharArray())
            head[i++] = (byte) c;
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            md.update(petitcom);
            md.update(data);
            md.update(type);
            
            byte[] result = md.digest();
            //System.out.println(Arrays.toString(result));
            System.arraycopy(result, 0, head, 0x14, 16);
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        i=36;
        for (byte b : type)
            head[i++] = b;
        
        return head;
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
