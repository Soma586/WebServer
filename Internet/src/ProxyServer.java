//import java.util.* ;
import java.net.ServerSocket;
import java.net.Socket;

public final class ProxyServer {
    public static void main(String argv[]) throws Exception {
	// Get the port number from the command line.
	//int port = (new Integer(argv[0])).intValue();
	int port = 999;
	
	// Establish the listen socket.
	ServerSocket socket = new ServerSocket(port);
	
	// Process HTTP service requests in an infinite loop.
	while (true) {
	    // Listen for a TCP connection request.
	    Socket connection = socket.accept();
	        
	    // Construct an object to process the HTTP request message.
	    ProxyRequest request = new ProxyRequest(connection);
	        
	    // Create a new thread to process the request.
	    Thread thread = new Thread(request);
	        
	    // Start the thread.
	    thread.start();
	}
    }
}



