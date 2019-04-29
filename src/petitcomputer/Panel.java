package petitcomputer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import petitcomputer.VirtualDevice.Evaluator;

/**
 * Lower screen keyboard, text, and panel graphics
 * @author minxr
 */
public class Panel implements ComponentPTC {
    Evaluator eval;
    Console console;
    BGLayer back;
    StringPTC pnltype;
    //some BG element
    
    public Panel(BGF bgf, COL col, BGD bgd, Evaluator ev){
        eval = ev;
        console = new Console(bgf, col, ev);
        back = new BGLayer(bgd, col);
        
        drawBGD();
        
        pnltype = new StringPTC("kya");
    }
    
    public void pnlstr(StringPTC text, NumberPTC x, NumberPTC y, NumberPTC col){
        console.locate(x, y);
        console.color(col);
        text.setLine(false);
        console.print(text);
    }    
    
    public void pnltype(StringPTC type){
        pnltype = type;
        //add redraw of bgd stuff
    }
    
    /**
     * Initializes the panel for keyboard usage. I brute-forced this manually because it seemed easier at the time. Don't question it.
     */
    private void drawBGD(){
        
        /*int[] keyX = new int[]{ 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27,
                            0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30,
                                3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29,
                                 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28,   };
        */
        
        //DEFAULT TILE
        back.bgfill(0, 0, 31, 23, 15);
        //DEFAULT GREEN BAR
        back.bgfill(0, 20, 31, 23, 6);
        
        //5-KEY ROWON TOP
        int[] dat = new int[]{219, 220, 220, 220, 220, 221};
        for (int x = 0; x < 30; x+=6){
            for (int subX = 0; subX < 6; subX++){
                back.bgput(x + subX, 0, dat[subX]);
                back.bgput(x + subX, 1, dat[subX] + 32);
            }
        }
        
        
        //KEYBOARD <fragile, don'ttouch> (form of pos[] is {xStart, xEnd, yPos}
        for (int[] pos : new int[][]{{3, 27, 6}, {0, 30, 9}, {3, 29, 12}, {4, 26, 15}}){
            for (int x = pos[0]; x <= pos[1]; x+=2){
                back.bgput(x, pos[2], 187);
                back.bgput(x + 1, pos[2], 189);
                back.bgput(x, pos[2] + 1, 187 + 32);
                back.bgput(x + 1, pos[2] + 1, 189 + 32);
                back.bgput(x, pos[2] + 2, 187 + 64);
                back.bgput(x + 1, pos[2] + 2, 189 + 64);
            }
        }   
        //KEYBOARD SELECT BUTTONS
        for (int x : new int[]{0, 3, 5, 7}){
            back.bgput(x, 18, 24);
            back.bgput(x + 1, 18, 25);
            back.bgput(x, 19, 56); //24+32
            back.bgput(x + 1, 19, 57); //25+32
        }
        
        //WHITE STRIP ABOVE GREEN BAR
        for (int x = 0; x < 32; x++){
            back.bgput(x, 20, 36);
        }        
        
        //UP&DOWN NEXT TO ICONS
        back.bgput(18, 21, 190);
        back.bgput(19, 21, 191);
        back.bgput(18, 22, 190+32);
        back.bgput(19, 22, 191+32);
        back.bgput(18, 23, 190+64);
        back.bgput(19, 23, 191+64);
        
        //4 CHANGEABLE ICONS WITHIN GREEN BAR
        for (int x = 20; x < 32; x+=3)
            for (int subX = 0; subX < 3; subX++)
                for (int subY = 0; subY < 3; subY++)
                    back.bgput(x + subX, 21 + subY, 69 + subX + 32 * subY);
        
        //HELP BUTTTON
        for (int x = 0; x < 4; x++){
            for (int y = 0; y < 3; y++)
                back.bgput(x, y + 21, 180 + x + 32 * y);
        }
        
        //"EDIT" && "STOP" BUTTONS
        dat = new int[]{166, 167, 167, 167, 167, 168};
        for (int x = 0; x < 6; x++){
            for (int y = 0; y < 3; y++){
                back.bgput(5 + x, 21 + y, dat[x] + 32 * y);
                back.bgput(5 + x + 6, 21 + y, dat[x] + 32 * y);
            }
        }
    }
    
    public Image createImage() {
        Image mainImage = new BufferedImage(256, 192, BufferedImage.TYPE_USHORT_555_RGB);
        Graphics main = mainImage.getGraphics();
        
        switch (pnltype.toString().toLowerCase()){
            case "pnl":
                main.drawImage(back.createImage(), 0, 0, null);
            case "off":
                main.drawImage(console.getImage(), 0, 0, null);
                break;
            case "kya":
            case "kym":
            case "kyk":
                main.drawImage(back.createImage(), 0, 0, null);
                break;
            
        }
        
        return mainImage;
    }
    
    /**
     * Clears the panel.
     */
    public void clear(){
        console.cls();
        back.bgclr();
    }
    
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> args) {
        Debug.print(Debug.ACT_FLAG, "PANEL act : " + command.toString() + "ARGS: " + args.toString());
        switch (command.toString().toLowerCase()){
            case "pnltype":
                StringPTC type = (StringPTC) eval.eval(args.get(0));
                
                pnltype(type);
                break;
            case "pnlstr":
                NumberPTC x = (NumberPTC) eval.eval(args.get(0));
                NumberPTC y = (NumberPTC) eval.eval(args.get(1));
                StringPTC s = (StringPTC) eval.eval(args.get(2));
                NumberPTC c = new NumberPTC(0);
                if (args.size() == 3)
                    c = (NumberPTC) eval.eval(args.get(3));
                
                pnlstr(s, x, y, c);
                break;
            default:
                System.out.println("PANEL ERROR: " + command.toString());
        }
        
        return null;
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        throw new UnsupportedOperationException("Unsupported operation. " + getClass().getName() + " does not support this function.");
    }

}
