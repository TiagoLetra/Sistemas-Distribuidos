package org.komparator.mediator.ws;
import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.domain.Mediator;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import javax.xml.ws.WebServiceException;


public class LifeProof extends Thread{


	String result;
    Exception exception;
    MediatorEndpointManager argument;
    private int narg =1;
    private static final String UDDI_URL = "http://localhost:9090";
    private static final String WS_NAME = "T59_Mediator";
    public static final int SECONDS = MediatorClient.INTERVAL;

    public LifeProof(MediatorEndpointManager argument) {
        this.argument = argument;
    }

    public Object getResult() {
        synchronized(this) {
            return this.result;
        }
    }

    public Exception getException() {
        synchronized(this) {
            return this.exception;
        }
    }

    public void run() {
        try {
        	System.out.println(this.getClass() + " running...");  
        	int i;
        	while(true){
        		synchronized(this) {
            		if(argument.getWsName()!=null){
            					i=narg;
            					while(true){
            						try{
            						MediatorClient mc= new MediatorClient("http://localhost:807"+(i+1)+"/mediator-ws/endpoint");
            						mc.imAlive();
            						i++;
            						}catch(WebServiceException x){
            							break;
            						}

            		}
            		}else{
            			String wsurl = null;
            			try{
            				UDDINaming uddi = new UDDINaming("http://localhost:9090");
							wsurl = uddi.lookup(WS_NAME);
						}catch(Exception x){
							System.out.println("Failed to access UDDI");
						}	
						if(wsurl!=null){
							while(true){
								if(wsurl.compareTo("http://localhost:807"+narg+"/mediator-ws/endpoint")==0)
									break;
								narg=narg+1;
							}
						}
            			Mediator mediator=Mediator.getInstance();
            			System.out.println(this.getClass() + "checking imAlive");
            			if(mediator.checkTime()){
							System.out.println("Primary server died looking for new Primary");
            				narg=narg+1;
            				argument.publishToUDDI(UDDI_URL,WS_NAME,"http://localhost:807"+narg+"/mediator-ws/endpoint");
            				if(argument.getWsName()!=null){
            					System.out.println("I am the new Primary Server");
            					i=narg;
            					while(true){
            						try{
            						MediatorClient mc= new MediatorClient("http://localhost:807"+(i+1)+"/mediator-ws/endpoint");
            						mc.imAlive();
            						i++;
            						}catch(WebServiceException x){
            							break;
            						}

            					}
            				}
						}
					}
            		this.notifyAll();
            		sleep(SECONDS*1000);
        		}
    		}

        } catch (Exception e) {
            System.out.println(this.getClass() + " caught exception: " + e.toString());
            this.exception = e;

        } finally {
            System.out.println(this.getClass() + " stopping.");
        }
    }

}
