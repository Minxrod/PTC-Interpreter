package petitcomputer;

import java.awt.*;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * Display and input handling.
 * @author minxr
 */
public class MenuGUI {
    final String LOAD_RUN = "Open File";
        
    JFrame frame;
    JFileChooser files;
    JLabel egg;
    JPanel total;
    JButton load;
    
    boolean pleaseLoad = false;
    boolean pleaseRun = false;
    
    public MenuGUI(){
        frame = new JFrame("PTC interpreter[?]");

        total = new JPanel(new GridLayout(2,1));
        
        files = new JFileChooser();
        
        load = new JButton(LOAD_RUN);
        load.setActionCommand(LOAD_RUN);
        load.addActionListener(new ButtonListener());
        
        egg = new JLabel("WIP Interpreter/Emulator using Java Swing"); //egg
        
        //total.add(file);
        total.add(load);
        //total.addKeyListener(new KeyboardListener());
        //total.setFocusable(true);
        total.add(egg); //egg
        
        frame.add(total);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setAlwaysOnTop(true); //useful for testing
    }
    
    private class ButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = e.getActionCommand();
            
            if (name.equals(LOAD_RUN)){
                int c = files.showOpenDialog(frame);
                
                if (c == JFileChooser.APPROVE_OPTION){
                    Debug.print(Debug.GUI_FLAG, files.getSelectedFile().toString());
                    PetitComGUI ptcgui = new PetitComGUI(files.getSelectedFile());
                    
                    Thread ptc = new Thread(ptcgui);
                    ptcgui.start();
                    ptc.start();
                    
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MenuGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //ptcgui.getProcess().setFile(files.getSelectedFile()); //fix later, it works for now
                    

                    
                }
            }
        }
    }
}