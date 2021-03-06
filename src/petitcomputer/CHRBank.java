package petitcomputer;

import java.awt.Image;

/**
 * Character bank of PTC 8x8 characters.
 * @author minxr
 */
public class CHRBank {
    final int size;
    CHR[] characters;
    COL colors;
    
    public CHRBank(COL col){
        size = 256;
        colors = col;
        characters = new CHR[size];
        
        for (int i = 0; i < size; i++)
            characters[i] = new CHR();
    }
    
    /*public void loadFromFile(String filename){
        try {
            File file = new File(filename);
            if (!file.exists())
                System.err.println("ERROR: " + file.getName() + " not found.");
            
            FileInputStream in;
            in = new FileInputStream(file);
            byte[] header = new byte[48];
            in.read(header);
            
            byte[] tempBytes;
            for (int i = 0; i < size; i++){
                tempBytes = new byte[32];
                in.read(tempBytes);

                for (int j = 0; j < 32; j++)
                    tempBytes[j] = (byte) (((tempBytes[j] & 0x0F) << 4) | ((tempBytes[j] & 0xF0) >> 4));

                characters[i].setData(tempBytes);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BGF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BGF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    /**
     * Sets the data for an entire bank of characters.
     * The data is assumed to have been loaded from a file.
     * @param data - byte array of character data 
     */
    public void setBankData(byte[] data){
        for (int i = 0; i < size; i++){
            byte[] tempBytes = new byte[32];
            
            System.arraycopy(data, tempBytes.length * i, tempBytes, 0, tempBytes.length);
            
            characters[i].setData(tempBytes);
        }
    }
    
    public void setCharacterData(int index, byte[] data){
        characters[index].setData(data);
    }
    
    public CHR getCharacter(int value){
        return characters[value];
    }
    
    public Image getImage(int index, byte palette){
        return characters[index].getImage(colors, palette);
        
        //old check from when it returned null
        //if (i == null)
        //    i = characters[index].createImage(colors, palette);
            
        //return i;
    }
    
    //obsolete
    /*public Image getChrImage(int index, byte palette){
        return characters[index].createImage(colors, palette);
    }*/
    
    public int getSize(){
        return size;
    }
}
