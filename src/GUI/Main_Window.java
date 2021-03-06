package GUI;

import Agent_Management.ContainersManager;
import jade.wrapper.ContainerController;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.WindowConstants;

/**
 *
 * @author Mateusz Kusaj
 */
public class Main_Window extends Window{
    
    private String selectedAgent;
    
    private JPanel mainPanel;
    private JPanel westPanel;
    private JPanel eastPanel;
    private JPanel southPanel;
    
    private JLabel fillerLabel;
    private JButton runSimulation;
    private JButton newBuyerAgent;
    private JButton newSellerAgent;
    private JButton deleteAgent;

    private JList<String> buyerAgentsList;
    public static DefaultListModel buyerAgentsListModel;
    private JList<String> sellerAgentsList;
    public static DefaultListModel sellerAgentsListModel;
    
    public Main_Window(String tit, int width, int height)
	{
		super(tit, width, height);
		ContainersManager.getInstance().startRMA();
                initiWidgets();
	}
    private void initiWidgets(){
        // Create panel for widgets and layout
		getWindow().setResizable(false);
		getWindow().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                
		westPanel = new JPanel(new FlowLayout());
                buyerAgentsList = new JList<>();
                westPanel.add(buyerAgentsList);
                
                
                eastPanel = new JPanel(new FlowLayout());
                sellerAgentsList = new JList<>();
                eastPanel.add(sellerAgentsList);
                
                southPanel = new JPanel(new FlowLayout());
                runSimulation = new JButton("Run Simulation");
                southPanel.add(runSimulation);
                fillerLabel = new JLabel("        ");
                southPanel.add(fillerLabel);
                newBuyerAgent = new JButton("Create Buyer Agent");
                southPanel.add(newBuyerAgent);
                newSellerAgent = new JButton("Create Seller Agent");
                southPanel.add(newSellerAgent);
                deleteAgent = new JButton("Delete Agent");
                southPanel.add(deleteAgent);
                
                
                
                mainPanel = new JPanel(new BorderLayout());
                mainPanel.add(westPanel, BorderLayout.WEST);
                mainPanel.add(eastPanel, BorderLayout.EAST);
                mainPanel.add(southPanel, BorderLayout.SOUTH);
                getWindow().add(mainPanel);
                
		newBuyerAgent.addActionListener(this);
                newSellerAgent.addActionListener(this);
                runSimulation.addActionListener(this);
                deleteAgent.addActionListener(this);
                
                buyerAgentsListModel = new DefaultListModel();
                buyerAgentsList.setModel(buyerAgentsListModel);
                
                sellerAgentsListModel = new DefaultListModel();
                sellerAgentsList.setModel(sellerAgentsListModel);
                setFrameOptions();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(newBuyerAgent)){
            // Opens new window that can be used to create new Buyer Agent
            new CreateBuyerAgentWindow("Create New Buyer Agent", 800, 800);
        }
        if(e.getSource().equals(newSellerAgent)){
            //Opens new window that can be used to create new Seller Agent
            new CreateSellerAgentWindow("Create New Seller Agent", 800, 800);
        }
        if(e.getSource().equals(runSimulation)){
            //Runs the similation with selected Buyer agent
            selectedAgent = buyerAgentsList.getSelectedValue();
            ContainerController myContainer = ContainersManager.getInstance().getBuyerContainer();
            try {
                AgentController ac = myContainer.getAgent(selectedAgent + "@192.168.0.13:1099/JADE", true);
                ac.activate();
            } catch (ControllerException ex) {
                Logger.getLogger(Main_Window.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }
        if(e.getSource().equals(deleteAgent)){
            //Removes selected agent from the list and from the container
          if (buyerAgentsList.getSelectedValue() != null){
              selectedAgent = buyerAgentsList.getSelectedValue();
              ContainerController myContainer = ContainersManager.getInstance().getBuyerContainer();
            try {
                AgentController ac = myContainer.getAgent(selectedAgent + "@192.168.0.13:1099/JADE", true);
                ac.kill();
            } catch (ControllerException ex) {
                Logger.getLogger(Main_Window.class.getName()).log(Level.SEVERE, null, ex);
            }
            buyerAgentsListModel.remove(buyerAgentsList.getSelectedIndex());
          }if(sellerAgentsList.getSelectedValue() != null){
                   selectedAgent = sellerAgentsList.getSelectedValue();
              ContainerController myContainer = ContainersManager.getInstance().getSellerContainer();
            try {
                AgentController ac = myContainer.getAgent(selectedAgent + "@192.168.0.13:1099/JADE", true);
                ac.kill();
            } catch (ControllerException ex) {
                Logger.getLogger(Main_Window.class.getName()).log(Level.SEVERE, null, ex);
            }
            sellerAgentsListModel.remove(sellerAgentsList.getSelectedIndex());
              
          }
        }
    }
    
    public static void main(String args[])
	{
		new Main_Window("Agent Negotiations Simulator", 800, 800);
                
	}

    
}
