package petitcomputer;

import java.awt.Graphics;
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
    CharsetPTC chr;
    
    int page;
    Sprite[] sprites;

    /**
     * Creates a sprite manager, initialized with a character set and palette.
     * @param chrs - character set
     * @param c - color palette
     * @param e - evaluator
     */
    public Sprites(CharsetPTC chrs, COL c, Evaluator e){
        chr = chrs;
        col = c;
        eval = e;
        
        page = 0;
        
        sprites = new Sprite[200];
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
                
                //create new sprite object
                sprites[page * 100 + spriteID] = new Sprite(cc.getIntNumber(), pal.getIntNumber(), hr.getIntNumber(), vr.getIntNumber(), oop.getIntNumber());
                sprites[page * 100 + spriteID].createImage(chr);
                break;
            case "spclr":
                if (args.isEmpty())
                    sprites = new Sprite[200];
                else {
                    spriteID = ((NumberPTC)eval.eval(args.get(0))).getIntNumber();
                    sprites[page * 100 + spriteID] = null;
                }
                break;
            case "spofs":
                spriteID = ((NumberPTC)eval.eval(args.get(0))).getIntNumber();
                NumberPTC x = (NumberPTC) eval.eval(args.get(1));
                NumberPTC y = (NumberPTC) eval.eval(args.get(2));
                
                sprites[page * 100 + spriteID].spofs(x.getIntNumber(), y.getIntNumber());
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
     * Draws active sprites to the graphics object given.
     * Will only draw sprites on the given screen and priority layer.
     * @param g - image to draw to
     * @param prio - priority of draw
     * @param upper - upper screen if true, lower if false
     */
    public void drawImage(Graphics g, int prio, boolean upper){
        for (int i = 99; i >= 0; i--){
            if (sprites[i] != null){
                sprites[i].draw(g);
            }
        }
    }
}
