package petitcomputer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import petitcomputer.VirtualDevice.Evaluator;

/**
 * Sprite manager class. Handles creation and modification of sprites by index.
 * This class mostly handles "global" sprite stuff, like the current page,
 * or sprite interactions, such as SPHITSP. Individual sprite commands act on
 * Sprite objects instead.
 * @author minxr
 */
public class Sprites implements ComponentPTC {
    Evaluator eval;
    COL col;
    CharsetPTC[] chr; //spu and sps
    
    int page;
    Sprite[][] sprites;

    /**
     * Creates a sprite manager, initialized with a character set and palette.
     * @param upper
     * @param lower
     * @param c - color palette
     * @param e - evaluator
     */
    public Sprites(CharsetPTC upper, CharsetPTC lower, COL c, Evaluator e){
        chr = new CharsetPTC[2];
        chr[0] = upper;
        chr[1] = lower;
        col = c;
        eval = e;
        
        page = 0;
        
        sprites = new Sprite[2][100];
    }
    
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> args) {
        Debug.print(Debug.ACT_FLAG, "ACT branch SPRITE: " + command + " ARGS: " + args);
        switch(command.toString().toLowerCase()){
            case "sppage":
                NumberPTC p = (NumberPTC) eval.eval(args.get(0));
                
                page = p.getIntNumber();
                break;
            case "spset":
                //get args
                int spriteID = ((NumberPTC)eval.eval(args.get(0))).getIntNumber();
                NumberPTC cc = (NumberPTC) eval.eval(args.get(1));
                NumberPTC pal = (NumberPTC) eval.eval(args.get(2));
                NumberPTC hr = (NumberPTC) eval.eval(args.get(3));
                NumberPTC vr = (NumberPTC) eval.eval(args.get(4));
                NumberPTC oop = (NumberPTC) eval.eval(args.get(5));
                NumberPTC width;
                NumberPTC height;
                if (args.size() > 6){
                    width = (NumberPTC) eval.eval(args.get(6));
                    height = (NumberPTC) eval.eval(args.get(7));
                } else {
                    width = new NumberPTC(16);
                    height = width; //also 16
                }
                
                //create new sprite object
                sprites[page][spriteID] = new Sprite(cc.getIntNumber(), pal.getIntNumber(), hr.getIntNumber(), vr.getIntNumber(), oop.getIntNumber(),
                                                     width.getIntNumber(), height.getIntNumber());
                sprites[page][spriteID].createImage(chr[page]);
                break;
            case "spclr":
                if (args.isEmpty())
                    sprites = new Sprite[2][100];
                else {
                    spriteID = ((NumberPTC)eval.eval(args.get(0))).getIntNumber();
                    sprites[page][spriteID] = null;
                }
                break;
            case "spofs":
                spriteID = ((NumberPTC)eval.eval(args.get(0))).getIntNumber();
                NumberPTC x = (NumberPTC) eval.eval(args.get(1));
                NumberPTC y = (NumberPTC) eval.eval(args.get(2));
                if (args.size() == 4){
                    NumberPTC time = (NumberPTC) eval.eval(args.get(3));
                    
                    sprites[page][spriteID].spofs(x.getDoubleNumber(), y.getDoubleNumber(), time.getIntNumber());
                } else {
                    sprites[page][spriteID].spofs(x.getDoubleNumber(), y.getDoubleNumber());
                }
                break;
            case "spchr":
                spriteID = ((NumberPTC)eval.eval(args.get(0))).getIntNumber();
                cc = (NumberPTC) eval.eval(args.get(1));
                
                if (args.size() > 2){
                    pal = (NumberPTC) eval.eval(args.get(2));
                    hr = (NumberPTC) eval.eval(args.get(3));
                    vr = (NumberPTC) eval.eval(args.get(4));      
                    oop = (NumberPTC) eval.eval(args.get(5));
                    
                    sprites[page][spriteID].spchr(cc.getIntNumber(), pal.getIntNumber(), hr.getIntNumber(), vr.getIntNumber(), oop.getIntNumber());
                    
                } else {
                    sprites[page][spriteID].spchr(cc.getIntNumber());
                }
                sprites[page][spriteID].createImage(chr[page]);
                break;
            case "spscale":
                spriteID = ((NumberPTC)eval.eval(args.get(0))).getIntNumber();
                NumberPTC scale = (NumberPTC) eval.eval(args.get(1));
                
                if (args.size() == 3) {
                    NumberPTC time = (NumberPTC) eval.eval(args.get(2));
                    
                    sprites[page][spriteID].spscale(scale.getIntNumber(), time.getIntNumber());
                } else {
                    sprites[page][spriteID].spscale(scale.getIntNumber());
                }
                sprites[page][spriteID].createImage(chr[page]);
                break;
            case "spangle":
                spriteID = ((NumberPTC)eval.eval(args.get(0))).getIntNumber();
                NumberPTC angle = (NumberPTC) eval.eval(args.get(1));
                
                if (args.size() == 3) {
                    NumberPTC time = (NumberPTC) eval.eval(args.get(2));
                    
                    sprites[page][spriteID].spangle(angle.getIntNumber(), time.getIntNumber());
                } else {
                    sprites[page][spriteID].spangle(angle.getIntNumber());
                }
                break;                
            case "spanim":
                spriteID = ((NumberPTC)eval.eval(args.get(0))).getIntNumber();
                NumberPTC frames = (NumberPTC) eval.eval(args.get(1));
                NumberPTC time = (NumberPTC) eval.eval(args.get(2));
                NumberPTC loop = (NumberPTC) eval.eval(args.get(3));
                
                sprites[page][spriteID].spanim(frames.getIntNumber(), time.getIntNumber(), loop.getIntNumber());
                //sprites[page][spriteID].createImage(chr);
                break;
            default:
                Debug.print(Debug.ACT_FLAG, "ACT ERROR: " + command);
        }
        return null;
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        Debug.print(Debug.ACT_FLAG, "FUNCTION branch SPRITE: " + function + " ARGS: " + args);
        switch(function.toString().toLowerCase()){
            default:
                Debug.print(Debug.ACT_FLAG, "FUNCTION ERROR: " + function);
        }
        return null;
    }
    
    /**
     * Update all sprites by one frame.
     */
    public void updateSprites(){
        for (int p = 0; p < 2; p++)
            for (Sprite sp : sprites[p])
                if (sp != null && sp.update())
                    sp.createImage(chr[p]);
    }
    
    /**
     * Draws active sprites to the graphics object given.
     * Will only draw sprites on the given screen and priority layer.
     * @param g - image to draw to
     * @param prio - priority of draw
     * @param page - upper screen if 0, lower if 1
     */
    public void drawImage(Graphics2D g, int prio, int page){
        for (int i = 99; i >= 0; i--){
            if (sprites[page][i] != null){
                sprites[page][i].draw(g);
            }
        }
    }
}
