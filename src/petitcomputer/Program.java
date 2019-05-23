package petitcomputer;

import java.util.ArrayList;
import java.util.Arrays;
import petitcomputer.CharacterPTC.Char;
import petitcomputer.VirtualDevice.Evaluator;

/**
 * Class to contain all functions related to the program data.
 * Sends commands to the VirtualDevice for action.
 * @author minxr
 */
public class Program implements ComponentPTC {
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
    
    @Override
    public Errors act(StringPTC command, ArrayList<ArrayList> arguments){
        Debug.print(Debug.ACT_FLAG, "ACT CODE: " + command.toString());
        switch (command.toString().toLowerCase()){
            case "end":
                //end program.
                code.setError(Errors.BREAK);
                break;
            case "wait":
                NumberPTC frames = (NumberPTC) eval.eval(arguments.get(0));
                
                code.wait(frames.getIntNumber());
                break;
            case "if":
                code.conditional(arguments);
                break;
            case "then":
                //will never run unless an IF is missing. So, return an error.
                code.setError(Errors.UNDEFINED_ERROR);
                break;
            case "else":
                //means that a then block has ended.
                //Skip to EOL.
                code.to_eol();
                //code.readUntil(items, new StringPTC(Character.toString((char)CharacterPTC.LINEBREAK)));
                break;
            case "goto":
                StringPTC label = (StringPTC) eval.eval(arguments.get(0));
                
                code.go_to(label);
                break;
            case "gosub":
                label = (StringPTC) eval.eval(arguments.get(0));
                
                code.gosub(label);
                break;
            case "on":
                code.on(arguments);
                break;
            case "for":
                code.for_to(arguments);
                break;
            case "next":
                code.next();
                break;
            case "return":
                code.ret();
                break;
            default:
                Debug.print(Debug.ACT_FLAG, "ERROR: " + command);
        }
        return null;
    }
    
