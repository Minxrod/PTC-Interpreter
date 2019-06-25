package petitcomputer;

import java.util.ArrayList;
import petitcomputer.VirtualDevice.Evaluator;

/**
 * Class to store all sorts of program graphics resources.
 * @author minxr
 */
public class Resources implements ComponentPTC {
    public static final String PATH = "src/resource/";
    
    private static final int
            BGU_BANKS = 4,
            BGF_BANKS = 1,
            BGD_BANKS = 2,
            SPU_BANKS = 8,
            SPS_BANKS = 2,
            SPD_BANKS = 4;
    
    Files files;
    Evaluator eval;
    VariablesII vars;
    
    CharsetPTC bgfu, bgfl;
    CharsetPTC bguu, bgul;
    CharsetPTC bgd; //only use is on bottom screen
    
    CharsetPTC spu, sps;
    CharsetPTC spd;
    
    COL col0, col1, col2; //BG+FONT, SP, GRP
    
    /**
     * Creates a Resources object. This will initialize all character and color
     * based resources for PTC, which can be accessed by their corresponding
     * methods.
     * @param f
     * @param v
     * @param e 
     */
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
        
        bgfu = new CharsetPTC(col0, BGF_BANKS);
        bgfu.setData(0, f.loadCHRBank(PATH + "BGF0.PTC", true));
        
        bgfl = new CharsetPTC(col0, BGF_BANKS);
        bgfl.setData(0, f.loadCHRBank(PATH + "BGF0.PTC", true));
        
        bguu = new CharsetPTC(col0, BGU_BANKS);
        for (int i = 0; i < BGU_BANKS; i++)
            bguu.setData(i, f.loadCHRBank(PATH + "BGU"+i+".PTC", true));
        
        bgd = new CharsetPTC(col0, BGD_BANKS);
        for (int i = 0; i < BGD_BANKS; i++)
            bgd.setData(i, f.loadCHRBank(PATH + "BGD"+i+".PTC", true));
        
        spu = new CharsetPTC(col1, SPU_BANKS);
        for (int i = 0; i < 8; i++)
            spu.setData(i, f.loadCHRBank(PATH + "SPU"+i+".PTC", true));
        
    }
    
    /**
     * Returns the upper screen font character data.
     * @return 
     */
    public CharsetPTC getBGFU(){
        return bgfu;
    }
    
    public CharsetPTC getBGFL(){
        return bgfl;
    }
    
    public CharsetPTC getBGUU(){
        return bguu;
    }
    
    public CharsetPTC getBGUL(){
        return bgul;
    }
    
    public CharsetPTC getBGD(){
        return bgd;
    }
    
    public CharsetPTC getSPU(){
        return spu;
    }
    
    public COL getCOL0(){
        return col0;
    }
    
    public COL getCOL1(){
        return col1;
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
                    bgfl.setData(0, data);
                else //default to upper
                    bgfu.setData(0, data);
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
        name = name.toUpperCase() + ".PTC";
        
        switch (type.substring(0, 3)){
            case "prg":
                //PROGRAM OBJECT LOAD
                break;
            case "spu":
                spu.setData(type.charAt(3) - '0', files.loadCHRBank(name, false));
                break;
            case "sps":
                sps.setData(type.charAt(3) - '0', files.loadCHRBank(name, false));
                break;
            case "spd":
                spd.setData(type.charAt(3) - '0', files.loadCHRBank(name, false));
                break;
            case "bgu":
                this.loadCharset(name, bguu, bgul, type.endsWith("l"));
                break;
            case "bgf":
                this.loadCharset(name, bgfu, bgfl, type.endsWith("l"));
                break;
            case "bgd":
                bgd.setData(type.charAt(3) - '0', files.loadCHRBank(name, false));
                break;
            case "mem":
                StringPTC mem = files.readMEM(files.loadMEM(name));
                
                vars.setVariable(VariablesII.SYSTEM_VARIABLES[VariablesII.MEM], mem);
                break;
        }
    }
    
    /**
     *  Helper method that will load 
     */
    private void loadCharset(String name, CharsetPTC upper, CharsetPTC lower, boolean isLower){
        if (isLower){
            lower.setData(0, files.loadCHRBank(name, false));
        } else {
            upper.setData(0, files.loadCHRBank(name, false));
        }
    }
    
    public void save(String type, String name){
        type = type.toLowerCase();
        name = name.toUpperCase();
        
        switch (type.substring(0, 3)){
            case "mem":
                StringPTC mem = (StringPTC) vars.getVariable(VariablesII.SYSTEM_VARIABLES[VariablesII.MEM]);
                
                files.saveMEM(name + ".PTC", mem);
                break;
        }
    }
    
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> args) {
        Debug.print(Debug.ACT_FLAG, "RESOURCE act branch " + command.toString() + " args " + args.toString());
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
                /*MY FILES WERE BEING SAVED WITH JUNK BECAUSE THIS WAS RUNNING 
                THROUGH TO THE SAVE FUNCTION WITHOUT PROPERLY CHECKING ANYTHING?
                REALLY? AAAAAAAAAAAAAAAAAAAAAAA
                */
            case "save":
                resource = ((StringPTC) eval.eval(args.get(0))).toString();
                if (resource.contains(":")){
                    type = resource.substring(0, resource.indexOf(":")); //TYPE:FILENAME -> TYPE
                    name = resource.substring(resource.indexOf(":")+1, resource.length()); // TYPE:FILENAME -> FILENAME
                } else {
                    type = "prg";
                    name = resource;
                }
                //type and name are set. Now load using given type + name
                save(type, name);                
                break;
        }
        return null;
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
