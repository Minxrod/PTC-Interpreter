package petitcomputer;

import java.util.ArrayList;
import petitcomputer.VirtualDevice.Evaluator;

/**
 * Class to store all sorts of program graphics resources.
 * @author minxr
 */
public class Resources implements ComponentPTC {
    public static final String PATH = "src/resource/";
    
    Files files;
    Evaluator eval;
    VariablesII vars;
    
    BGF bgfu, bgfl;
    BGU bguu, bgul;
    BGD bgd; //only use is on bottom screen
    COL col0, col1, col2; //BG+FONT, SP, GRP
    
    public Resources(Files f, VariablesII v, Evaluator e){
        files = f;
        eval = e;
        vars = v;
        
        col0 = new COL(false);
        col0.setData(f.loadColor(PATH + "COL0.PTC"));
        col0.createICM();
        
        col1 = new COL(false);
        col1.setData(f.loadColor(PATH + "COL1.PTC"));
        col1.createICM();
        
        col2 = new COL(true);
        col2.setData(f.loadColor(PATH + "COL2.PTC"));
        col2.createICM();
        
        bgfu = new BGF(col0);
        bgfu.setData(f.loadCHRBank(PATH + "BGF0.PTC", true));
        //bgfu.setDefault();
        
        bgfl = new BGF(col0);
        bgfl.setData(f.loadCHRBank(PATH + "BGF0.PTC", true));
        //bgfl.setDefault();
        
        bguu = new BGU(col0);
        
        bguu.setData(0, f.loadCHRBank(PATH + "BGU0.PTC", true));
        bguu.setData(1, f.loadCHRBank(PATH + "BGU1.PTC", true));
        bguu.setData(2, f.loadCHRBank(PATH + "BGU2.PTC", true));
        bguu.setData(3, f.loadCHRBank(PATH + "BGU3.PTC", true));
        //bguu.setDefault();
        
        bgd = new BGD(col0);
        bgd.setData(0, f.loadCHRBank(PATH + "BGD0.PTC", true));
        bgd.setData(1, f.loadCHRBank(PATH + "BGD1.PTC", true));
        //bgd.setDefault();
    }
    
    /**
     * Returns the upper screen font character data.
     * @return 
     */
    public BGF getBGFU(){
        return bgfu;
    }
    
    public BGF getBGFL(){
        return bgfl;
    }
    
    public BGU getBGUU(){
        return bguu;
    }
    
    public BGU getBGUL(){
        return bgul;
    }
    
    public BGD getBGD(){
        return bgd;
    }
    
    public COL getCOL0(){
        return col0;
    }
    
    public COL getCOL2(){
        return col2;
    }
    
    /**
     * Sets data for a given resource.
     * Useful for file loading.
     * @param rString resource type string
     * @param data data to be stored
     */
    public void setResource(String rString, byte[] data){
        rString = rString.toLowerCase();
        switch (rString.substring(0, 3)){
            case "bgf":
                if (isLower(rString)) //check if lower screen data
                    bgfl.setData(data);
                else //default to upper
                    bgfu.setData(data);
                break;
        }
    }
    
    /**
     * Checks if the last characters of the string contains a "l" or not.
     * @param s
     * @return 
     */
    private boolean isLower(String s){
        return s.substring(3).contains("l");
    }
    
    /**
     * Loads data using a files object and stores it to the correct object.
     * @param type
     * @param name 
     */
    public void load(String type, String name){
        type = type.toLowerCase();
        name = name.toUpperCase();
        
        switch (type.substring(0, 3)){
            case "prg":
                //PROGRAM OBJECT LOAD
                break;
            case "bgf":
                if (type.endsWith("l")){
                    bgfl.setData(files.loadCHRBank(name + ".PTC", false));
                } else {
                    bgfu.setData(files.loadCHRBank(name + ".PTC", false));
                }
                break;
            case "mem":
                StringPTC mem = files.readMEM(files.loadMEM(name + ".PTC"));
                
                vars.setVariable(VariablesII.SYSTEM_VARIABLES[VariablesII.MEM], mem);
                break;
        }
    }
    
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> args) {
        switch (command.toString().toLowerCase()){
            case "load":
                String resource = ((StringPTC) eval.eval(args.get(0))).toString();
                String type;
                String name;
                if (resource.contains(":")){
                    type = resource.substring(0, resource.indexOf(":")); //TYPE:FILENAME -> TYPE
                    name = resource.substring(resource.indexOf(":")+1, resource.length()); // TYPE:FILENAME -> FILENAME
                } else {
                    type = "prg";
                    name = resource;
                }
                //type and name are set. Now load using given type + name
                load(type, name);
                break;
        }
        return null;
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
