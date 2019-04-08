package petitcomputer;

import java.util.Arrays;

/**
 * This represents a string with similar limitations to the strings of PTC.
 * @author minxr
 */
public class StringPTC extends VariablePTC {    
    private byte[] string;
    private boolean newLine, newTab;
    
    public StringPTC(){
        string = new byte[0];
        newLine = true;
        type = STRING_LITERAL;
    }
    
    public StringPTC(int size){
        string = new byte[size];
        newLine = true;
        type = STRING_LITERAL;
    }
    
    /**
     * This constructor is for debugging purposes and partial compatability with Java Strings.
     * Shouldn't be used for any sort of interpretation of PTC data.
     * @param text 
     */
    public StringPTC(String text){
        string = new byte[text.length()];
        for (int i = 0; i < string.length; i++)
            string[i] = (byte)(text.charAt(i) % 256);
        type = STRING_LITERAL;
        newLine = true;
    }
    
    public void setLine(boolean state){
        newLine = state;
    }
    
    public void setTab(boolean state){
        newTab = state;
    }
    
    public boolean getLine(){
        return newLine;
    }
    
    public boolean getTab(){
        return newTab;
    }
    
    public void add(byte character){
        byte[] newString = new byte[string.length + 1];
        for (int i = 0; i < string.length; i++)
            newString[i] = string[i];
        newString[string.length] = character;
        string = newString;
    }
    
    public void add(StringPTC string){
        for (int i = 0; i < string.getLength(); i++){
            this.add(string.getCharacter(i));
        }
    }
    
    /**
     * Checks for a character's location within the string.
     * @param character
     * @return 
     */
    public int inString(byte character){
        for (int i = 0; i < string.length; i++)
            if (string[i] == character)
                return i;
        
        return -1;
    }
    
    /**
     * Gets a substring frome a string.
     * @param begin
     * @param length
     * @return 
     */
    public StringPTC getSubstring(int begin, int length){
        StringPTC sub = new StringPTC(0);
        sub.setLine(this.getLine()); //preserves properties of string
        sub.setTab(this.getTab()); 
        
        for (int i = begin; i < begin + Math.min(string.length, length); i++){
            sub.add(string[i]);
        }
        
        return sub;
    }
    
    public void setStringData(byte[] newString){
        if (newString.length > string.length)
            return;
        
        for (int i = 0; i < newString.length; i++)
            string[i] = newString[i];
    }
    
    public void setString(byte[] newString){
        string = newString;
    }
    
    public byte[] getString(){
        return string;
    }
    
    /**
     * Lazy way to get numbers from a string.
     * @return 
     */
    public NumberPTC getNumberFromString(){
        NumberPTC num;
        System.out.println(this.toString());
        num = new NumberPTC(Double.valueOf(this.toString()));
        System.out.println(num.getDoubleNumber());
        return num;
    }
    
    /*
    public int getNumberFromString(){
        int number = 0;
        for (int i = 0; i < length; i++){
            number += (string[i] - 48) * 4096;
            number *= 10;
        }
        return 0;
    }
    */
    
    public int getLength(){
        return string.length;
    }
    
    public void setCharacter(int index, byte character){
        if (index < string.length)
            string[index] = character;
        else {
            byte[] newstr = Arrays.copyOf(string, 1 + index);
            newstr[index] = character;
            
            string = newstr;
        }
    }
    
    public byte getCharacter(int index){
        return string[index];
    }
    
    public boolean isEmpty(){
        return (string[0] == (byte)0) && (string.length <= 1);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Arrays.hashCode(this.string);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StringPTC other = (StringPTC) obj;
        return Arrays.equals(this.string, other.string);
    }
    
    @Override
    public String toString(){
        char[] temp = new char[string.length]; 
        for (int i = 0; i < string.length; i++)
            temp[i] = (char) string[i];
        return String.valueOf(temp);
    }
    
    @Override
    public StringPTC toStringPTC(){
        return this;
    }
}
