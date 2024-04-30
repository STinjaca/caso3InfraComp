import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Servidor {
	
	public static final int PUERTO =3400;
	
	public static PublicKey KPublica;
	private static PrivateKey KPrivada;
	
	public Servidor() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(256);
		KeyPair keyPair = generator.generateKeyPair();
		
		KPublica = keyPair.getPublic();
		KPrivada = keyPair.getPrivate();
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		Servidor s = new Servidor();
		s.ejecutar();
	}
	public void ejecutar() throws IOException, NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		ServerSocket ss =null;
		boolean continuar =true;
		
		System.out.println("Main Server ..");
		
		try {
			ss = new ServerSocket(PUERTO);
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		while (continuar) {
			Socket socket =ss.accept();
			
			try {
				PrintWriter escritor = new PrintWriter(
						socket.getOutputStream(),true);
				BufferedReader lector = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				ProtocoloServidor.procesar(lector,escritor,this.KPrivada);
				escritor.close();
				lector.close();
				socket.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}

}
