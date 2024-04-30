import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProtocoloServidor {
public static void procesar(BufferedReader pIn, PrintWriter pOut) throws IOException {
	String inputLine;
	String outputLine;
	
	inputLine = pIn.readLine(); 
	System.out.println("Entrada a procesar:"+inputLine);
	
	outputLine = inputLine;
	
	pOut.println(outputLine);
	System.out.println("Salida procesada: "+outputLine);
	
}
}
