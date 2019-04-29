package petitcomputer;

public class Main {

    public static void main(String[] args) {
        if (args.length != 0)
            Debug.setFlags(Integer.valueOf(args[0]));
        else
            Debug.setFlags(Debug.ALL);
        
        MenuGUI gui = new MenuGUI();
    }
    
    ;;;;  ;     ;;;;; ;;;;; ;;;;; ;;;;;       ;   ; ;;;;; ;;;;  ;   ; ;;;;;
    ;   ; ;     ;     ;   ; ;     ;           ;   ; ;   ; ;   ; ;  ;      ;
    ;;;;  ;     ;;;   ;;;;; ;;;;; ;;;         ; ; ; ;   ; ;;;;  ;;;    ;;;;
    ;     ;     ;     ;   ;     ; ;           ; ; ; ;   ; ;   ; ;  ;       
    ;     ;;;;; ;;;;; ;   ; ;;;;; ;;;;;       ;;;;; ;;;;; ;   ; ;   ;  ;;  
}

