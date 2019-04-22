package petitcomputer;

import java.util.ArrayList;
import java.util.Arrays;
import petitcomputer.VirtualDevice.Evaluator;

/**
 * Class to contain all functions related to the program data.
 * Sends commands to the VirtualDevice for action.
 * @author minxr
 */
public class Program {
    Code code;
    VirtualDevice device;
    VariablesII vars;
    Evaluator eval;
    
    public Program(ArrayList<VariablePTC> items){
        code = new Code(items, 0);
    }
    
    public void execute(){
        code.execute();
    }
    
    public void setDevice(VirtualDevice vd){
        device = vd;
        vars = device.getVars();
        eval = device.getEval();
    }
    
    /**
     * Class to contain code and execute code pieces. Requires a VirtualDevice to send commands to. 
     * @author minxr
     */
    private class Code {
        Errors error;
        ArrayList<VariablePTC> items;
        int location;

        public Code(ArrayList items, int startLocation){
            this.items = items;
            location = startLocation;
            error = null;
        }
        
        public Errors step(){
            VariablePTC command;
            ArrayList<ArrayList> arguments;
            ArrayList<VariablePTC> singleArg;
            error = null;
            
            VariablePTC item = items.get(location);
            Debug.print(Debug.CODE_FLAG, "Item# " + location + " = " + item.toString());

            switch (item.getType()) {
                case VariablePTC.STRING_COMMAND:
                    command = item;
                    if (command.toString().toLowerCase().equals("if")){
                        //IF requires special reading of args. (to eol) //no, not really
                        location++; //move past IF
                        singleArg = readUntil(items, new StringPTC(Character.toString((char)CharacterPTC.LINEBREAK)));

                        arguments = new ArrayList<>();
                        arguments.add(singleArg);

                        Debug.print(Debug.CODE_FLAG, "IF arguments: " + Arrays.deepToString(arguments.toArray()));
                    } else {
                        //it's a command. Get the argument.
                        arguments = readArgs(items);//, location);

                        Debug.print(Debug.CODE_FLAG, command.toString() + " || " + arguments.toString());
                    }

                    //in this case, arguments is an arraylist of arraylists.
                    //The outer array list groups by argument: each inner array list is a single expresion to be evaluated with Evaluator.eval() as necessary.
                    call((StringPTC) command, arguments);

                    break;
                //case VariablePTC.NUMBER_LITERAL:
                case VariablePTC.STRING_REFERENCE:
                    //new code:
                    ArrayList name = readUntil(items, new StringPTC("=")); //var name;
                    location++; //past '=' sign
                    ArrayList expression = readUntil(items, VariablePTC.LINE_SEPARATOR);
                    Debug.print(Debug.CODE_FLAG, "LET: "  + name.toString() + " = " + expression.toString());

                    vars.setVariable(name, eval.eval(expression));
                    break;
                default:
                    location++;
                    break;
            }
            
            return error;
        }
        
        public Errors execute(){
            VariablePTC command;
            ArrayList<ArrayList> arguments;
            ArrayList<VariablePTC> singleArg;
            error = null;
            
            while (location < items.size() && error == null){
                error = step();
            }
            return error;
        }

        /**
         * Reads arguments of a function from the given starting location to the next newline.
         * @param items
         * @param startLocation
         * @return 
         */
        private ArrayList<ArrayList> readArgs(ArrayList<VariablePTC> items){
            ArrayList<ArrayList> fullArgs;
            ArrayList<VariablePTC> singleArg;

            fullArgs = new ArrayList();
            //int position = startLocation; //now is command location
            location++; //move past command to 1st arg (if exists)
            VariablePTC item = items.get(location);
            //if instant newline, no args, else read args
            //searcharg loop
            while (item.getType() != VariablePTC.LINE_SEPARATOR){
                item = items.get(location);
                singleArg = new ArrayList();
                //System.out.println("A: " + item.toString());
                int nest = 0;
                while (nest > 0 || (item.getType() != VariablePTC.LINE_SEPARATOR && item.getType() != VariablePTC.ARG_SEPARATOR)){
                    //repeat until comma or newline
                    singleArg.add(item);

                    location++;
                    item = items.get(location);
                    if (item.toString().equals("(") || item.toString().equals("["))
                        nest++;
                    if (item.toString().equals(")") || item.toString().equals("]"))
                        nest--;
                    //System.out.println("AC: " + item.toString());
                }
                fullArgs.add(singleArg);
                location++;
            }

            return fullArgs;
        }
        
        /**
         * Reads arguments into an ArrayList until the given variable data is found.
         * @param items
         * @param toMatch
         * @return 
         */
        public ArrayList<VariablePTC> readUntil(ArrayList<VariablePTC> items, VariablePTC toMatch){
            //ArrayList<ArrayList> fullArgs;
            ArrayList<VariablePTC> singleArg;

            //fullArgs = new ArrayList();
            VariablePTC item = items.get(location);
            //if instant newline, no args, else read args
            //searcharg loop
            singleArg = new ArrayList();
            while (!item.equals(toMatch)){
                //repeat until comma or newline
                singleArg.add(item);

                location++;
                if (location >= items.size())
                    break;
                
                item = items.get(location);
                //System.out.println("AC untilMatch: " + location + item.toString() + item.getType());
            }
            //System.out.println("final obj was" + item.toString() + item.equals(toMatch));
            return singleArg;
        }
        
        /**
         * Reads data into an ArrayList until the given type of data is found.
         * @param items
         * @param type
         * @return 
         */
        public ArrayList<VariablePTC> readUntil(ArrayList<VariablePTC> items, int type){
            //ArrayList<ArrayList> fullArgs;
            ArrayList<VariablePTC> singleArg;

            //fullArgs = new ArrayList();
            VariablePTC item = items.get(location);
            //if instant newline, no args, else read args
            //searcharg loop
            singleArg = new ArrayList();
            while (item.getType() != type){
                //repeat until comma or newline
                singleArg.add(item);

                location++;
                if (location >= items.size())
                    break;
                    
                item = items.get(location);
                //System.out.println("AC untilType: " + item.toString() + item.getType());
            }

            return singleArg;
        }
        
        /**
         * Calls the given function with the given arguments.
         * @param command
         * @param args 
         */
        private void call(StringPTC command, ArrayList<ArrayList> args){
            error = device.act(command, args);
        }

        public void setLocation(int newLocation){
            location = newLocation;
        }

        public void setError(Errors err){
            error = err;
        }
        
        public int getLocation(){
            return location;
        }

    }
}
