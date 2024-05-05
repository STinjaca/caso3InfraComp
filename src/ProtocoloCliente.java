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
		System.out.println("Escriba el Reto a enviar: ");
		String fromUser = stdIn.readLine();
		
		// enviar Reto
		pOut.println("SECURE INIT,"+fromUser);
		
		byte[] fromServerCifrado = Base64.getDecoder().decode(pIn.readLine());
		byte[] fromServerDescifrado = CifradoAsimetrico.descifrar(kPublica, "RSA", fromServerCifrado);
		String descifradoClaro = new String(fromServerDescifrado, StandardCharsets.UTF_8);
		
		if(!fromUser.equals(descifradoClaro)) {
			pOut.println("ERROR");
			return;
		}
		
		pOut.println("OK");
		System.out.println("BIEN");

	}
}
