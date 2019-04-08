package petitcomputer;

/**
 * Simply a class to hold some character constants and perform character checks according to PTC standard.
 * @author minxr
 */
public class CharacterPTC {
    
    public enum Char{
        NULL,
        BUTTON_A,
        BUTTON_B,
        BRICK,
        SHIP_1,
        SHIP_2,
        ALIEN,
        FACE_CLEAR,
        FACE_SOLID,
        TAB,
        STAR,
        HAND,
        BUTTON_L,
        LINEBREAK,
        ALIEN_SHIP_1,
        ALIEN_SHIP_2, //15
        
        MUSIC_SINGLE,
        MUSIC_DOUBLE,
        BUTTON_R,
        BUTTON_PAD,
        CLOCK,
        DOT_CORNER,
        DOT_TOP,
        DOT_SIDE,
        BUTTON_X,
        BUTTON_Y,
        UHH, //what is this again?
        SNAKE,
        RIGHT,
        LEFT,
        UP,
        DOWN, //31
        
        SPACE,
        EXCLAMATION,
        QUOTE,
        NUMBER,
        DOLLAR,
        PERCENT,
        AMPERSAND,
        APOSTROPHE,
        OPEN_PAREN,
        CLOSE_PAREN,
        ASTERISK,
        PLUS,
        COMMA,
        DASH,
        PERIOD,
        SLASH, //47
        
        NUM_0,
        NUM_1,
        NUM_2,
        NUM_3,
        NUM_4,
        NUM_5,
        NUM_6,
        NUM_7,
        NUM_8,
        NUM_9,
        COLON,
        SEMICOLON,
        LESS_THAN,
        EQUALS,
        GREATER_THAN,
        QUESTION, //63

        AT,
        A,
        B,
        C,
        D,
        E,
        F,
        G,
        H,
        I,
        J,
        K,
        L,
        M,
        N,
        O, //79
        
        P,
        Q,
        R,
        S,
        T,
        U,
        V,
        W,
        X,
        Y,
        Z,
        OPEN_BRAKCET,
        YEN,
        CLOSE_BRACKET,
        POWER,
        UNDERSCORE, //95
        
        ACCENT, //maybe??
        LOWER_A,
        LOWER_B,
        LOWER_C,
        LOWER_D,
        LOWER_E,
        LOWER_F,
        LOWER_G,
        LOWER_H,
        LOWER_I,
        LOWER_J,
        LOWER_K,
        LOWER_L,
        LOWER_M,
        LOWER_N,
        LOWER_O, //111
        
        LOWER_P,
        LOWER_Q,
        LOWER_R,
        LOWER_S,
        LOWER_T,
        LOWER_U,
        LOWER_V,
        LOWER_W,
        LOWER_X,
        LOWER_Y,
        LOWER_Z,
        OPEN_CURLY_BRACE,
        VERTICAL_BAR,
        CLOSE_CURLY_BRACE,
        FLOAT_TILDE,
        BACKSLASH, //127
        
        DIAMOND_CLEAR,
        BLOCK_1,
        BLOCK_2,
        BLOCK_3,
        BLOCK_4,
        BLOCK_5,
        BLOCK_6,
        BLOCK_7,
        BLOCK_8,
        BLOCK_9,
        BLOCK_A,
        BLOCK_B,
        BLOCK_C,
        BLOCK_D,
        BLOCK_E,
        BLOCK_F, //143
        
        PIPE_3_UP,
        PIPE_3_DOWN,
        PIPE_3_RIGHT,
        PIPE_4,
        PIPE_3_LEFT,
        PIPE_2_HORIZ,
        PIPE_2_VERT,
        WHITE_SQUARE,
        PIPE_2_DR,
        PIPE_2_DL,
        PIPE_2_UR,
        PIPE_2_UL,
        TRIANGLE_1,
        TRIANGLE_2,
        TRIANGLE_3,
        TRIANGLE_4, //159
        
        KANA_TILDE,
        KANA_STOP,
        KANA_CORNER_OPEN,
        KANA_CORNER_CLOSE,
        KANA_COMMA,
        KANA_DOT,
        KANA_WO,
        KANA_SMALL_A,
        KANA_SMALL_I,
        KANA_SMALL_U,
        KANA_SMALL_E,
        KANA_SMALL_O,
        KANA_SMALL_YA,
        KANA_SMALL_YU,
        KANA_SMALL_YO,
        KANA_SMALL_TSU, //175
        
