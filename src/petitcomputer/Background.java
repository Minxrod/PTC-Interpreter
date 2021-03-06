package petitcomputer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import petitcomputer.VirtualDevice.Evaluator;

/**
 *
 * @author minxr
 */
public class Background implements ComponentPTC{
    CharsetPTC bgu;
    COL colors;
    BGLayer[][] layer;
    Evaluator eval;
    
    int bgpage, bglayer;
    
    /**
     * Initializes a Background object with the given character set, palette, and evaluator object.
     * @param bg - background tileset
     * @param col - color palette object
     * @param ev - evaluator
     */
    public Background(CharsetPTC bg, COL col, Evaluator ev){
        bgu = bg;
        colors = col;
        eval = ev;
        
        layer = new BGLayer[2][2]; //[screen][layer]
        
        layer[0][0] = new BGLayer(bg, col);
        layer[0][1] = new BGLayer(bg, col);
        layer[1][0] = new BGLayer(bg, col);
        layer[1][1] = new BGLayer(bg, col);
    }
    
    public Image createImage(int screen) {
        BufferedImage image = new BufferedImage(256, 192, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        
        g.drawImage(layer[screen][1].getImage(), 0, 0, null);
        g.drawImage(layer[screen][0].getImage(), 0, 0, null);
        
        return image;
    }

    public void clear(){
        for (BGLayer[] screen: layer)
            for (BGLayer l: screen)
                l.bgclr();
    }
    
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> args) {
        Debug.print(Debug.ACT_FLAG, "Background ACT branch: " + command.toString());
        
        NumberPTC l, x, y, x2, y2, data;
        NumberPTC pal, v, h;
        switch (command.toString().toLowerCase()){
            case "bgput":
                l = (NumberPTC) eval.eval(args.get(0));
                x = (NumberPTC) eval.eval(args.get(1));
                y = (NumberPTC) eval.eval(args.get(2));
                data = (NumberPTC) eval.eval(args.get(3));
                if (args.size() > 4){
                    pal = (NumberPTC) eval.eval(args.get(4));
                    h = (NumberPTC) eval.eval(args.get(5));
                    v = (NumberPTC) eval.eval(args.get(6));
                    
                    layer[bgpage][l.getIntNumber()].bgput(x, y, data, pal, h, v);
                } else
                    layer[bgpage][l.getIntNumber()].bgput(x, y, data);
                break;
            case "bgfill":
                l = (NumberPTC) eval.eval(args.get(0));
                x = (NumberPTC) eval.eval(args.get(1));
                y = (NumberPTC) eval.eval(args.get(2));
                x2= (NumberPTC) eval.eval(args.get(3));
                y2= (NumberPTC) eval.eval(args.get(4));
                data = (NumberPTC) eval.eval(args.get(5));
                
                layer[bgpage][l.getIntNumber()].bgfill(x, y, x2, y2, data);
                break;
            case "bgclr":
                if (args.isEmpty()){
                    layer[bgpage][0].bgclr();
                    layer[bgpage][1].bgclr();
                } else {
                    l = (NumberPTC) eval.eval(args.get(0));
                    
                    layer[bgpage][l.getIntNumber()].bgclr();
                }
                break;
            case "bgpage":
                data = (NumberPTC) eval.eval(args.get(0));
                
                bgpage = data.getIntNumber();
                break;
            case "bgofs":
                l = (NumberPTC) eval.eval(args.get(0));
                x = (NumberPTC) eval.eval(args.get(1));
                y = (NumberPTC) eval.eval(args.get(2));
                
                layer[bgpage][l.getIntNumber()].bgofs(x, y);
                break;
            default:
                Debug.print(Debug.ACT_FLAG, "Background ERROR: " + command.toString()  +" is unknown.");
        }
        return null;
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        switch (function.toString().toLowerCase()){
            case "bgchk":
                NumberPTC l = (NumberPTC) eval.eval(args);
                
                return layer[bgpage][l.getIntNumber()].bgchk();
            default: 
                return null;
            
        }
    }
    
}
