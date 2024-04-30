import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

public class ProtocoloServidor {
public static void procesar(BufferedReader pIn, PrintWriter pOut, PrivateKey kPrivada) throws IOException {
	String inputLine;
	String outputLine;
	
	inputLine = pIn.readLine(); 
	System.out.println("Entrada a procesar:"+inputLine);
	String[] mensajes = inputLine.split(",");
	
	outputLine = new String(CifradoAsimetrico.cifrar(kPrivada, "RSA" ,mensajes[1]),StandardCharsets.UTF_8);
	
	pOut.println(outputLine);
	System.out.println("Salida procesada: "+outputLine);
	
	inputLine = pIn.readLine();
	
	if (inputLine.equals("ERROR")) {
		return;
	}
	//else ok
	System.out.println(inputLine);
	
	}
}