        KANA_EXTEND,
        KANA_A,
        KANA_I,
        KANA_U,
        KANA_E,
        KANA_O,
        KANA_KA,
        KANA_KI,
        KANA_KU,
        KANA_KE,
        KANA_KO,
        KANA_SA,
        KANA_SHI,
        KANA_SU,
        KANA_SE,
        KANA_SO, //191
        
        KANA_TA,
        KANA_CHI,
        KANA_TSU,
        KANA_TE,
        KANA_TO,
        KANA_NA,
        KANA_NI,
        KANA_NU,
        KANA_NE,
        KANA_NO,
        KANA_HA,
        KANA_HI,
        KANA_FU,
        KANA_HE,
        KANA_HO,
        KANA_MA, //207
        
        KANA_MI,
        KANA_MU,
        KANA_ME,
        KANA_MO,
        KANA_YA,
        KANA_YU,
        KANA_YO,
        KANA_RA,
        KANA_RI,
        KANA_RU,
        KANA_RE,
        KANA_RO,
        KANA_WA,
        KANA_N,
        KANA_DAKUTEN,
        KANA_HANDAKUTEN, // 223
        
        SQUARE_SOLID,
        CIRCLE_SOLID,
        TRIANGLE_UP_SOLID,
        TRIANGLE_DOWN_SOLID,
        SQUARE_CLEAR,
        CIRCLE_CLEAR,
        TRIANGLE_UP_CLEAR,
        TRIANGLE_DOWN_CLEAR,
        HOUSE,
        APPLE,
        DOOR,
        KEY,
        CAR_UP,
        CAR_RIGHT,
        CAR_DOWN,
        CAR_LEFT, //249
        
        SPADE,
        HEART,
        DIAMOND,
        CLUB,
        HUMAN_UP,
        HUMAN_RIGHT,
        HUMAN_DOWN,
        HUMAN_LEFT,
        BORDER_TOP,
        BORDER_LEFT,
        BORDER_RIGHT,
        BORDER_BOTTOM,
        BORDER_DIAGONAL_UP,
        BORDER_DIAGONAL_DOWN,
        BORDER_CROSS,
        GRAY; //255
        
        private final byte idx;
        private final StringPTC str;
        
        Char(){
            idx = (byte) this.ordinal();
            str = new StringPTC(0);
            str.setString(new byte[]{idx});
        }
        
        public byte getIndex(){
            return idx;
        }
        
        public StringPTC getStringPTC(){
            return str;
        }
    }
    
    //useless but makes readable in like one location
    public static final byte PLUS = 0x2B;
    public static final byte ASTERISK = 0x2A;
    public static final byte EXCLAMATION = 0x21;
    public static final byte SEMICOLON = 0x3B;
    public static final byte LESS_THAN = 0x3C;
    public static final byte GREATER_THAN = 0x3E;
    public static final byte APOSTROPHE = 0x27;
    public static final byte SLASH = 0x2F;
    
    //broad ranges of characters
    public static final byte LETTER_UPPER_START = 0x41;
    public static final byte LETTER_UPPER_END = 0x5a;
    public static final byte LETTER_LOWER_START = 0x61;
    public static final byte LETTER_LOWER_END = 0x7a;
    public static final byte NUMBER_START = 0x30;
    public static final byte NUMBER_END = 0x39;
    public static final byte KATAKANA_START = (byte) 0xa0; //needs to be cast because signed bytes or something. Should work.
    public static final byte KATAKANA_END = (byte) 223; //these don't even matter tbh I don't know why I have these constants
    
    //kana consts
    public static final byte KANA_A = 0;
    public static final byte KANA_I = 1;
    public static final byte KANA_U = 2;
    public static final byte KANA_E = 3;
    public static final byte KANA_O = 4;
    
    public static final byte KANA_K = 5;
    public static final byte KANA_S = 10;
    public static final byte KANA_T = 15;
    public static final byte KANA_N = 20;
    public static final byte KANA_H = 25;
    public static final byte KANA_M = 30;
    public static final byte KANA_Y = 35;
    public static final byte KANA_R = 38;
    public static final byte KANA_W = 41;
    public static final byte KANA_NM= 42;
            
    
    //command separators
    public static final byte COLON = 58;
    public static final byte LINEBREAK = 13;
    
    //useful
    public static final byte COMMA = 44;
    public static final byte DASH = 45;
    public static final byte PERIOD = 46;
    
