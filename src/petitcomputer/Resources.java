package petitcomputer;

class Resources {
    BGF bgfu, bgfl;
    BGU bguu, bgul;
    BGD bgd; //only use is on bottom screen
    COL col0, col1, col2; //BG+FONT, SP, GRP
    
    public Resources(){
        col0 = new COL(false);
        col0.setDefault("src/resource/COL0.PTC");
        col0.createICM();
        
        col2 = new COL(true);
        col2.setDefault("src/resource/COL2.PTC");
        col2.createICM();
        
        bgfu = new BGF(col0);
        bgfu.setDefault();
        
        bgfl = new BGF(col0);
        bgfl.setDefault();
        
        bguu = new BGU(col0);
        bguu.setDefault();
        
        bgd = new BGD(col0);
        bgd.setDefault();
    }
    
    /**
     * Returns the upper screen font character data.
     * @return 
     */
    public BGF getBGFU(){
        return bgfu;
    }
    
    public BGF getBGFL(){
        return bgfl;
    }
    
    public BGU getBGUU(){
        return bguu;
    }
    
    public BGU getBGUL(){
        return bgul;
    }
    
    public BGD getBGD(){
        return bgd;
    }
    
    public COL getCOL0(){
        return col0;
    }
    
    public COL getCOL2(){
        return col2;
    }
}
