package agents;

import jade.core.Agent;
import behaviours.RequestPerformer;
import gui.BookBuyer;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BookBuyerAgent extends Agent {
  private String bookTitle;
  private AID[] sellerAgents;
  private int ticker_timer = 10000;
  private BookBuyerAgent this_agent = this;
  
  protected void setup() {
    System.out.println("Buyer agent " + getAID().getName() + " is ready");
    
    BookBuyer by = new BookBuyer();
    by.setVisible(true);
    
    Object[] args = getArguments();
    by.jTextField1.setText((String)args[0]);
    if(args != null && args.length > 0) {
      bookTitle = by.jTextField1.getText();
      System.out.println("Book: " + bookTitle);
      
      addBehaviour(new TickerBehaviour(this, ticker_timer) {
        protected void onTick() {
          System.out.println("Trying to buy " + bookTitle);
          
          DFAgentDescription template = new DFAgentDescription();
          ServiceDescription sd = new ServiceDescription();
          sd.setType("book-selling");
          template.addServices(sd);
          
          try {
            DFAgentDescription[] result = DFService.search(myAgent, template);
            System.out.println("Found the following seller agents:");
            sellerAgents = new AID[result.length];
            for(int i = 0; i < result.length; i++) {
              sellerAgents[i] = result[i].getName();
              System.out.println(sellerAgents[i].getName());
            }
            
          }catch(FIPAException fe) {
            fe.printStackTrace();
          }
          
          myAgent.addBehaviour(new RequestPerformer(this_agent));
        }
      });
    } else {
      System.out.println("No target book title specified");
      doDelete();
    }
  }
  
  
  protected void takeDown() {
    System.out.println("Buyer agent " + getAID().getName() + " terminating");
  }
  
  public AID[] getSellerAgents() {
    return sellerAgents;
  }
  
  public String getBookTitle() {
    return bookTitle;
  }
}
