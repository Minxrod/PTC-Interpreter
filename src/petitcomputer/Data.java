/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petitcomputer;

import java.util.ArrayList;

/**
 * Class to store and find DATA statements.
 * @author minxr
 */
public class Data implements ComponentPTC{
    ProcessII.Evaluator eval;
    VariablesII vars;
      
    ArrayList<VariablePTC> program;
    int currentData;
    boolean isData;
    
    public Data(VariablesII variables, ProcessII.Evaluator ev){
        eval = ev;
        currentData = 0;
        isData = false;
        vars = variables;
    }

    public void setProgramData(ArrayList<VariablePTC> prog){
        program = prog;
    }
    
    /**
     * Reads the next available DATA sequence into a given variable name.
     * @param variable
     * @return 
     */
    public Errors read(ArrayList<VariablePTC> variable){
        StringPTC data = findNextDATA();
        Debug.print(Debug.DATA_FLAG, "Var name: " + variable + " Var: " + vars.getVariable(variable) + " Type: " + vars.getVariable(variable).getType() + " Read: " + data);
        //calling vars.getVariable is resulting in a string being converted to a number type.
        
        if (vars.getVariable(variable).getType() == VariablePTC.NUMBER_LITERAL)
            vars.setVariable(variable, data.getNumberFromString());
        /*else if (vars.getVariable(variable).getType() == VariablePTC.ARRAY)
            vars;*/
        else //var is a string
            vars.setVariable(variable, data);
        
        return null;
        
    }
    
    public void restore(StringPTC argument){
        //takes a label
        String label = argument.toString().toLowerCase();
        //finds a label
        int location = program.size(); //default to end of program
        for (int i = 0; i < program.size(); i++)
            if (label.equals(program.get(i).toString().toLowerCase()))
                if (i == 0 || program.get(i-1).getType() == VariablePTC.LINE_SEPARATOR){
                    location = i;
                    break;
            }
        
        Debug.print(Debug.DATA_FLAG, "Location found: " + location + "\nLabel search for: " + label + "\nLabel found: " + program.get(location).toString());
        
        currentData = location;
    }
    
    private StringPTC findNextDATA(){
        while (!isData){
            //System.out.println(currentData);
            if (program.get(currentData).toString().toLowerCase().equals("data"))
                isData = true;
            currentData++; //check next element.  If DATA was found, go past to first element.
        }
        
        StringPTC data = new StringPTC(0);
        Debug.print(Debug.DATA_FLAG, "Reading data");
        while (isData){ //until hits comma or eol, continue getting elements of DATA.
            if (program.get(currentData).getType() == VariablePTC.LINE_SEPARATOR)
                isData = false;
            else if (program.get(currentData).getType() == VariablePTC.ARG_SEPARATOR)
                break; //it is still data (isData is true) but stop reading the data for now
            else {
                data.add(program.get(currentData).toStringPTC());
                currentData++; //increment to next element
                Debug.print(Debug.DATA_FLAG, "Got: " + data);
            }
        }
        currentData++; //move past comma or eol
        
        if (data.getLength() == 0)
            return null;
        
        return data;
    }
    
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> args) {
        Debug.print(Debug.ACT_FLAG, "DATA act branch: " + command.toString() + " args: " + args.toString());
        switch (command.toString().toLowerCase()){
            case "read":
                for (ArrayList<VariablePTC> varExpression : args){
                    read(varExpression);
                }
                break;
            case "restore":
                StringPTC label = (StringPTC) eval.eval(args.get(0));
                
                restore(label);
                break;
            default:
                Debug.print(Debug.DATA_FLAG, "DATA ERROR:" + command.toString());
        }
        return null;
    }

    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        throw new UnsupportedOperationException("Unsupported operation. " + getClass().getName() + " does not have any functions to call.");
    }
}
