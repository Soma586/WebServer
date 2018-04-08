import java.io.* ;
import java.net.* ;
import java.util.* ;

final class ProxyRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;
    String t= " ";
    
    // Constructor
    public ProxyRequest(Socket socket) throws Exception {
	this.socket = socket;
    }
    
    // Implement the run() method of the Runnable interface.
    public void run() {
	try {
	    processRequest();
	} catch (Exception e) {
	    System.out.println(e);
	}
    }

    private void processRequest() throws Exception {
	// Get a reference to the socket's input and output streams.
	InputStream is = socket.getInputStream();
	DataOutputStream os = new DataOutputStream(socket.getOutputStream());
	
	// Set up input stream filters.
	BufferedReader br = new BufferedReader(new InputStreamReader(is));

        // Get the request line of the HTTP request message.
        String requestLine = br.readLine();

        // Extract the filename from the request line.
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();  // skip over the method, which should be "GET"
        String fileName = tokens.nextToken();
	
        // Prepend a "." so that file request is within the current directory.
       // fileName = "." + fileName ;
	
	// Open the requested file.
        FileInputStream fis = null ;
        boolean fileExists = true ;
        try {
	    fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
	    fileExists = false ;
        }

	// Debug info for private use
	System.out.println("Incoming!!!");
	System.out.println(requestLine);
	String headerLine = null;
	while ((headerLine = br.readLine()).length() != 0) {
	    System.out.println(headerLine);
	}
	
	// Construct the response message.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
	    statusLine = "HTTP/1.0 200 OK" + CRLF;
	        contentTypeLine = "Content-Type: " + 
		    contentType(fileName) + CRLF;
        } else {
        	try {
        		String x = "http:/" + fileName;
        		System.out.println("THIS IS XOXOXOXO: "+ x);
        		URL url = new URL("http://lehman.edu");
        		BufferedReader inputSt = new BufferedReader(new InputStreamReader(url.openStream()));
        		 BufferedWriter writer = new BufferedWriter(new FileWriter(x +".html"));
        		 String line = "";
        		 while((line = inputSt.readLine()) != null)  {
        			 writer.write(line);
        			 writer.newLine();
        			// System.out.println(line);
        		 }
        	
        		 inputSt.close();
        		 writer.close();
        		 
        		 File folder = new File("D:\\voidspace\\Internet");

             	if(folder.isDirectory()) {
             	contentTypeLine = "Content-Type: text/html" + CRLF;
             	File[] files = folder.listFiles();
             	
             	entityBody = "<HTML>" + 
             			"<HEAD><TITLE>list</TITLE></HEAD>"
             			+ "<BODY>"+ printFiles(files)+ "</BODY></HTML>" ; 


             	}
             	
        		/* contentTypeLine = "Content-Type: text/html" + CRLF;
        		 entityBody = "<HTML>" + 
        		 "<HEAD><TITLE>sup </TITLE></HEAD>" +

        		 "<BODY>" +line+" </BODY></HTML>";
        		 //inputStream.close();*/
        		 
        		 
        	}catch(IOException e) {
        		System.out.println("IOException " + e.getMessage());
        	
	 /*   statusLine = "HTTP/1.0 404 Not Found" + CRLF;
	    contentTypeLine = "Content-Type: text/html" + CRLF;
	        entityBody = "<HTML>" + 
		    "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
		    "<BODY>"+ "noting" + "</BODY></HTML>";*/
        }
	// Send the status line.
        os.writeBytes(statusLine);

        // Send the content type line.
        os.writeBytes(contentTypeLine);

        // Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);

        // Send the entity body.
        if (fileExists) {
	    sendBytes(fis, os);
	    fis.close();
        } else {
	    os.writeBytes(entityBody) ;
        }

        // Close streams and socket.
        os.close();
        br.close();
        socket.close();
    }
    }
    private static void sendBytes(FileInputStream fis, 
				  OutputStream os) throws Exception {
	// Construct a 1K buffer to hold bytes on their way to the socket.
	byte[] buffer = new byte[1024];
	int bytes = 0;
	
	// Copy requested file into the socket's output stream.
	while ((bytes = fis.read(buffer)) != -1) {
	    os.write(buffer, 0, bytes);
	}
    }
    
    public static String printFiles(File[] files) throws Exception{

    	String s = "<p>";
    	for(File file : files) {
    	if(file.isFile()) {
    	s = s.concat("<a href= " +file.getName() +">"+ file.getName()+ "</a><br/>");
    	//s = s.concat( file.getName() );
    	}
    	s = s.concat("</p>");
    	}
    	return s;
    	}

    private static String contentType(String fileName) {

	return "text/html";
	
	/*if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
	    return "text/html";
	}
	if(fileName.endsWith(".ram") || fileName.endsWith(".ra")) {
	    return "audio/x-pn-realaudio";
	}
	return "application/octet-stream" ;
	*/
    }
}

