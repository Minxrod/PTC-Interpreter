package petitcomputer;

/**
 * Class for debugging with flags. Extra information can be disabled to make debugging specifc things easier.
 * @author minxr
 */
public class Debug {
    public static final int ALL = 0xFFFFFFFF;
    public static final int CONSOLE_FLAG = 1;
    public static final int PROCESS_FLAG = 2;
    public static final int EVALUATOR_FLAG = 4;
    public static final int CODE_FLAG = 8;
    public static final int DATA_FLAG = 16;
    public static final int VARIABLE_FLAG = 32;
    public static final int BACKGROUND_FLAG = 64;
    public static final int PANEL_FLAG = 128;
    public static final int SOUND_FLAG = 256;
    public static final int ACT_FLAG = 512;
    public static final int INPUT_FLAG = 1024;
    public static final int GUI_FLAG = 2048;
    public static final int COLOR_FLAG = 4096;
    
    private static int flags = 0; //default: no debug
    
    /**
     * Sets all flags at once using each bit of an int.
     * @param flag 
     */
    public static void setFlags(int flag){
        flags |= flag;
    }
    
    public static void resetFlags(int flag){
        flags &= flag^ALL;
    }
    
    public static void print(int source, String s){
        if ((flags & source) != 0)
            System.out.println(s);
    }
    
}
