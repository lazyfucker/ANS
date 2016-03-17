/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Agent_Management.ContainersManager;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author Matthew
 */
public class CreateBuyerAgentWindow extends Window{
   
    ContainerController myContainer;
    public  String SECONDARY_PROPERTIES_FILE = "cfg/containerServer.cfg"; 
    
    private String agentName;
    
    private JPanel panel;
    private JPanel panelName;
    private JPanel panelButtons;
    
    private JLabel nameLabel;
    private JTextField nameText;
    private JButton accept;
    
    public CreateBuyerAgentWindow (String tit, int width, int height)
	{
		super(tit, width, height);
                initiWidgets();
        }
    private void initiWidgets(){
        // Create panel for widgets and layout
		getWindow().setResizable(false);
                
                panelName = new JPanel(new FlowLayout());
                nameLabel = new JLabel("Agent Name");
                panelName.add(nameLabel);
                nameText = new JTextField("Buyer Agent");
                panelName.add(nameText);
                
                panelButtons = new JPanel(new FlowLayout());
                accept = new JButton("Accept");
                panelButtons.add(accept);
                
                panel = new JPanel(new BorderLayout());
                panel.add(panelName, BorderLayout.NORTH);
                panel.add(panelButtons, BorderLayout.SOUTH);
                getWindow().add(panel);
                
                accept.addActionListener(this);
                setFrameOptions();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(accept)){        
            agentName = nameText.getText();
		try{
                    if(!ContainersManager.getInstance().buyerContainerExists()){
                        ContainersManager.getInstance().createBuyerContainer();
                    }
                    myContainer = ContainersManager.getInstance().getBuyerContainer();
                    AgentController ac = myContainer.createNewAgent(agentName, "jade.core.Agent", null);
                    }   catch(Exception ex) {
		}
        Main_Window.buyerAgentsListModel.addElement(agentName);        
        }
    }       
}