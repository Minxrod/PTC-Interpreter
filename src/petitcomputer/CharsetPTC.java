package petitcomputer;

import java.awt.Image;

/**
 * Character sets of PTC. BG tiles, fonts, panel, etc.
 * @author minxr
 */
public interface CharsetPTC {
    
    public Image getImage(int index, byte palette);
    public CHR getCharacter(int index);
    //public Image getChrImage(int index, byte palette);
    //public void setDefault();
}
