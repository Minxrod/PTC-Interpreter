package petitcomputer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import petitcomputer.CharacterPTC.Char;
import petitcomputer.VirtualDevice.Evaluator;

/**
 * The main text console of PTC.
 * @author minxr
 */
class Console implements ComponentPTC {
    VariablesII vars;
    Evaluator eval;
    Input in;
    
    //IN CHARACTER UNITS, NOT PIXELS.
    final int CONSOLE_WIDTH = 32;
    final int CONSOLE_HEIGHT = 24;
    
    CharsetPTC font;
    COL colors;
    
    //BufferedImage image;
    //Graphics g;
    byte[][] characters;
    byte[][] color;
    int currentX, currentY;
    int currentColor;
    
    public Console(CharsetPTC fontSet, COL colorSet, Evaluator ev){
        eval = ev;
        font = fontSet;
        colors = colorSet;
        
        characters = new byte[CONSOLE_HEIGHT][CONSOLE_WIDTH];
        color = new byte[CONSOLE_HEIGHT][CONSOLE_WIDTH];
        
        //image = new BufferedImage(PetitComGUI.WINDOW_WIDTH, PetitComGUI.WINDOW_HEIGHT, BufferedImage.TYPE_BYTE_INDEXED, colors.getICM256());
        //g = image.createGraphics();
        
        for (int y = 0; y < CONSOLE_HEIGHT; y++){
            for (int x = 0; x < CONSOLE_WIDTH; x++){
                characters[y][x] = 0;
                color[y][x] = 0;
            }
        }
        
        currentX = 0;
        currentY = 0;
        currentColor = 0;
    }
    
    public void setIn(Input input){
        in = input;
    }
    