    @Override
    public VariablePTC func(StringPTC function, ArrayList<VariablePTC> args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            error = null;
            
            while (location < items.size() && error == null){
                step(); //read commands, check for errors after each one
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
            while (location < items.size() && item.getType() != VariablePTC.LINE_SEPARATOR){
                item = items.get(location);
                singleArg = new ArrayList();
                //System.out.println("A: " + item.toString());
                int nest = 0;
                while (nest > 0 || (item.getType() != VariablePTC.LINE_SEPARATOR && item.getType() != VariablePTC.ARG_SEPARATOR)){
                    //repeat until comma or newline
                    singleArg.add(item);

                    location++;
                    if (location >= items.size())
                        break;
                    
                    item = items.get(location);
                    Debug.print(Debug.CODE_FLAG, "RA: Item# " + location + " = " + item.toString());

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
                Debug.print(Debug.CODE_FLAG, "variable: Item# " + location + " = " + item.toString());

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
                Debug.print(Debug.CODE_FLAG, "type: Item# " + location + " = " + item.toString());

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
            device.act(command, args); //maintain old error unless it was null
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
        
        /*
         * CODE FUNCTIONS + COMMANDS
         * Stuff that requires the code to function.
         */

        public void for_to(ArrayList<ArrayList> args){
            ArrayList<VariablePTC> arguments = new ArrayList<>();

            for (ArrayList arg : args)
                arguments.addAll(arg);
            //arguments is more or less a list of code now

            ArrayList<VariablePTC> variable = new ArrayList<>();
            ArrayList<VariablePTC> expression = new ArrayList<>();
            ArrayList<VariablePTC> comparison = new ArrayList<>();
            ArrayList<VariablePTC> increment = new ArrayList<>();

            int i = 0;
            int toLocation = 0;     //ints not booleans
            int stepLocation = 0;   //in case it's useful later
            int equalLocation = 0;  //who knows if it helped

            do {
                String arg = arguments.get(i).toString().toLowerCase();

                switch (arg) {
                    case "to":
                        toLocation = i + 1;
                        break;
                    case "step":
                        stepLocation = i + 1;
                        break;
                    case "=":
                        equalLocation = i;
                        expression.add(arguments.get(i));
                        break;
                    default: //cool
                        if (equalLocation == 0){ //not end of variable
                            variable.add(arguments.get(i));
                            expression.add(arguments.get(i));
                        }else if (toLocation == 0) //hasn't found end of initialization expression
                            expression.add(arguments.get(i));
                        else if (stepLocation == 0) //hasn't found the STEP in (FOR...TO...STEP...)
                            comparison.add(arguments.get(i));
                        else //there's a step, read until end of line
                            increment.add(arguments.get(i));
                        break;
                }

                i++;//next element
            } while (i < arguments.size());

            if (increment.isEmpty())
                increment.add(new NumberPTC(1));

            //initialize variable
            Code init = new Code(expression, 0);
            init.execute();

            //exit condition
            StringPTC lessThan = new StringPTC("<=");
            lessThan.setType(VariablePTC.STRING_OPERATOR);

            ArrayList<VariablePTC> condition = new ArrayList<>();
            condition.addAll(variable);             //variable
            condition.add(lessThan);                //<=
            condition.addAll(comparison);           //end state

            //step equation
            StringPTC equals = new StringPTC("=");
            equals.setType(VariablePTC.STRING_OPERATOR);

            StringPTC plus = new StringPTC("+");
            plus.setType(VariablePTC.STRING_OPERATOR);

            StringPTC newLine = new StringPTC(Character.toString((char)CharacterPTC.LINEBREAK));
            newLine.setType(VariablePTC.LINE_SEPARATOR);

            ArrayList<VariablePTC> steps = new ArrayList<>();
            steps.addAll(variable);                 //variable
            steps.add(equals);                      //=
            steps.addAll(variable);                 //variable
            steps.add(plus);                        //+
            steps.addAll(increment);                //step
           // steps.add(newLine);                     //(endline)

            Code step = new Code(steps, 0);

            int endOfFor = getLocation(); //location is at the end of the FOR-TO[-STEP]. 

            Debug.print(Debug.CODE_FLAG, "FOR LOOP INIT\n!!!!!!!!!!!!!!!!!!!!1\n" + expression.toString() + "\n" + condition.toString() + "\n" + steps.toString());
            while (((NumberPTC)eval.eval(condition)).getIntNumber() == 1){
                setLocation(endOfFor);
                execute();
                //gets here after hitting "next"
                if (error == Errors.NEXT_WITHOUT_FOR)
                    setError(null); //there's a FOR so it's actually fine.

                Debug.print(Debug.CODE_FLAG, "FOR LOOP DEBUG\nXXXXXXXXXXXXXXXXXXXXx\n" + expression.toString() + "\n" + condition.toString() + "\n" + steps.toString());
                step.setLocation(0); //reset increment
                step.execute(); //lincrement the var
            }
            //loop is done.

            //System.out.println(Evaluator.eval(condition).toString());
        }

        public void next(){
            setError(Errors.NEXT_WITHOUT_FOR); //has encountered a next
        }

        /**
         * Return function.
         */
        public void ret(){
            setError(Errors.RETURN_WITHOUT_GOSUB);
        }

        /**
         * Depending on a given expression, jump to a set location.
         * @param args
         */
        public void on(ArrayList<ArrayList> args){
            ArrayList<VariablePTC> arguments = new ArrayList<>(); //converts multiarg to a single list of args

            for (ArrayList arg : args)
                arguments.addAll(arg);

            ArrayList<VariablePTC> expression = new ArrayList<>();
            ArrayList<VariablePTC> labels = new ArrayList<>();

            int i = 0;
            boolean isGosub = false;
            int goLocation = 0;
            do {
                String arg = arguments.get(i).toString().toLowerCase();

                switch (arg) {
                    case "gosub":
                        isGosub = true;
                    case "goto":
                        goLocation = i;
                        break;
                    default: //cool
                        if (goLocation == 0) //hasn't found end of 'on' expression
                            expression.add(arguments.get(i));
                        else
                            labels.add(arguments.get(i));
                        break;
                }

                i++;//next element
            } while (i < arguments.size());

            Debug.print(Debug.PROCESS_FLAG, "ON expression: " + expression.toString());
            Debug.print(Debug.PROCESS_FLAG, "GOTO/SUB: " + labels.toString());

            //evaluate expression a
            NumberPTC result = (NumberPTC) eval.eval(expression);

            if (result.getIntNumber() < labels.size() && result.getIntNumber() > -1){
                if (!isGosub)
                    go_to((StringPTC) labels.get(result.getIntNumber()));
                else
                    gosub((StringPTC) labels.get(result.getIntNumber()));
            }
        }

        /**
         * GOTO function - sets the main location of the program to the location of the given label.
         * @param argument
         */
        public void go_to(StringPTC argument){
            //takes a label
            String label = argument.toString().toLowerCase();
            //finds a label
            int labelLocation = items.size(); //default to end of program
            for (int i = 0; i < items.size(); i++)
                if (label.equals(items.get(i).toString().toLowerCase()))
                    if (i == 0 || items.get(i-1).getType() == VariablePTC.LINE_SEPARATOR){
                        labelLocation = i;
                        break;
                }

            Debug.print(Debug.PROCESS_FLAG, "Location found: " + labelLocation + "\nLabel search for: " + label + "\nLabel found: " + items.get(labelLocation).toString());
            //sets main location to that label
            setLocation(labelLocation);
        }

        /**
         * GOSUB: jumps to a sub routine and saves the jump location. Returns to the location after hitting a RETURN.
         * @param argument
         * @return 
         */
        public Errors gosub(StringPTC argument){
            //do it later.
            String label = argument.toString().toLowerCase();
            //find label
            int labelLocation = items.size();
            for (int i = 0; i < items.size(); i++)
                if (label.equals(items.get(i).toString().toLowerCase()))
                    if (i == 0 || items.get(i-1).getType() == VariablePTC.LINE_SEPARATOR){
                        labelLocation = i;
                        break;
                }

            Debug.print(Debug.PROCESS_FLAG, "Location found: " + labelLocation + "\nLabel search for: " + label + "\nLabel found: " + items.get(labelLocation).toString());

            int callLocation = getLocation(); //end of GOSUB command

            setLocation(labelLocation); //jump to the label
            execute(); //run from label

            if (error == Errors.RETURN_WITHOUT_GOSUB){ //if returned intentionally, set location back to just after the GOSUB.
                setLocation(callLocation); //RETURN lends to here
                error = null; //Error is meant to appear, therefore can be discarded.
            } 
            
            return error; //doesn't do anything, really.
        }

        /**
         * IF statements, but using PTC logic. 
         * @param args 
         */
        public void conditional(ArrayList<ArrayList> args){
            ArrayList<VariablePTC> arguments = new ArrayList<>();

            for (ArrayList arg : args)
                arguments.addAll(arg);        
            //arguments roughly of form (expression THEN statement(s) [ELSE statements])

            //read until THEN or GOTO: from beginning to here is expression to evaluate.
            ArrayList<VariablePTC> expression = new ArrayList<>();
            ArrayList<VariablePTC> thenCode = new ArrayList<>();
            ArrayList<VariablePTC> elseCode = new ArrayList<>();

            int i = 0;
            int thenLocation = 0;
            int elseLocation = 0;
            do {
                String arg = arguments.get(i).toString().toLowerCase();

                switch (arg) {
                    case "then":
                        if (thenLocation == 0)
                            thenLocation = i + 1;
                        break;
                    case "goto":
                        if (thenLocation == 0) //prevent "...THEN <something>:GOTO..." from breaking and skipping something, right?
                            thenLocation = i;
                        break;
                    case "else":
                        if (elseLocation == 0)
                            elseLocation = i + 1;
                        break;
                    default: //obsolete, but good for debug and might be useful later
                        if (thenLocation == 0) //hasn't found end of expression
                            expression.add(arguments.get(i));
                        else if (elseLocation == 0) //hasn't found an ELSE yet
                            thenCode.add(arguments.get(i));
                        else  //at this point, it will go until it hits the end of the list, and is definitely an else block.
                            elseCode.add(arguments.get(i));
                        break;
                }

                i++;//next element
            } while (i < arguments.size());            

            Debug.print(Debug.PROCESS_FLAG, expression.toString());
            Debug.print(Debug.PROCESS_FLAG, thenCode.toString());
            Debug.print(Debug.PROCESS_FLAG, elseCode.toString());

            NumberPTC result = (NumberPTC) eval.eval(expression); //condition of IF: true or false? (really, 0 or !0)

            //Code then = new Code(thenCode, 0);
            //Code notThen = new Code(elseCode, 0);

            Debug.print(Debug.PROCESS_FLAG, "result:" + result.toString());

            StringPTC go = new StringPTC("GOTO");
            go.setType(VariablePTC.STRING_COMMAND);

            //THEN...LABEL        

            //THEN ...CODE... [ELSE]...
            if (result.getDoubleNumber() != 0){
                if (thenCode.get(0).getType() == VariablePTC.STRING_LABEL){
                    go_to((StringPTC) thenCode.get(0));
                } else 
                    setLocation(getLocation() - arguments.size() + thenLocation); //go from end of IF back to beginning and add the offset of the THEN.
                //(conditional(args) only has a piece of the whole program and thus locations are off by that much.)
            } else //ELSE ...CODE
                if (elseLocation != 0)
                    setLocation(getLocation() - arguments.size() + elseLocation); //Same thing with the added condition that an else has to exist for it to be used. :D
        }
        /**
         * Waits the desired number of frames.
         * @param frames 
         */
        public void wait(int frames){
            try {
                Thread.sleep(frames * 1000 / 60); 
            } catch (InterruptedException ex) {
            }
        }
        
        /**
         * Skips to the end of the line.
         */
        public void to_eol(){
            readUntil(items, Char.LINEBREAK.getStringPTC()); //new StringPTC(Character.toString((char)CharacterPTC.LINEBREAK)));
        }
    }
}
