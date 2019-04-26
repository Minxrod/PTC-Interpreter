package petitcomputer;

import java.util.ArrayList;
import petitcomputer.VirtualDevice.Evaluator;

/**
 * Attempt at a less bad variable storage, with added functionality for finding data from a variable name.
 * @author minxr
 */
public class VariablesII {
    public static final int
            FREEMEM = 2,
            MAINCNTL = 11,
            MAINCNTH = 12,
            TIME = 28,
            DATE = 29,
            TRUE = 33,
            FALSE = 34;
    
    public static final StringPTC SYSTEM_VARIABLES[] = new StringPTC[]{
            new StringPTC("CSRX"),
            new StringPTC("CSRX"),
            new StringPTC("FREEMEM"), //2
            new StringPTC("VERSION"),
            new StringPTC("ERR"),
            new StringPTC("ERL"),
            new StringPTC("RESULT"),
            new StringPTC("TCHX"),
            new StringPTC("TCHY"),
            new StringPTC("TCHST"),
            new StringPTC("TCHTIME"),
            new StringPTC("MAINCNTL"), //11
            new StringPTC("MAINCNTH"), //12
            new StringPTC("TABSTEP"),
            new StringPTC("TRUE"),
            new StringPTC("FALSE"),
            new StringPTC("CANCEL"),
            new StringPTC("ICONPUSE"),
            new StringPTC("ICONPAGE"),
            new StringPTC("ICONPMAX"),
            new StringPTC("FUNCNO"),
            new StringPTC("FREEVAR"),
            new StringPTC("SYSBEEP"),
            new StringPTC("KEYBOARD"),
            new StringPTC("SPHITNO"),
            new StringPTC("SPHITX"),
            new StringPTC("SPHITY"),
            new StringPTC("SPHITT"),
            new StringPTC("TIME$"), //28
            new StringPTC("DATE$"), //29
            new StringPTC("MEM$"),
            new StringPTC("PRGNAME$"),
            new StringPTC("PACKAGE$"),
            new StringPTC("TRUE"),
            new StringPTC("FALSE")};
    
    Evaluator eval;
    
    ArrayList<VariablePTC> vars;
    ArrayList<StringPTC> name;
    
    /**
     * Initializes all of the relevant variable data and names.
     * System variables are added first, to prevent name-stealing.
     * @param ev
     */
    public VariablesII(Evaluator ev){
        eval = ev;
        vars = new ArrayList<>();
        name = new ArrayList<>();
        
        initSystemVariables();
    }
    
    /**
     * Set a variable with the given name and data. If the variable doesn't exist, it will be created: otherwise, replaced.
     * @param name
     * @param data
     */
    public void setVariable(StringPTC name, VariablePTC data){
        int index = this.name.indexOf(name);
        
        if (index != -1)
            vars.set(index, data);
        else {
            createVariable(name, data);
        }
    }
    
    /**
     * Retrieves a variable with the given name.
     * @param name
     * @return 
     */
    public VariablePTC getVariable(StringPTC name){
        int index = this.name.indexOf(name);
        
        if (index != -1)
            return vars.get(index);
        else {
            createVariable(name);
            return getVariable(name);
        }
    }
    