    public void init(){
        print(new StringPTC("PetitInterpreter ver0.3"));
        
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
     * Prints the given arguments to the emulated console.
     * @param args 
     */
    public void print(ArrayList<ArrayList> args){
        if (args.isEmpty()){
            newLine();
            return;
        }
        //Debug.print(Debug.CONSOLE_FLAG, "BEGIN PRINT BRANCH  {");
        for (int i = 0; i < args.size(); i++){ //each arg is separated by a comma ...
            ArrayList<VariablePTC> newArg = eval.evaluate(args.get(i));
            for (VariablePTC smallArg : newArg){
                write(smallArg); //print args as necessary.
            }
            if (i < args.size() - 1) //implies a comma follows the argument
                tab();
        }
        
        ArrayList<VariablePTC> lastArg = args.get(args.size() - 1);
        //check for ending comma
        if (lastArg.isEmpty())
            tab();
        //check for ending semicolon. if not present, newline as usual
        else if (!lastArg.get(lastArg.size() - 1).equals(Char.SEMICOLON.getStringPTC())){
            newLine();
            //Debug.print(Debug.CONSOLE_FLAG, "NEWLINE");        
        }
        
        //Debug.print(Debug.CONSOLE_FLAG, "}  END PRINT BRANCH");
    }
    
    /**
     * Print function designed to take whatever argument was given
     * and take the correct action.
     * @param variable 
     */
    private void write(VariablePTC variable){
        //Debug.print(Debug.CONSOLE_FLAG, "  PRINT:" + variable.toString() + Arrays.toString(variable.toStringPTC().getString()));
        
        if (variable.getType() == VariablePTC.STRING_OPERATOR //semicolon separator (concat)
              && variable.equals(CharacterPTC.Char.SEMICOLON.getStringPTC()))
            ; //semicolons are ignored; only for separation of variables.
        else
            print(variable);
    }
    
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
     * NOTE: old method.
     */
    public void print(StringPTC text){
        printNoModifier(text);
        if (text.getTab())
            do 
                advanceCursor();
                while (currentX % 4 != 0);//whatever the tab calculator is
        if (text.getLine())
            newLine();
    }
    
    /**
     * Prints a variable to the emulated console. Will not print a line break
     * at the end of the line.
     * @param var 
     */
    private void print(VariablePTC var){
        StringPTC text = var.toStringPTC();
        
        printNoModifier(text);
    }
    
    /**
     * Print command to print some text without modifying the final cursor position.
     * @param var 
     */
    private void printNoModifier(VariablePTC var){
        StringPTC text = var.toStringPTC();
        for (int i = 0; i < text.getLength(); i++){
            characters[currentY][currentX] = text.getCharacter(i);
            
            color[currentY][currentX] = (byte) currentColor;
            //g.drawImage(font.getImage(Byte.toUnsignedInt(text.getCharacter(i)), (byte) currentColor), currentX * 8, currentY * 8, null);
            if ((currentX == CONSOLE_WIDTH - 1 && currentY == CONSOLE_HEIGHT - 1 && i == text.getLength() - 1))
                ; //don't advance cursor if a semicolon was encountered at the end AND you are at the edge of the console already.
            else
                advanceCursor();
        }
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
    
    private void tab(){
        do {
            advanceCursor();
        } while (currentX % 4 != 0);//whatever the tab calculator is
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
            
            //scroll by copying lower part of image to new image, and making that the image saved.
            /*BufferedImage shiftImage = new BufferedImage(PetitComGUI.WINDOW_WIDTH, PetitComGUI.WINDOW_HEIGHT, BufferedImage.TYPE_BYTE_INDEXED, colors.getICM256());
            Raster r = image.getData(new Rectangle(0, 8, PetitComGUI.WINDOW_WIDTH, PetitComGUI.WINDOW_HEIGHT - 8));
            shiftImage.setData(r); //copy old image to new image
            System.out.println(shiftImage.toString());
            //g = shiftImage.createGraphics();
            //g.clearRect(0, PetitComGUI.WINDOW_HEIGHT - 8, PetitComGUI.WINDOW_WIDTH, 8);
            
            image = shiftImage;*/
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
        currentColor = 0;
        
        //OLD://creates (or recreates) an image for the console to draw to.
        //image = new BufferedImage(PetitComGUI.WINDOW_WIDTH, PetitComGUI.WINDOW_HEIGHT, BufferedImage.TYPE_BYTE_INDEXED, colors.getICM256());
        //g = image.createGraphics();
    }
    
    public void setVariables(VariablesII v){
        vars = v;
    }
    
    public void setSystemVariables(){
        vars.setVariable(VariablesII.SYSTEM_VARIABLES[VariablesII.CSRX], new NumberPTC(currentX));
        vars.setVariable(VariablesII.SYSTEM_VARIABLES[VariablesII.CSRY], new NumberPTC(currentY));
    }
    
    /**
     * Execute an action based off of a (StringPTC format) text command.
     */
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> arguments){
        Debug.print(Debug.ACT_FLAG, "ACT CONSOLE: " + command.toString() + "ARGS:" + arguments.toString());
        switch (command.toString().toLowerCase()){
            case "print":
                print(arguments);
                setSystemVariables();
                break;
            case "cls":
                cls();
                setSystemVariables();
                break;
            case "locate":
                if (arguments.size() != 2)
                    return Errors.MISSING_OPERAND;//Errors.MISSING_OPERAND;
                NumberPTC x = (NumberPTC) eval.eval(arguments.get(0));
                NumberPTC y = (NumberPTC) eval.eval(arguments.get(1));
                
                locate(x, y);
                setSystemVariables();
                break;
            case "color":
                NumberPTC col = (NumberPTC) eval.eval(arguments.get(0));
                color(col);
                break;
            case "input":
                if (arguments.get(0).size() > 1){
                    //System.out.println(arguments.toString());
                    StringPTC text = (StringPTC) arguments.get(0).get(0);
                    arguments.get(0).remove(0); //remove string
                    arguments.get(0).remove(0); //remove semicolon
                    ArrayList var = arguments.get(0); //get first element of second argument. 
                
                    input(text, var);
                } else {
                    StringPTC text = new StringPTC("");
                    
                    ArrayList var = arguments.get(0);
                    
                    input(text, var);
                }
                setSystemVariables();
                break;
            default:
                Debug.print(Debug.ACT_FLAG, "Console ACT error: " + command.toString());
                break;
        }
        
        return null;
    }
    
    
    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args){
        switch (function.toString().toLowerCase()){
            case "chkchr$":
                NumberPTC x = (NumberPTC) args.get(0);
                NumberPTC y = (NumberPTC) args.get(1);
                
                return chkchr(x, y);
            default:
                return null;
        }
    }
    
    /**
     * Creates a visual image of the console.
     */
    public Image createImage(){
        BufferedImage image = new BufferedImage(CONSOLE_WIDTH * 8, CONSOLE_HEIGHT * 8, BufferedImage.TYPE_INT_ARGB);//BufferedImage.TYPE_BYTE_INDEXED, colors.getICM256());
        Graphics drawToImage = image.createGraphics();
        
        //drawToImage.setColor(new Color(0,0,0,0));
        //drawToImage.fillRect(0, 0, CONSOLE_WIDTH * 8, CONSOLE_HEIGHT * 8);
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
    
    /**
     * Method to return an image of the console screen in it's current state.
     * @return 
     */
    public Image getImage(){
        return createImage();
    }
}
