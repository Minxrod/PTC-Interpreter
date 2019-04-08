package petitcomputer;

public enum Errors {
    
    SYNTAX_ERROR,
    OUT_OF_RANGE,
    OUT_OF_MEMORY,
    UNDEFINED_LABEL,
    NEXT_WITHOUT_FOR,
    OUT_OF_DATA,
    ILLEGAL_FUNCTION_CALL,
    DUPLICATE_DEFINITION,
    CAN_NOT_CONTINUE,
    MISSING_OPERAND,
    DUPLICATE_LABEL,
    ILLEGAL_RESOURCE_TYPE,
    ILLEGAL_CHARACTER_TYPE,
    STRING_TOO_LONG,
    DIVIDE_BY_0,
    OVERFLOW,
    SUBSCRIPT_OUT_OF_RANGE,
    TYPE_MISMATCH,
    FORMULA_TOO_COMPLEX,
    RETURN_WITHOUT_GOSUB,
    FOR_WITHOUT_NEXT,
    ILLEGAL_MML, 
    /**
     * Used when error type is unknown as a default error type.
     */
    UNDEFINED_ERROR
    
}
