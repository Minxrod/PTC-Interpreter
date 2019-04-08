/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petitcomputer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author minxr
 */
public class Graphic implements ComponentPTC {
    ProcessII.Evaluator eval;
    GRPLayer[] layer;
    int displayLayer[];
    int drawLayer[];
    int gpage;
    
    public Graphic(ProcessII.Evaluator ev, COL col){
        layer = new GRPLayer[4];
        for (int i = 0; i < layer.length; i++)
            layer[i] = new GRPLayer(col);
        displayLayer = new int[2]; //number of screens is always 2.
        drawLayer = new int[2];
        displayLayer[0] = 0;
        drawLayer[0] = 0;
        displayLayer[1] = 1;
        drawLayer[1] = 1;
        gpage = 0;
        eval = ev;
    }

    /**
     * Returns the currently displayed graphics layer for the required page. 
     * 
     * Note:drawLayer[gpage] is the currently set drawing layer.
     * @param page
     * @return 
     */
    public BufferedImage createImage(int page){
        return layer[displayLayer[page]].getImage();
    }
    
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> args) {
        Debug.print(Debug.ACT_FLAG, "ACT branch GRAPHIC: " + command + " ARGS: " + args);
        NumberPTC x, y, x2, y2, c;
        switch (command.toString().toLowerCase()){
            case "gpage":
                x = (NumberPTC) eval.eval(args.get(0)); //screen to use: 0 = upper, 1 = lower
                gpage = x.getIntNumber(); //screen is always required
                if (args.size() > 1){ //optional args
                    x2 = (NumberPTC) eval.eval(args.get(1)); //draw page
                    y2 = (NumberPTC) eval.eval(args.get(2)); //display page
                    //since gpage is already set, can use to determine draw pages and display pages
                    displayLayer[gpage] = x2.getIntNumber();
                    displayLayer[gpage] = y2.getIntNumber();
                }
                break;
            case "gcls":
                layer[drawLayer[gpage]].gcls();
                break;
            case "gcolor":
                c = (NumberPTC) eval.eval(args.get(0));
                
                layer[drawLayer[gpage]].gcolor(c.getIntNumber());
                break;
            case "gpset":
                x = (NumberPTC) eval.eval(args.get(0));
                y = (NumberPTC) eval.eval(args.get(1));
                if (args.size() > 2){
                    c = (NumberPTC) eval.eval(args.get(2));
                    layer[drawLayer[gpage]].gpset(x.getIntNumber(), y.getIntNumber(), c.getIntNumber());
                } else {
                    layer[drawLayer[gpage]].gpset(x.getIntNumber(), y.getIntNumber());
                }
                break;
            case "gline":
                x = (NumberPTC) eval.eval(args.get(0));
                y = (NumberPTC) eval.eval(args.get(1));
                x2 = (NumberPTC) eval.eval(args.get(2));
                y2 = (NumberPTC) eval.eval(args.get(3));
                if (args.size() > 4){
                    c = (NumberPTC) eval.eval(args.get(4));
                    layer[drawLayer[gpage]].gline(x.getIntNumber(), y.getIntNumber(), x2.getIntNumber(), y2.getIntNumber(), c.getIntNumber());
                } else {
                    layer[drawLayer[gpage]].gline(x.getIntNumber(), y.getIntNumber(), x2.getIntNumber(), y2.getIntNumber());
                }
                break;
            case "gfill":
                x = (NumberPTC) eval.eval(args.get(0));
                y = (NumberPTC) eval.eval(args.get(1));
                x2 = (NumberPTC) eval.eval(args.get(2));
                y2 = (NumberPTC) eval.eval(args.get(3));
                if (args.size() > 4){
                    c = (NumberPTC) eval.eval(args.get(4));
                    layer[drawLayer[gpage]].gfill(x.getIntNumber(), y.getIntNumber(), x2.getIntNumber(), y2.getIntNumber(), c.getIntNumber());
                } else {
                    layer[drawLayer[gpage]].gfill(x.getIntNumber(), y.getIntNumber(), x2.getIntNumber(), y2.getIntNumber());
                }
                break;
            case "gbox":
                x = (NumberPTC) eval.eval(args.get(0));
                y = (NumberPTC) eval.eval(args.get(1));
                x2 = (NumberPTC) eval.eval(args.get(2));
                y2 = (NumberPTC) eval.eval(args.get(3));
                if (args.size() > 4){
                    c = (NumberPTC) eval.eval(args.get(4));
                    layer[drawLayer[gpage]].gbox(x.getIntNumber(), y.getIntNumber(), x2.getIntNumber(), y2.getIntNumber(), c.getIntNumber());
                } else {
                    layer[drawLayer[gpage]].gbox(x.getIntNumber(), y.getIntNumber(), x2.getIntNumber(), y2.getIntNumber());
                }
                break;
            default:
                Debug.print(Debug.ACT_FLAG, "GRAPHIC branch ERROR: " + command.toString());
                
        }
        return null;
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
