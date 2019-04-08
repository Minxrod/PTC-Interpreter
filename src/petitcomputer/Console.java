package petitcomputer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Arrays;
import petitcomputer.CharacterPTC.Char;

/**
 * The main text console of PTC.
 * @author minxr
 */
class Console implements ComponentPTC {
    VariablesII vars;
    ProcessII.Evaluator eval;
    Input in;
    
    //IN CHARACTER UNITS, NOT PIXELS.
    final int CONSOLE_WIDTH = 32;
    final int CONSOLE_HEIGHT = 24;
    
    BGF font;
    COL colors;
    
    byte[][] characters;
    byte[][] color;
    int currentX, currentY;
    int currentColor;
    
    public Console(BGF fontSet, COL colorSet, ProcessII.Evaluator ev){
        eval = ev;
        font = fontSet;
        colors = colorSet;
        
        //used to just init arrays
        cls();
        
        currentX = 0;
        currentY = 0;
        currentColor = 0;
        //print(new StringPTC("This is text. Can you tell? Wow!:D amaze []\\;',./"));
    }
    
    public void setIn(Input input){
        in = input;
    }
    
    public void init(){
        print(new StringPTC("PetitInterpreter ver0.2"));
        
        print(new StringPTC("SMILEBASIC " + Runtime.getRuntime().freeMemory() + " bytes free"));
        print(new StringPTC("(C)2011-2012 SmileBoom Co.Ltd."));
        print(new StringPTC(""));
        print(new StringPTC("READY"));
    }
    
    public void locate(NumberPTC x, NumberPTC y){
        locate(x.getIntNumber(), y.getIntNumber());
    }
    
    /**
     * Sets the current cursor position.
     * @param newX
     * @param newY 
     */
    public void locate(int newX, int newY){
        currentX = newX;
        currentY = newY;
    }
    
    
    /**
     * Sets console color.
     */
    public void color(NumberPTC color){
        color((byte) color.getIntNumber());
    }
    
    /**
     * Sets the console color.
     * @param color 
     */
    public void color(byte color){
        currentColor = color;
    }
    
    /**
     * Print function designed to take whatever argument was given and convert to the correct format (or return an error)
     * @param variable 
     */
    public int print(VariablePTC variable){
        int error = 0;
        System.out.println("PRINT branch:" + variable.toString());
        
        //if (variable == null)
            //return error;
        
        int type = variable.getType();
        switch (type) {
            case VariablePTC.NUMBER_LITERAL:
                print(((NumberPTC)variable).toStringPTC());
                break;
            case VariablePTC.STRING_LITERAL:
                print((StringPTC)variable); //ez StringPTC to StringPTC
                break;
            default:
                System.out.println(variable.toString() + "type: " + variable.getType());
                break;
        }
        
        return error;
    }
    
    /*
     * OLD VERSTION OF INPUT COMMAND: USED A SCANNER FOR TESTING AND I WAS LAZY. IT HAS SINCE BEEN REPLACED, NEVER USE THIS PLEASE.
     * Gets keyboard input and stores it to the given variable.
     * @param text
     * @param variable
     * @return 
     */
    /*public void input(StringPTC text, StringPTC variable){
        print(text);
        
        int x = currentX;
        int y = currentY; //save location for display
        StringPTC result = new StringPTC(0);
        while (in.button().getIntNumber() != 16 ||
             !(in.keyboard().getIntNumber() != 15)){
            locate(x, y);
            print(result);
            Debug.print(Debug.INPUT_FLAG, "key2int: " + in.keyboard().getIntNumber());
            if (in.keyboard().getIntNumber() != 0)
                result.add(in.inkey());
        }
        
        System.out.println("INPUT var to store NAME: " + variable.toString());
        if (vars.getVariable(variable).getType() == VariablePTC.NUMBER_LITERAL)
            vars.setVariable(variable, result.getNumberFromString());
        else //it's a string.
            vars.setVariable(variable, result);
    }*/
    
    /**
     * Command to receive text input from the user and store it to the given variable. Cannot type more than one character per key press (no key-repeat).
     * @param text
     * @param variable 
     */
    public void input(StringPTC text, ArrayList<VariablePTC> variable){
         StringPTC msg = text.getSubstring(0, text.getLength());
         msg.add(Char.QUESTION.getStringPTC());
         msg.setLine(false);
         print(msg);
        
        int x = currentX; //input start location.
        int y = currentY; //save location for display
        int c = 0;
        StringPTC result = new StringPTC(0);
        result.setLine(false); //no newline after print
        while (in.getButtons() != 16 &&
               in.keyboard() != 60){
            locate(x, y);
            print(result);
            in.waitForInput();
            if (in.keyboard() == Input.BACKSPACE){
                result = result.getSubstring(0, result.getLength() - 1); //this removes the last character of result
                result.add(Char.SPACE.getStringPTC()); //adds a space instead, to clear the last character when printed
                locate(x, y); //resets location
                print(result); //this clears the last character typed.
                result = result.getSubstring(0, result.getLength() - 1); //this code then removes the added space
                if (c > 0)
                    c--;
                in.waitForReset(); //wait for character to be released to prevent RAPID-FIRE TYPING!
            } else if (in.keyboard() != 0 && in.keyboard() != 60){
                result.setCharacter(c, in.inkey().getCharacter(0));
                if (c < 256)
                    c++; //I thought this was Java? /*please forgive me*/
                in.waitForReset(); //same as before...
            }
        }
        in.waitForReset();
        print(Char.NULL.getStringPTC()); //don't remember what the goal was with this, leaving it anyways
        
        System.out.println("INPUT var to store NAME: " + variable.toString());
        if (vars.getVariable(variable).getType() == VariablePTC.NUMBER_LITERAL)
            vars.setVariable(variable, result.getNumberFromString());
        else //it's a string.
            vars.setVariable(variable, result);
    }
    
