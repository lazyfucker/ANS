/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agent_Management;

import GUI.SellerItemsGui;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;

public class SellerAgent extends Agent {
    
        private static final SellerAgent INSTANCE = new SellerAgent();
	// The catalogue of books for sale (maps the title of a book to its price)
	private Hashtable catalogue;
        private ArrayList<String> parameters;
	// The GUI by means of which the user can add books in the catalogue
	private SellerItemsGui myGui;
        // The amount that will be substracted from the price if the proposal is not accepted
        private String loweringNumberS;
        private int loweringNumberI;
        private String newPriceString;
        private int newPriceInt;
        public SellerAgent myAgent;
        private int valueStart;
        private int valueEnd;

        
	// Put agent initializations here
        @Override
	protected void setup() {
		// Create the catalogue
		catalogue = new Hashtable();
                //get the value that will be used to lower the price
                Object[] args= getArguments();
                loweringNumberS = (String) args[0];
                loweringNumberI = Integer.valueOf(loweringNumberS);
		// Create and show the GUI 
		myGui = new SellerItemsGui("Create new Iem", 500, 140, this);
		//myGui.showGui();

		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("book-selling");
		sd.setName("JADE-book-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
		}

		// Add the behaviour serving queries from buyer agents
		addBehaviour(new OfferRequestsServer());

		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServer());
	}

	// Put agent clean-up operations here
        @Override
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
		}
		// Close the GUI
		//myGui.dispose();
		// Printout a dismissal message
		System.out.println("Seller-agent "+getAID().getName()+" terminating.");
	}

	/**
     This is invoked by the GUI when the user adds a new book for sale
     * @param title
     * @param parameters
	 */
	public void updateCatalogue(final String title, final ArrayList<String> parameters) {
		addBehaviour(new OneShotBehaviour() {
                        @Override
			public void action() {
				catalogue.put(title, parameters);
				System.out.println(title+" inserted into catalogue by " + getAID().getName() + ". Utility = "+parameters.get(0).substring(2, parameters.get(0).length() - 2) + " Current Price = "+ parameters.get(1).substring(2, parameters.get(1).length() - 2));
			}
		} );
	}

	/**
	   Inner class OfferRequestsServer.
	   This is the behaviour used by Book-seller agents to serve incoming requests 
	   for offer from buyer agents.
	   If the requested book is in the local catalogue the seller agent replies 
	   with a PROPOSE message specifying the price. Otherwise a REFUSE message is
	   sent back.
	 */
	private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();

                                if(catalogue.get(title) != null){
				String parametersString = catalogue.get(title).toString();
                                System.out.println(parametersString);
                                valueStart = parametersString.indexOf("$v");
                                valueEnd = parametersString.indexOf("v$");
                                String newparam = parametersString.substring(valueStart + 2, valueEnd);
                                Integer price = Integer.valueOf(newparam);
//				if (price != null) {
					// The requested book is available for sale. Reply with the price
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(String.valueOf(price));
                                        //catalogue.remove(title);
                                        newPriceInt = price - loweringNumberI;
                                        newPriceString = String.valueOf(newPriceInt);
                                        parameters = SellerItemsGui.getProperties();
                                        parameters.set(1, "$v" + newPriceString + "v$");
                                        catalogue.put(title, parameters);
                                        price = newPriceInt;
                                        System.out.println("The price of the book " + title + " selling by " + getAID().getName() + "has been lowered by "+ loweringNumberI +" to a new price which is " + price);
				}
				else {
					// The requested book is NOT available for sale.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer

	/**
	   Inner class PurchaseOrdersServer.
	   This is the behaviour used by Book-seller agents to serve incoming 
	   offer acceptances (i.e. purchase orders) from buyer agents.
	   The seller agent removes the purchased book from its catalogue 
	   and replies with an INFORM message to notify the buyer that the
	   purchase has been sucesfully completed.
	 */
	private class PurchaseOrdersServer extends CyclicBehaviour {
                @Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();

				Integer price = (Integer) catalogue.remove(title);
				if (price != null) {
					reply.setPerformative(ACLMessage.INFORM);
					System.out.println(title+" sold to agent "+msg.getSender().getName());
				}
				else {
					// The requested book has been sold to another buyer in the meanwhile .
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer
        public static SellerAgent getInstance() {
        return INSTANCE;
        }
        public SellerAgent getMyAgent(){
        return myAgent;
        }
}
