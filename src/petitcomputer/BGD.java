package petitcomputer;

import java.awt.Image;
import java.util.Arrays;

/**
 * Limited use BGD class - essentially a panel-exclusive character set...
 * @author minxr
 */
public class BGD implements CharsetPTC{
    CHRBank[] bgd;
    
    public BGD(COL col){
        bgd = new CHRBank[2];
        
        for (int i = 0; i < bgd.length; i++)
            bgd[i] = new CHRBank(col);

    }
    
    /**
     * Gets a character from the given index.
     * @param index
     * @return 
     */
    @Override
    public CHR getCharacter(int index){
        return bgd[index / 256].getCharacter(index % 256);
    }
    
    /**
     * Generates an image from the given index and palette.
     * @param index
     * @param palette
     * @return 
     */
    @Override
    public Image getImage(int index, byte palette){
        return bgd[index / 256].getImage(index % 256, palette);
    }
    
    /**
     * Loads the default character set into the character banks.
     */
    public void setDefault(){
        //bgd[0].loadFromFile("src/resource/BGD0.PTC"); //UPDATE LATER!!!
        //bgd[1].loadFromFile("src/resource/BGD1.PTC");
    }
    
    /**
     * Sets the data of the character bank.
     * @param bank
     * @param data 
     */
    public void setData(int bank, byte[] data){
        bgd[bank].setBankData(data);
    }
}
