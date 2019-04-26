package petitcomputer;

class Resources {
    public static final String PATH = "src/resource/";
    
    BGF bgfu, bgfl;
    BGU bguu, bgul;
    BGD bgd; //only use is on bottom screen
    COL col0, col1, col2; //BG+FONT, SP, GRP
    
    public Resources(Files f){
        col0 = new COL(false);
        col0.setData(f.loadColor(PATH + "COL0.PTC"));
        //col0.setDefault("src/resource/COL0.PTC");
        col0.createICM();
        
        col1 = new COL(false);
        col1.setData(f.loadColor(PATH + "COL1.PTC"));
        col1.createICM();
        
        col2 = new COL(true);
        col2.setData(f.loadColor(PATH + "COL2.PTC"));
        //col2.setDefault("src/resource/COL2.PTC");
        col2.createICM();
        
        bgfu = new BGF(col0);
        bgfu.setData(f.loadCHRBank(PATH + "BGF0.PTC"));
        //bgfu.setDefault();
        
        bgfl = new BGF(col0);
        bgfl.setData(f.loadCHRBank(PATH + "BGF0.PTC"));
        //bgfl.setDefault();
        
        bguu = new BGU(col0);
        
        bguu.setData(0, f.loadCHRBank(PATH + "BGU0.PTC"));
        bguu.setData(1, f.loadCHRBank(PATH + "BGU1.PTC"));
        bguu.setData(2, f.loadCHRBank(PATH + "BGU2.PTC"));
        bguu.setData(3, f.loadCHRBank(PATH + "BGU3.PTC"));
        //bguu.setDefault();
        
        bgd = new BGD(col0);
        bgd.setData(0, f.loadCHRBank(PATH + "BGD0.PTC"));
        bgd.setData(1, f.loadCHRBank(PATH + "BGD1.PTC"));
        //bgd.setDefault();
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
    
    /**
     * Sets data for a given resource.
     * Useful for file loading.
     * @param rString resource type string
     * @param data data to be stored
     */
    public void setResource(String rString, byte[] data){
        rString = rString.toLowerCase();
        switch (rString.substring(0, 3)){
            case "bgf":
                if (isLower(rString)) //check if lower screen data
                    bgfl.setData(data);
                else //default to upper
                    bgfu.setData(data);
                break;
        }
    }
    
    /**
     * Checks if the last characters of the string contains a "l" or not.
     * @param s
     * @return 
     */
    private boolean isLower(String s){
        return s.substring(3).contains("l");
    }
}
