package petitcomputer;

import java.awt.Image;
import java.util.Arrays;

/**
 * Class that contains data for BGU characters.
 * @author minxr
 */
public class BGU implements CharsetPTC {
    CHRBank[] bgu;
    
    public BGU(COL col){
        bgu = new CHRBank[4];
        
        for (int i = 0; i < bgu.length; i++)
            bgu[i] = new CHRBank(col);

    }
    
    /**
     * Gets a character from the given index.
     * @param index
     * @return 
     */
    @Override
    public CHR getCharacter(int index){
        return bgu[index / 256].getCharacter(index % 256);
    }
    
    /**
     * Generates an image from the given index and palette.
     * @param index
     * @param palette
     * @return image of character 
     */
    @Override
    public Image getImage(int index, byte palette){
        return bgu[index / 256].getImage(index % 256, palette);
    }
    
    /**
     * Loads the default character set into the character banks.
     */
    public void setDefault(){
        //bgu[0].loadFromFile("src/resource/BGU0.PTC");
        //bgu[1].loadFromFile("src/resource/BGU1.PTC");
        //bgu[2].loadFromFile("src/resource/BGU0.PTC");
        //bgu[3].loadFromFile("src/resource/BGU1.PTC");        
    }
    
    /**
     * Sets the graphic data for the given bank.
     * @param bank character bank index
     * @param data byte array of data
     */
    public void setData(int bank, byte[] data){
        bgu[bank].setBankData(data);
    }
}
