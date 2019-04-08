package petitcomputer;

import java.util.ArrayList;

/**
 * Class that handles mathematical and logical operations on numbers in PTC format. Pretty much a wrapper for java.lang.Math, as well as the simple operators like +-/*
 * @author minxr
 */
public class MathPTC {
    
    /**
     * Adds two NumberPTC numbers and returns the sum as NumberPTC format.
     * @param a
     * @param b
     * @return 
     */
    public static NumberPTC add(NumberPTC a, NumberPTC b){
        double a_plus_b = a.getDoubleNumber() + b.getDoubleNumber();
        NumberPTC c = new NumberPTC(a_plus_b);
        
        return c;       
    }
    
    public static NumberPTC sub(NumberPTC a, NumberPTC b){
        double a_minus_b = a.getDoubleNumber() - b.getDoubleNumber();
        NumberPTC c = new NumberPTC(a_minus_b);
        
        return c;    
    }
    
    public static NumberPTC mult(NumberPTC a, NumberPTC b){
        double a_times_b = a.getDoubleNumber() * b.getDoubleNumber();
        NumberPTC c = new NumberPTC(a_times_b);
        
        return c; 
    }
    
    public static NumberPTC div(NumberPTC a, NumberPTC b){
        double a_div_b = a.getDoubleNumber() / b.getDoubleNumber();
        NumberPTC c = new NumberPTC(a_div_b);
        
        return c; 
    }
    
    public static NumberPTC mod(NumberPTC a, NumberPTC b){
        double a_mod_b = a.getIntNumber() % b.getIntNumber();
        NumberPTC c = new NumberPTC(a_mod_b);
        
        return c; 
    }
    
    public static NumberPTC not(NumberPTC a){
        int not_a = ~a.getIntNumber();
        NumberPTC b = new NumberPTC(not_a);
        
        return b;
    }
    
    public static NumberPTC negate(NumberPTC a){
        double neg_a = -a.getDoubleNumber();
        NumberPTC b = new NumberPTC(neg_a);
        
        return b;
    }
    
    public static NumberPTC logicalNot(NumberPTC a){
        int logic_not_a;
        if (a.getDoubleNumber() != 0)
            logic_not_a = 0;
        else
            logic_not_a = 1;
        
        NumberPTC b = new NumberPTC(logic_not_a);
        
        return b;
    }
    
    /**
     * Evaluates whether a<b. If a<b, returns 1. Otherwise returns 0.
     * @param a
     * @param b
     * @return 
     */
    public static NumberPTC lessThan(NumberPTC a, NumberPTC b){
        boolean a_lessThan_b = a.getDoubleNumber() < b.getDoubleNumber();
        
        if (a_lessThan_b)
            return new NumberPTC(1);
        else
            return new NumberPTC(0);
    }
    
    /**
     * Evaluates a>b. If a>b, return 1, else return 0.
     * @param a
     * @param b
     * @return 
     */
    public static NumberPTC moreThan(NumberPTC a, NumberPTC b){
        boolean a_moreThan_b = a.getDoubleNumber() > b.getDoubleNumber();
        
        if (a_moreThan_b)
            return new NumberPTC(1);
        else
            return new NumberPTC(0);
    }
    
    /*==================================
    = BEGIN FUNCTIONS || END OPERATORS =
    ==================================*/
    
    /**
     * Calculates the sine of a given radian angle in NumberPTC format.
     * @param angle
     * @return 
     */
    public static NumberPTC sin(NumberPTC angle){
        double a = angle.getDoubleNumber();
        
        return new NumberPTC(Math.sin(a));
    }
    
    /**
     * Calculates the cosine of a given radian angle in NumberPTC format.
     * @param angle
     * @return 
     */
    public static NumberPTC cos(NumberPTC angle){
        double a = angle.getDoubleNumber();
        
        return new NumberPTC(Math.cos(a));
    }
    
    /**
     * Calculates the tangent value of a given radian angle in NumberPTC format.
     * @param angle
     * @return 
     */
    public static NumberPTC tan(NumberPTC angle){
        double a = angle.getDoubleNumber();
        
        return new NumberPTC(Math.tan(a));
    }
    
    /**
     * Calculate the arc tangent of a given value and returns the result in NumberPTC format.
     * @param value
     * @return 
     */
    public static NumberPTC atan(NumberPTC value){
        double v = value.getDoubleNumber();
        
        return new NumberPTC(Math.atan(v));
    }
    
