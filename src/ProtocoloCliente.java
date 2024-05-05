import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Identity;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class ProtocoloCliente {
	public static void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		// lee del teclado
		
		String inputLine;
		String outputLine;
		
		// SIMULACIÖN ESTABLECER LLAVES INICIALES
		
		// 1
		System.out.println("iniciar");
		pOut.println("Establcer llave");
		
		// 5
		inputLine = pIn.readLine();
		System.out.println(inputLine);
		
		System.out.println("Llegada llave");
		// modulus
		BigInteger modulus = new BigInteger(pIn.readLine());
		BigInteger publicExponent = new BigInteger(pIn.readLine());
		
		// Crear LLave
		RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, publicExponent);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey kPublica = keyFactory.generatePublic(rsaPublicKeySpec);
		System.out.println(kPublica.toString());
		
		// INICIO DE COMUNICACIÖN
		// 1
		System.out.println("Escriba el Reto a enviar: ");
		String fromUser = stdIn.readLine();
		
		// enviar Reto
		pOut.println("SECURE INIT,"+fromUser);
		
		// 4
		byte[] fromServerCifrado = Base64.getDecoder().decode(pIn.readLine());
		byte[] fromServerDescifrado = CifradoAsimetrico.descifrar(kPublica, "RSA", fromServerCifrado);
		String descifradoClaro = new String(fromServerDescifrado, StandardCharsets.UTF_8);
		
		// 5
		if(!fromUser.equals(descifradoClaro)) {
			pOut.println("ERROR");
			return;
		}
		
		pOut.println("OK");
		System.out.println("OK");
		
		// 8
		
		BigInteger p = new BigInteger(pIn.readLine());
		BigInteger g = new BigInteger(pIn.readLine());
		BigInteger g_x = new BigInteger(pIn.readLine());
		BigInteger iv = new BigInteger(pIn.readLine());
		
		descifradoClaro = "";
		if (pIn.readLine().equals("SOBRE")) {
			inputLine = pIn.readLine();
			while (!inputLine.equals("FIN SOBRE")) {
				fromServerCifrado = Base64.getDecoder().decode(inputLine);
				fromServerDescifrado = CifradoAsimetrico.descifrar(kPublica, "RSA", fromServerCifrado);
				descifradoClaro += new String(fromServerDescifrado, StandardCharsets.UTF_8);
				inputLine = pIn.readLine();
			}
			
		}

		String[] descifradoClaroStrings = descifradoClaro.split(",");
		
		if(!descifradoClaroStrings[0].equals(p.toString())) {
			pOut.println("ERROR");
			return;
		}
		if(!descifradoClaroStrings[1].equals(g.toString())) {
			pOut.println("ERROR");
			return;
		}
		if(!descifradoClaroStrings[2].equals(g_x.toString())) {
			pOut.println("ERROR");
			return;
		}
		// 9
		pOut.println("OK");
		System.out.println("9,OK");
		
		// 10
		BigInteger y = new BigInteger("11");
		BigInteger g_y = g.modPow(y, p);
		pOut.println(g_y);
		// 11b
		BigInteger K = g_x.modPow(y, p);

	}
}
