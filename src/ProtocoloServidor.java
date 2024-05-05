import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Random;

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
	// 2 Recibir reto
	inputLine = pIn.readLine(); 
	System.out.println("Entrada a procesar:"+inputLine);
	String[] mensajes = inputLine.split(",");
	// 3 Cifrar reto
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
	
	// 6
	BigInteger p = BigInteger.probablePrime(1024, new Random());
	BigInteger  g =  new BigInteger("5");
	BigInteger x = new BigInteger("10");
	BigInteger g_x = g.modPow(x, p);
	BigInteger iv = new BigInteger("100");
	
	// 7
	pOut.println(p);
	pOut.println(g);
	pOut.println(g_x);
	pOut.println(iv);
	
	outputLine = p.toString() + "," + g.toString() + "," + g_x.toString();
	String outlineCifrado = "";
	pOut.println("SOBRE");
	if (outputLine.length() > 117) {
	    int i = 0;
	    while (i < outputLine.length()) {
	        String tempString;
	        int endIndex = Math.min(i + 117, outputLine.length());
	        tempString = outputLine.substring(i, endIndex);
	        mensajesCifrado = CifradoAsimetrico.cifrar(kPrivada, "RSA", tempString);
	        outlineCifrado = Base64.getEncoder().encodeToString(mensajesCifrado);
	        pOut.println(outlineCifrado);
	        i = endIndex;
	    }
	} else {
	    mensajesCifrado = CifradoAsimetrico.cifrar(kPrivada, "RSA", outputLine);
	    outlineCifrado = Base64.getEncoder().encodeToString(mensajesCifrado);
	    pOut.println(outlineCifrado);
	}
	
	pOut.println("FIN SOBRE");
	inputLine = pIn.readLine();
	
	if (inputLine.equals("ERROR")) {
		return;
	}
	System.out.println(inputLine);
	
	// 11a
	BigInteger g_y = new BigInteger(pIn.readLine());
	BigInteger K = g_y.modPow(x, p);

	}
}

