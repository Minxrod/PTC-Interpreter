package petitcomputer;

import java.util.ArrayList;

/**
 * Unifies various elements of PTC that have separate subsystems or data.
 * @author minxr
 */
public interface ComponentPTC {
    
    /**
     * Handles commands passed to the component and modifies its data.
     * @param command
     * @param args
     * @return 
     */
    public Errors act(StringPTC command, ArrayList<ArrayList> args);
    
    /**
     * Uses the given function with given arguments to return a single value in VariablePTC format.
     * @param function
     * @param args
     * @return 
     */
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args);
}