    /**
     * Retrieves a variable with the given name expression. Name argument passed is either a STRING name or a STRING name for an array, followed by an index.
     * @param name
     * @return 
     */
    public VariablePTC getVariable(ArrayList<VariablePTC> name){
        name = (ArrayList<VariablePTC>) name.clone();
        if (name.size() == 1)
            return getVariable((StringPTC) name.get(0));
        else{ //it's an array and must determine index.. Index should just be remainder of the name, evaluated.
            ArrayPTC array = (ArrayPTC) getVariable((StringPTC) name.get(0));
            name.remove(0); //remove name from list.
            name.remove(0); //remove opening bracket
            name.remove(name.size()-1); //remove closing bracket
            
            ArrayList<VariablePTC> indexes = eval.evaluate(name);
            Debug.print(Debug.VARIABLE_FLAG, array.toString());
            Debug.print(Debug.VARIABLE_FLAG, name + " " + indexes);
            if (indexes.size() == 1){ //one index
                int index = ((NumberPTC)indexes.get(0)).getIntNumber();
                
                Debug.print(Debug.VARIABLE_FLAG, array.getElement(index) + " TarrayElelmt:" + array.getElement(index).getType());
                return array.getElement(index);
            } else { //two indexes.
                int ix = ((NumberPTC)indexes.get(0)).getIntNumber();
                int iy = ((NumberPTC)indexes.get(2)).getIntNumber();
                
                return ((ArrayPTC)array.getElement(ix)).getElement(iy); //fixed
            }
            /*NumberPTC index = (NumberPTC) Evaluator_OLD.eval(name); //name is really the index now, no brackets.
            int indexAsInt = index.getIntNumber();
            
            return array.getElement(indexAsInt);*/
        }
    }
    
    /**
     * Sets a variable with the given name ArrayList.
     * Useful for arrays where the "name" might be an expression.
     * @param name - name of variable to set
     * @param data - the data to store to the variable
     */
    public void setVariable(ArrayList<VariablePTC> name, VariablePTC data){
        if (name.size() == 1)
            setVariable((StringPTC) name.get(0), data);
        else{ //it's an array and must determine index.. Index should just be remainder of the name, evaluated.
            StringPTC arrName = (StringPTC) name.get(0);
            Debug.print(Debug.VARIABLE_FLAG, "setVar Array Name:" + arrName);
            ArrayPTC array = (ArrayPTC) getVariable(arrName);
            name.remove(0); //remove name from list.
            name.remove(0); //remove opening bracket
            name.remove(name.size()-1); //remove closing bracket
            
            ArrayList<VariablePTC> indexes = eval.evaluate(name);
            if (indexes.size() == 1){ //one index
                int index = ((NumberPTC)indexes.get(0)).getIntNumber();
                
                array.setElement(index, data);
            } else { //two indexes.
                int ix = ((NumberPTC)indexes.get(0)).getIntNumber(); //first index
                int iy = ((NumberPTC)indexes.get(2)).getIntNumber(); //second index
                
               ArrayPTC subArray = ((ArrayPTC)array.getElement(ix));
               subArray.setElement(iy, data);
            }
            //setVariable(arrName, array);
        }
    }
    
    /**
     * Creates a variable of the given name while assigning a default value..
     * @param name 
     */
    public void createVariable(StringPTC name){
        int index = this.name.indexOf(name);
        VariablePTC newVar;
        
        if (name.inString(CharacterPTC.DOLLAR) != -1)
            newVar = new StringPTC(0);
        else if (name.inString(CharacterPTC.OPEN_SQUARE_BRACKET) != -1 || name.inString(CharacterPTC.OPEN_PARENTHESIS) != -1)
            newVar = new ArrayPTC(10); //for some inane reason
        else
            newVar = new NumberPTC(0);
        
        if (index == -1){ //only create/add to list if variable doesn't yet exist.
            vars.add(newVar);
            this.name.add(name);
        }
    }
    
    /**
     * Creates a variable of the given name while assigning the given value.
     * @param name
     * @param data
     */
    public void createVariable(StringPTC name, VariablePTC data){
        vars.add(data);
        this.name.add(name);
    }
    
