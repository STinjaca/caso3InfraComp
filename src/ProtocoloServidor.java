import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;

public class ProtocoloServidor {
	public static void procesar(int id, BufferedReader pIn, PrintWriter pOut, PrivateKey kPrivada, PublicKey kPublica)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException {
		String inputLine;
		String outputLine;

		// SIMULACIÖN ESTABLECER LLAVES INICIALES
		// 2
		inputLine = pIn.readLine();
		System.out.println("LLegada del cliente: " + inputLine);

		// 3
		pOut.println("PUBLIC_KEY");

		String[] kPublicaString = kPublica.toString().strip().split("\n");
		String modulus = kPublicaString[2].split(":")[1].strip();
		String publicExponent = kPublicaString[3].split(":")[1].strip();

		// 4
		pOut.println(modulus);
		pOut.println(publicExponent);

		// INICIO DE COMUNICACIÖN
		// 2 Recibir reto
		inputLine = pIn.readLine();
		String[] mensajes = inputLine.split(",");
		// 3 Cifrar reto
		long startTime = System.nanoTime();
		byte[] mensajesCifrado = CifradoAsimetrico.cifrar(kPrivada, "RSA", mensajes[1]);
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		synchronized(Servidor.data){
			Servidor.data.get(id)[0] = elapsedTime;
		}
		
		outputLine = Base64.getEncoder().encodeToString(mensajesCifrado);
		pOut.println(outputLine);

		inputLine = pIn.readLine();

		if (inputLine.equals("ERROR")) {
			return;
		}
		// else ok
		System.out.println("5," + inputLine);

		// 6
		BigInteger p = BigInteger.probablePrime(1024, new Random());
		BigInteger g;
		do {
			// Generar un número aleatorio en el rango (2, p-1)
			g = new BigInteger(p.bitLength(), new Random()).mod(p);
		} while (!p.gcd(g).equals(BigInteger.ONE));

		BigInteger x = new BigInteger(p.bitLength(), new Random());
		BigInteger g_x = g.modPow(x, p);
		byte[] ivBytes = new byte[16];
		new SecureRandom().nextBytes(ivBytes);
		BigInteger iv = new BigInteger(ivBytes);

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
		System.out.println("9," + inputLine);

		// 11a
		BigInteger g_y = new BigInteger(pIn.readLine());
		BigInteger Kmaestra = g_y.modPow(x, p);

		byte[] digest = DigestCalculator.calcularDigest(Kmaestra.toByteArray());

		// Convertir la clave y el IV a arreglos de bytes
		byte[] KAB_1Bytes = Arrays.copyOfRange(digest, 0, 32);
		byte[] KAB_2Bytes = Arrays.copyOfRange(digest, 32, 64);
		System.out.println(iv.toByteArray().length);

		SecretKeySpec KAB_1 = new SecretKeySpec(KAB_1Bytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		SecretKeySpec KAB_2 = new SecretKeySpec(KAB_2Bytes, "HmacSHA256");

		// 12
		pOut.println("CONTINUAR");
		System.out.println("12,Continuar");

		// 15
		inputLine = pIn.readLine();
		inputLine = CifradoSimetrico.descifrar(KAB_1, inputLine, ivSpec);

		if (!inputLine.equals("s.tinjaca@uniandes.edu.co")) {
			pOut.println("ERROR");
			return;
		}

		inputLine = pIn.readLine();
		inputLine = CifradoSimetrico.descifrar(KAB_1, inputLine, ivSpec);

		if (!inputLine.equals("123456789")) {
			pOut.println("ERROR");
			return;
		}

		// 16
		pOut.println("OK");
		System.out.println("16,OK");

		// 18
		inputLine = pIn.readLine();
		startTime = System.nanoTime();
		inputLine = CifradoSimetrico.descifrar(KAB_1, inputLine, ivSpec);
		elapsedTime = endTime - startTime;
		synchronized(Servidor.data){
			Servidor.data.get(id)[1] = elapsedTime;
		}

		String inputlineHMac = pIn.readLine();

		startTime = System.nanoTime();
		String inputlineHMacGenerado = DigestCalculator.calcularHMACSHA256(KAB_2, inputLine);
		if (!inputlineHMac.equals(inputlineHMacGenerado)) {
			pOut.println("ERROR");
			return;
		}
		
		elapsedTime = endTime - startTime;
		synchronized(Servidor.data){
			Servidor.data.get(id)[2] = elapsedTime;
		}
		pOut.println("OK");
		System.out.println("17 18,OK");

		// 19
		outputLine = "OH GRACIAS POR CONSULTAR AQUI TU RESPUESTA";
		outlineCifrado = CifradoSimetrico.cifrar(KAB_1, outputLine, ivSpec);
		pOut.println(outlineCifrado);

		// 20
		String outlineHMac = DigestCalculator.calcularHMACSHA256(KAB_2, outputLine);
		pOut.println(outlineHMac);

		inputLine = pIn.readLine();
		if (!inputLine.equals("OK")) {
			return;
		}
		System.out.println("19 20," + inputLine);
	}
}
