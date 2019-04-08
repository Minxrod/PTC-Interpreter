package petitcomputer;

abstract public class VariablePTC {
    
    /**
     * Marks EOL and colons.
     */
    public static final int LINE_SEPARATOR = -9999999;
    /**
     * Marks commas. That's pretty much it.
     */
    public static final int ARG_SEPARATOR = -999;
    /**
     * String literal. Can be taken as-is for data.
     */
    public static final int STRING_LITERAL = 7;
    /**
     * String reference = variable name. Not necessarily string data contained within.
     */
    public static final int STRING_REFERENCE = 1;
    /**
     * String expression = string data that must be evaluated for use.
     */
    public static final int STRING_EXPRESSION = 2;
    /**
     * String operator. Stuff like +, -, as a string. Used in evaluation.
     */
    public static final int STRING_OPERATOR = 3;
    /**
     * String function. It's a function call, but contained in a string.
     */
    public static final int STRING_FUNCTION = 4;
    /**
     * String command. Command in string form.
     */
    public static final int STRING_COMMAND = 5;
    /**
     * String name of a label.
     */
    public static final int STRING_LABEL = 6;
    
    /**
     * Numeric literal. It's a number and can be used as a number.
     */
    public static final int NUMBER_LITERAL = 8;
    
    /**
     * An array of undefined type.
     */
    public static final int ARRAY = 16;
    
    boolean readable;
    boolean writeable;
    
    int type;
    
    public VariablePTC(){
        readable = true;
        writeable = true;
    }
    
    public void setReadable(boolean r){
        readable = r;
    }
    
    public boolean getReadable(){
        return readable;
    }
    
    public void setWriteable(boolean w){
        writeable = w;
    }
    
    public boolean getWriteable(){
        return writeable;
    }
    
    public void setType(int newType){
        type = newType;
    }
    
    /**
     * 
     * @return 
     */
    public int getType(){
        return type;
    }
    
    /**
     *
     * @return
     */
    abstract public StringPTC toStringPTC();
    
    @Override
    abstract public boolean equals(Object o);

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.type;
        return hash;
    }

}
