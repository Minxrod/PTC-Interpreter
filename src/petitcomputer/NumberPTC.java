package petitcomputer;

import java.text.NumberFormat;

/**
 * The fixed point number format.
 * @author minxr
 */
public class NumberPTC extends VariablePTC {
    int number;
    
    public NumberPTC(){
        number = 0;
        type = NUMBER_LITERAL;
    }
    
    /**
     * Creates a new fixed-point representation of the provided integer.
     * @param intNumber 
     */
    public NumberPTC(int intNumber){
        number = intNumber * 4096;
        type = NUMBER_LITERAL;
    }
   
    /**
     * Creates a new fixed-point number from the provided double.
     * @param fpNumber 
     */
    public NumberPTC(double fpNumber){
        number = (int) (fpNumber * 4096);
        type = NUMBER_LITERAL;
    }
    /**
     * Sets the data of a NumberPTC.
     * @param newData
     */
    public void setData(int newData){
        number = newData;
    }
    
    /**
     * Returns the integer from the stored fixed-point number.
     * @return 
     */
    public int getIntNumber(){
        return (int)(number / 4096.0);
    }
    
    public double getDoubleNumber(){
        return (double)(number / 4096.0);
    }
    
    @Override
    public StringPTC toStringPTC(){
        NumberFormat f = NumberFormat.getNumberInstance();
        f.setMinimumFractionDigits(0);
        f.setMaximumFractionDigits(3);
        f.setGroupingUsed(false);
        return new StringPTC(f.format((double)number / 4096.0));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + this.number;
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
        final NumberPTC other = (NumberPTC) obj;
        return this.number == other.number;
    }
    
    @Override
    public String toString(){
        return toStringPTC().toString();
    }
}