    //important
    public static final byte COMMENT = 39;
    public static final byte LABEL = 64;
    public static final byte SPACE = 32;
    public static final byte QUOTE = 34;
    public static final byte EQUALS = 61;
    public static final byte DOLLAR = 36;
    
    //idk, technically usable in var names and similar
    public static final byte UNDERSCORE = 95;
    
    //parens + similar
    public static final byte OPEN_PARENTHESIS = 40;
    public static final byte CLOSE_PARENTHESIS = 41;
    
    public static final byte OPEN_SQUARE_BRACKET = 91;
    public static final byte CLOSE_SQUARE_BRACKET = 93;

    /**
     * Method returns true if the given character is a letter.
     * @param character
     * @return 
     */
    public static boolean isLetter(byte character){
        boolean isLetter;
        isLetter = (character >= LETTER_UPPER_START) & (character <= LETTER_UPPER_END);
        isLetter |= (character >= LETTER_LOWER_START) & (character <= LETTER_LOWER_END);
        return isLetter;
    }
    
    /**
     * for variable and file names. All valid characters.
     * @param character
     * @return 
     */
    public static boolean isName(byte character){
        boolean isValidName = false;
        
        isValidName |= isLetter(character);
        isValidName |= isNumber(character);
        isValidName |= (character == UNDERSCORE);
        
        return isValidName;
    }
    
    /**
     * Method returns true if the given character is a number.
     * @param character
     * @return 
     */
    public static boolean isNumber(byte character){
        boolean isNumber;
        isNumber = (character >= NUMBER_START) & (character <= NUMBER_END);
        return isNumber;
    }
    
    /**
     * You can figure this one out. I believe in you!
     * @param character
     * @return 
     */
    public static boolean isDash(byte character){
        return character == DASH;
    }
    
    /**
     * Method to check if the given character is a symbol: not a letter or number.
     * @param character
     * @return 
     */
    public static boolean isSymbol(byte character){
        boolean isSymbol;
        isSymbol = !isNumber(character) & !isLetter(character);
        isSymbol &= !isReturn(character);
        isSymbol &= character != SPACE;
        return isSymbol;
    }
    
    /**
     * Method to check if given character is an end-of-command character: a line break or a colon.
     * @param character
     * @return 
     */
    public static boolean isReturn(byte character){
        boolean isEndOfLine = false;
        
        isEndOfLine |= character == LINEBREAK;
        isEndOfLine |= character == COLON;
        
        return isEndOfLine;
    }
    
    /**
     * Checks for end-of-item characters, such as those used to separate arguments or 
     * @param character
     * @return 
     */
    public static boolean isSeparator(byte character){
        boolean isSeparator = false;
        
        isSeparator |= character == COMMA;
        isSeparator |= character == CLOSE_PARENTHESIS;
        isSeparator |= character == LINEBREAK;
        
        return isSeparator;
    }
    
    /**
     * Checks if the character is an opening parenthesis or opening square bracket.
     * @param character
     * @return 
     */
    public static boolean isOpener(byte character){
        boolean isOpener = false;
        
        isOpener |= character == OPEN_PARENTHESIS;
        isOpener |= character == OPEN_SQUARE_BRACKET;
        
        return isOpener;
    }
    
    /**
     * Checks if the character is a closing parenthesis or bracket.
     * @param character
     * @return 
     */
    public static boolean isCloser(byte character){
        boolean isCloser = false;
        
        isCloser |= character == CLOSE_PARENTHESIS;
        isCloser |= character == CLOSE_SQUARE_BRACKET;
        
        return isCloser;
    }
    
    /**
     * Checks if character is a paren or square paren of any kind.
     * @param character
     * @return 
     */
    public static boolean isContainer(byte character){
        boolean isContainer;
        isContainer = isCloser(character) || isOpener(character);
        return isContainer;
    }
    
    public static boolean isPadding(byte character){
        boolean isStart = false;
            
        //if character is any of the following, the argument or command has not started
        isStart |= (character == CharacterPTC.SPACE); //if character is SPACE then arg has not started
        isStart |= (character == CharacterPTC.LINEBREAK); //character == LINEBREAK
        isStart |= (character == CharacterPTC.COLON); //character == COLON (treated as a new line)
        //isStart &= !(character == (byte)40); //character == OPEN_PAREN
        //isStart &= !(character == (byte)34); //character == QUOTE
        
        return isStart;      
    }
}
