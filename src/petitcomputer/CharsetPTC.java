package petitcomputer;

import java.awt.Image;

/**
 * Character sets of PTC. BG tiles, fonts, panel, etc.
 * @author minxr
 */
public class CharsetPTC {
    CHRBank[] banks;
    
    public CharsetPTC(COL col, int numBanks){
        banks = new CHRBank[numBanks];
        
        for (int i = 0; i < banks.length; i++)
            banks[i] = new CHRBank(col);
    }
    
    public Image getImage(int index, byte palette){
        return banks[index / 256].getImage(index % 256, palette);
    }
    
    public CHR getCharacter(int index){
        return banks[index / 256].getCharacter(index % 256);
    }
    
    public void setData(int bank, byte[] data){
        banks[bank].setBankData(data);
    }
}