    /**
     * Converts a given angle in degrees to an approximate equivalent angle in radians.
     * @param degAngle
     * @return 
     */
    public static NumberPTC rad(NumberPTC degAngle){
        double a = degAngle.getDoubleNumber();
        
        return new NumberPTC(Math.toRadians(a));
    }
    
    /**
     * Converts a given radian angle to it's approximation in degrees.
     * @param radAngle
     * @return 
     */
    public static NumberPTC deg(NumberPTC radAngle){
        double a = radAngle.getDoubleNumber();
        
        return new NumberPTC(Math.toDegrees(a));
    }
    
    /**
     * Calculates the natural logarithm of a given number.
     * @param value
     * @return 
     */
    public static NumberPTC log(NumberPTC value){
        double v = value.getDoubleNumber();
        
        if (v <= 0)
            return null;  //null indicates an out of range error
        else
            return new NumberPTC(Math.log(v));
    }
    
    public static NumberPTC exp(NumberPTC value){
        return new NumberPTC(Math.exp(value.getDoubleNumber()));
    }
    
    public static NumberPTC pow(NumberPTC base, NumberPTC exponent){
        return new NumberPTC(Math.pow(base.getDoubleNumber(), base.getDoubleNumber()));
    }
    
    /**
     * Calculates the square root of the given number.
     * @param value
     * @return 
     */
    public static NumberPTC sqr(NumberPTC value){
        return new NumberPTC(Math.sqrt(value.getDoubleNumber()));
    }
    /**
     * Returns +1, -1, or 0 depending on the sign of the given argument.
     * @param value
     * @return 
     */
    public static NumberPTC sgn(NumberPTC value){
        double num = value.getDoubleNumber();
        
        if (num > 0)
            return new NumberPTC(1);
        else if (num < 0)
            return new NumberPTC(-1);
        else 
            return new NumberPTC(0);
    }
    
    /**
     * Takes the absolute value of a given number.
     * @param value
     * @return 
     */
    public static NumberPTC abs(NumberPTC value){
        return new NumberPTC(Math.abs(value.getDoubleNumber()));
    }
    
    /**
     * Roounds the given value to the closest integer in the negative direction.
     * @param value
     * @return 
     */
    public static NumberPTC floor(NumberPTC value){
        return new NumberPTC(value.getIntNumber()); //el em ey oh
    }
    
    /**
     * Returns a random value using Java's Math.random(). Not accurate to original random function of PTC.
     * @param maximum
     * @return 
     */
    public static NumberPTC rnd(NumberPTC maximum){
        return new NumberPTC((int)(maximum.getIntNumber() * Math.random()));
    }
    
    /**
     * Returns a VariablePTC from the given function + arguments. Arguments should be of form arg1, arg2, ...
     * @param function
     * @param args
     * @return 
     */
    public static VariablePTC func(StringPTC function, ArrayList<VariablePTC> args){
        NumberPTC angle, value;
        
        switch (function.toString().toLowerCase()){
            case "sin":
                angle = (NumberPTC) args.get(0);
                return sin(angle);
            case "cos":
                angle = (NumberPTC) args.get(0);
                return cos(angle);
            case "tan":
                angle = (NumberPTC) args.get(0);
                return tan(angle);
            case "atan":
                angle = (NumberPTC) args.get(0);
                return atan(angle);
            case "rad":
                angle = (NumberPTC) args.get(0);
                return rad(angle);
            case "deg":
                angle = (NumberPTC) args.get(0);
                return deg(angle);
            case "log":
                value = (NumberPTC) args.get(0);
                return log(value);
            case "exp":
                value = (NumberPTC) args.get(0);
                return exp(value);
            case "pow":
                value = (NumberPTC) args.get(0);//base
                angle = (NumberPTC) args.get(1);//exponent
                return pow(value, angle);
            case "sqr":
                value = (NumberPTC) args.get(0);
                return sqr(value);
            case "abs":
                value = (NumberPTC) args.get(0);
                return abs(value);
            case "sgn":
                value = (NumberPTC) args.get(0);
                return sgn(value);
            case "rnd":
                value = (NumberPTC) args.get(0);
                return rnd(value);
            case "pi":
                return new NumberPTC((double)12867.0/4096.0);
            default: 
                return null;
        }
    }
}
