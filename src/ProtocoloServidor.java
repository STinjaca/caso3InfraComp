import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class ProtocoloServidor {
public static void procesar(BufferedReader pIn, PrintWriter pOut, PrivateKey kPrivada, PublicKey kPublica) throws IOException {
	String inputLine;
	String outputLine;	
	
	
	// SIMULACIÖN ESTABLECER LLAVES INICIALES
	//2
	inputLine = pIn.readLine(); 
	System.out.println("LLegada del cliente: " + inputLine);
	
	// 3
	System.out.println("PUBLIC_KEY");
	pOut.println("PUBLIC_KEY");
	
	System.out.println(kPublica.toString());
	String[] kPublicaString = kPublica.toString().strip().split("\n");
	String modulus = kPublicaString[2].split(":")[1].strip();
	String publicExponent = kPublicaString[3].split(":")[1].strip();
	
	// 4
	pOut.println(modulus);
	pOut.println(publicExponent);
	
	// INICIO DE COMUNICACIÖN
	// Recibir reto
	inputLine = pIn.readLine(); 
	System.out.println("Entrada a procesar:"+inputLine);
	String[] mensajes = inputLine.split(",");
	byte[] mensajesCifrado = CifradoAsimetrico.cifrar(kPrivada, "RSA" ,mensajes[1]);
	outputLine = Base64.getEncoder().encodeToString(mensajesCifrado);
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

