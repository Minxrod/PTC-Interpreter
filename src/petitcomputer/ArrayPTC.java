package petitcomputer;

import java.util.Arrays;

/**
 * Array to hold variables of VariablePTC format. Note that this class is typeless and does not differentiate between string arrays and numeric arrays.
 * @author minxr
 */
public class ArrayPTC extends VariablePTC{
    
    VariablePTC[] elements;

    public ArrayPTC(int size){
        elements = new VariablePTC[size];
        type = VariablePTC.ARRAY;
    }
    
    public VariablePTC getElement(int index){
        return elements[index];
    }
    
    public int getLength(){
        return elements.length;
    }
    
    public void setElement(int index, VariablePTC data){
        elements[index] = data;
    }
    
    @Override
    public StringPTC toStringPTC() {
        return new StringPTC("don't convert arrays to strings please");
    }

    @Override
    public String toString(){
        return Arrays.toString(elements);
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Arrays.deepHashCode(this.elements);
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
        final ArrayPTC other = (ArrayPTC) obj;
        return Arrays.deepEquals(this.elements, other.elements);
    }
}
