/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petitcomputer;

import java.awt.Image;

/**
 *
 * @author minxr
 */
public class SPU implements CharsetPTC {
    CHRBank[] spu;
    
    public SPU(COL col){
        spu = new CHRBank[8];
        
        for (int i = 0; i < spu.length; i++)
            spu[i] = new CHRBank(col);
    }
    
    @Override
    public Image getImage(int index, byte palette) {
        return spu[index / 256].getImage(index % 256, palette);
    }

    @Override
    public CHR getCharacter(int index) {
        return spu[index / 256].getCharacter(index % 256);
    }
    
    public void setData(int bank, byte[] data){
        spu[bank].setBankData(data);
    }
}