    public void createArray(ArrayList<VariablePTC> name){
        StringPTC arrayName = (StringPTC) name.get(0);
        name.remove(0); //remove name from list.
        name.remove(0); //remove opening bracket
        name.remove(name.size()-1); //remove closing bracket
        name = eval.evaluate(name); //evaluate to get either "index" or "ix,iy".
        
        if (name.size() == 1)
            createArray(arrayName, ((NumberPTC)name.get(0)).getIntNumber());
        else
            createArray(arrayName, ((NumberPTC)name.get(0)).getIntNumber(), ((NumberPTC)name.get(2)).getIntNumber());
        
        ArrayPTC arr = (ArrayPTC) getVariable(arrayName);
        for (int i = 0; i < arr.getLength(); i++){
            if (arr.getElement(0) != null && arr.getElement(0).getType() == VariablePTC.ARRAY){
                ArrayPTC arr2 = (ArrayPTC) arr.getElement(i);
                for (int j = 0; j < arr2.getLength(); j++)
                    if (arrayName.toString().contains("$"))
                        arr2.setElement(j, new StringPTC(""));
                    else 
                        arr2.setElement(j, new NumberPTC(0));
            } else {
                if (arrayName.toString().contains("$"))
                    arr.setElement(i, new StringPTC(""));
                else 
                    arr.setElement(i, new NumberPTC(0));
            }
        }
    }
    
    /**
     * Creates an array of variables with the format name(index[,index])
     * @param name
     * @param dim1
     */
    public void createArray(StringPTC name, int dim1){
        ArrayPTC array = new ArrayPTC(dim1);
        
        setVariable(name, array); //gives the list of vars an array reference.
    }
    
    public void createArray(StringPTC name, int dim1, int dim2){
        ArrayPTC array = new ArrayPTC(dim1);
        for (int i = 0; i < dim1; i++) //for each array in array: create another array. RESULT: 2D array.
            array.setElement(i, new ArrayPTC(dim2));
        
        setVariable(name, array); //gives the list of vars an array reference.
    }
    /**
     * Initializes the system variables with 0 or "". Sets read/write permissions on each. 
     */
    private void initSystemVariables(){                   
        Object[][] sysvars = new Object[][]{
            //name      read  write
            /*Numeric system variables*/
            {"csrx",     true, false},
            {"csry",     true, false},
            {"freemem",  true, false},
            {"version",  true, false},
            {"err",      true, false},
            {"erl",      true, false},
            {"result",   true, false},
            {"tchx",     true, false},
            {"tchy",     true, false},
            {"tchst",    true, false},
            {"tchtime",  true, false},
            {"maincntl", true, false},
            {"maincnth", true, false},
            {"tabstep",  true, true},
            {"true",     true, false},
            {"false",    true, false},
            {"cancel",   true, false},
            {"iconpuse", true, true},
            {"iconpage", true, true},
            {"iconpmax", true, true},
            {"funcno",   true, false},
            {"freevar",  true, false},
            {"sysbeep",  true, true},
            {"keyboard", true, false},
            {"sphitno",  true, false},
            {"sphitx",   true, false},
            {"sphity",   true, false},
            {"sphitt",   true, false},
            /*String system variables*/
            {"time$",    true, false},
            {"date$",    true, false},
            {"mem$",     true, true},
            {"prgname$", true, false},
            {"package$", true, false},
            {"TRUE", true, false},
            {"FALSE", true, false}
        };
        
        for (int i = 0; i < sysvars.length; i++){//Object[] vardat : sysvars){
            Object[] vardat = sysvars[i];
            StringPTC varname = SYSTEM_VARIABLES[i];
            
            this.createVariable(varname);
            VariablePTC var = this.getVariable(varname);
            var.setReadable((boolean) vardat[1]); //literally all of these are true. Only added for completeness...
            var.setWriteable((boolean) vardat[2]); //this one actually matters. Attempting to set a var should throw an error....
        }
        
        this.setVariable(SYSTEM_VARIABLES[TRUE], new NumberPTC(1));
        //false will default to 0 anyways.
    }
    
    public void act(StringPTC command, ArrayList<ArrayList> args){
        if (command.toString().toLowerCase().equals("dim")){
            ArrayList<VariablePTC> arg = args.get(0);
            
            createArray(arg);
        }
    }
    
    @Override
    public String toString(){
        String temp = "";
                
        for (int i = 0; i < name.size(); i++){
            temp = temp + name.get(i) + "  " + vars.get(i) + "\n";
        }
        
        return temp;
    }
}
