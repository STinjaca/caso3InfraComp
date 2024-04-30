import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
	
<<<<<<< HEAD
	public static final int PUERTO =3400;
	
=======
>>>>>>> 6d918ca071b5a2d5eef0eea88b9a345b68d04e56
	public static void main(String[] args) {
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
				ProtocoloServidor.procesar(lector,escritor);
				escritor.close();
				lector.close();
				socket.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}

}