    /**
     * Adds characters to the console.
     */
    public void print(StringPTC text){
        for (int i = 0; i < text.getLength(); i++){
            characters[currentY][currentX] = text.getCharacter(i);
            color[currentY][currentX] = (byte) currentColor;
            advanceCursor();
        }
        if (text.getTab())
            do 
                advanceCursor();
                while (currentX % 4 != 0);//whatever the tab calculator is
        if (text.getLine())
            newLine();
    }
    
    private void advanceCursor(){
        currentX++;
        if (currentX > 31){
            newLine();
        }
        scroll();
    }
    
    private void retreatCursor(){
        currentX--;
        if (currentX < 0){
            currentX = 31;
            currentY--;
        }
    }
    
    private void newLine(){
        currentX = 0;
        currentY++;
        scroll();
        
    }
    
    private void scroll(){
        if (currentY > 23){
            //scroll all data up
            for (int i = 0; i < CONSOLE_HEIGHT-1; i++){
                for (int j = 0; j < CONSOLE_WIDTH; j++){
                    characters[i][j] = characters[i+1][j];
                    color[i][j] = color[i+1][j];
                }
            }
            characters[CONSOLE_HEIGHT-1] = new byte[CONSOLE_WIDTH]; //zero out bottom row
            color[CONSOLE_HEIGHT-1] = new byte[CONSOLE_WIDTH];//similar, but fill with current color after
            Arrays.fill(color[CONSOLE_HEIGHT-1], (byte)currentColor);
            currentY = CONSOLE_HEIGHT-1;
        }
    }
    
    public NumberPTC chkchr(NumberPTC x, NumberPTC y){
        return new NumberPTC(Byte.toUnsignedInt(characters[y.getIntNumber()][x.getIntNumber()]));
    }
    
    /**
     * Clears the console of all characters.
     */
    public void cls(){
        characters = new byte[CONSOLE_HEIGHT][CONSOLE_WIDTH];
        color = new byte[CONSOLE_HEIGHT][CONSOLE_WIDTH];
        
        for (int y = 0; y < CONSOLE_HEIGHT; y++){
            for (int x = 0; x < CONSOLE_WIDTH; x++){
                characters[y][x] = 0;
                color[y][x] = 0;
            }
        }
    
        currentX = 0;
        currentY = 0;
    }
    
    public void setVariables(VariablesII v){
        vars = v;
    }
    
    /**
     * Execute an action based off of a (StringPTC format) text command.
     */
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> arguments){
        System.out.println("ACT CONSOLE: " + command.toString());
        switch (command.toString().toLowerCase()){
            case "print":
                for (ArrayList argument : arguments) {
                    ArrayList<VariablePTC> newArg = eval.evaluate(argument);
                    for (VariablePTC smallArg : newArg)
                        print(smallArg); //print args as necessary.
                }
                break;
            case "cls":
                cls();
                break;
            case "locate":
                if (arguments.size() != 2)
                    return Errors.MISSING_OPERAND;//Errors.MISSING_OPERAND;
                NumberPTC x = (NumberPTC) eval.eval(arguments.get(0));
                NumberPTC y = (NumberPTC) eval.eval(arguments.get(1));
                
                locate(x, y);
                break;
            case "color":
                NumberPTC col = (NumberPTC) eval.eval(arguments.get(0));
                color(col);
                break;
            case "input":
                //System.out.println(arguments.toString());
                StringPTC text = (StringPTC) arguments.get(0).get(0);
                arguments.get(0).remove(0); //remove string
                arguments.get(0).remove(0); //remove semicolon
                ArrayList var = arguments.get(0); //get first element of second argument. (Should only be one element, but just in case it will ignore everything after it. Because of this, it breaks arrays.)
                
                input(text, var);
                break;
            default:
                System.err.println("Conolse ACT error: " + command.toString());
                break;
        }
        
        return null;
    }
    
    
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args){
        switch (function.toString().toLowerCase()){
            case "chkchr$":
                NumberPTC x = (NumberPTC) args.get(0); //skip comma
                NumberPTC y = (NumberPTC) args.get(2);
                
                return chkchr(x, y);
            default:
                return null;
        }
    }
    
    /**
     * Creates a visual image of the console.
     */
    public Image createImage(){
        BufferedImage image = new BufferedImage(CONSOLE_WIDTH * 8, CONSOLE_HEIGHT * 8, BufferedImage.TYPE_INT_ARGB);//BufferedImage.TYPE_BYTE_INDEXED, colors.getICM());
        Graphics drawToImage = image.createGraphics();
        
        drawToImage.setColor(new Color(0,0,0,0));
        drawToImage.fillRect(0, 0, CONSOLE_WIDTH * 8, CONSOLE_HEIGHT * 8);
        for (int y = 0; y < CONSOLE_HEIGHT; y++){
            for (int x = 0; x < CONSOLE_WIDTH; x++){
                //System.out.println(characters[x][y]);
                int character = Byte.toUnsignedInt(characters[y][x]);
                drawToImage.drawImage(font.getImage(character, color[y][x]), 8 * x, 8 * y, null);
            }
        }
        /*
        for (int i = 0; i < 24; i++)
            System.out.println(Arrays.toString(characters[i]));
        */
        return image;
    }
}
