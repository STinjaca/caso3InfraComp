import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.Identity;

public class ProtocoloCliente {
	public static void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut) throws IOException {
		// lee del teclado
		System.out.println("Escriba el Reto a enviar: ");
		String fromUser = stdIn.readLine();
		
		// envia por la red
		pOut.println("SECURE INIT,"+fromUser);
		
		String fromServerCifrado = pIn.readLine();
		
		byte[] fromServerDescifrado = CifradoAsimetrico.descifrar(Servidor.KPublica, "RSA", fromServerCifrado.getBytes());
		String descifradoClaro = new String(fromServerDescifrado, StandardCharsets.UTF_8);
		
		if(!fromUser.equals(descifradoClaro)) {
			pOut.println("ERROR");
			return;
		}
		
		pOut.println("OK");
		System.out.println("BIEN");

	}
}
