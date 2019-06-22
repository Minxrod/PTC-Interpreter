package petitcomputer;

import java.awt.Image;

/**
 * This class is the font of PTC.
 * @author minxr
 */
public class BGF implements CharsetPTC{
    CHRBank characters;
    
    /**
     * Initializes font data.
     * @param col
     */
    public BGF(COL col){
        characters = new CHRBank(col);        
    }
    
    /**
     * Returns the CHR of a given index.
     * @param index
     * @return 
     */
    @Override
    public CHR getCharacter(int index){
        return characters.getCharacter(index);
    }
    
    /**
     * Gets the character image of a given CHR. 
     * @param index
     * @param palette
     * @return 
     */
    @Override
    public Image getImage(int index, byte palette){
        return characters.getImage(index, palette);
    }
    
    /**
     * Attempts to read the default font from a file.
     */
    public void setDefault(){
        //characters.loadFromFile("src/resource/BGF0.PTC");
    }

    public void setData(byte[] data){
        characters.setBankData(data);
    }
    
    /**
     * No longer supported method to create the character image and return the newly created image. Was very inefficient as it would recreate the same image on most calls.
     * @param index
     * @param palette
     * @return 
     */
    public Image getChrImage(int index, byte palette) {
        throw new UnsupportedOperationException("No longer supported."); //To change body of generated methods, choose Tools | Templates.
    }
}
