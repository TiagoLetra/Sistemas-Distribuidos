package org.komparator.mediator.ws;


public class MediatorApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length == 0 || args.length == 2 ) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + MediatorApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
			return;
		}
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;

		// Create server implementation object, according to options
		MediatorEndpointManager endpoint = null;
		if (args.length == 1 || ( args.length ==4 && Integer.parseInt(args[3])>1)) {
			wsURL = args[2];
			endpoint = new MediatorEndpointManager(wsURL);
			System.out.println("Creating Secundary Server");
		} else if (args.length >= 3) {
			System.out.println("Creating Primary Server");
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			endpoint = new MediatorEndpointManager(uddiURL, wsName, wsURL);
			endpoint.setVerbose(true);

		}
			LifeProof aliveThread= new LifeProof(endpoint);
		try {
			aliveThread.start();
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			aliveThread.stop();
			endpoint.stop();
		}

	}

}
