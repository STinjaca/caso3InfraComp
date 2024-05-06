import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Identity;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ProtocoloCliente {
	public static void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
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

		// INICIO DE COMUNICACIÖN
		// 1
		// enviar Reto
		String fromUser = new Random().nextInt() + "";
		pOut.println("SECURE INIT," + fromUser);

		// 4
		byte[] fromServerCifrado = Base64.getDecoder().decode(pIn.readLine());
		byte[] fromServerDescifrado = CifradoAsimetrico.descifrar(kPublica, "RSA", fromServerCifrado);
		String descifradoClaro = new String(fromServerDescifrado, StandardCharsets.UTF_8);

		// 5
		if (!fromUser.equals(descifradoClaro)) {
			pOut.println("ERROR");
			return;
		}

		pOut.println("OK");
		System.out.println("5,OK");

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

		if (!descifradoClaroStrings[0].equals(p.toString())) {
			pOut.println("ERROR");
			return;
		}
		if (!descifradoClaroStrings[1].equals(g.toString())) {
			pOut.println("ERROR");
			return;
		}
		if (!descifradoClaroStrings[2].equals(g_x.toString())) {
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
		BigInteger Kmaestra = g_x.modPow(y, p);

		byte[] digest = DigestCalculator.calcularDigest(Kmaestra.toByteArray());

		// Convertir la clave y el IV a arreglos de bytes
		byte[] KAB_1Bytes = Arrays.copyOfRange(digest, 0, 32);
		byte[] KAB_2Bytes = Arrays.copyOfRange(digest, 32, 64);
		byte[] ivBytes = iv.toByteArray();

		SecretKeySpec KAB_1 = new SecretKeySpec(KAB_1Bytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		SecretKeySpec KAB_2 = new SecretKeySpec(KAB_2Bytes, "HmacSHA256");

		// 12
		inputLine = pIn.readLine();
		if (!inputLine.equals("CONTINUAR")) {
			return;
		}
		System.out.println("12," + inputLine);

		// 13
		outputLine = "s.tinjaca@uniandes.edu.co";
		String outlineCifrado = CifradoSimetrico.cifrar(KAB_1, outputLine, ivSpec);
		pOut.println(outlineCifrado);

		// 14
		outputLine = "123456789";
		outlineCifrado = CifradoSimetrico.cifrar(KAB_1, outputLine, ivSpec);
		pOut.println(outlineCifrado);

		// 16
		inputLine = pIn.readLine();
		if (!inputLine.equals("OK")) {
			return;
		}
		System.out.println("16," + inputLine);

		// 17
		outputLine = "CONSULTA DE ALGO GENIAL Y SUPER MAGICO";
		outlineCifrado = CifradoSimetrico.cifrar(KAB_1, outputLine, ivSpec);
		pOut.println(outlineCifrado);

		// 18
		String outlineHMac = DigestCalculator.calcularHMACSHA256(KAB_2, outputLine);
		pOut.println(outlineHMac);

		inputLine = pIn.readLine();
		if (!inputLine.equals("OK")) {
			return;
		}
		System.out.println("17 18," + inputLine);

		// 21
		inputLine = pIn.readLine();
		inputLine = CifradoSimetrico.descifrar(KAB_1, inputLine, ivSpec);

		String inputlineHMac = pIn.readLine();
		String inputlineHMacGenerado = DigestCalculator.calcularHMACSHA256(KAB_2, inputLine);

		if (!inputlineHMac.equals(inputlineHMacGenerado)) {
			pOut.println("ERROR");
			return;
		}
		pOut.println("OK");
		System.out.println("19 20,OK");
	}
}
