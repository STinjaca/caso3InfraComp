import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ThreadServidor extends Thread {
    private Socket sktCliente = null;
    private int id;
    public PublicKey KPublica;
	private PrivateKey KPrivada;
	
    public ThreadServidor(Socket pSocket, int pId, PrivateKey KPrivada, PublicKey KPublica) {
        this.sktCliente = pSocket;
        this.id = pId;
        this.KPrivada = KPrivada;
        this.KPublica = KPublica;
 
    }

    public void run() {
        System.out.println("Inicio de un nuevo thread: " + id);

        try {
            PrintWriter escritor = new PrintWriter(
                    sktCliente.getOutputStream(), true);

            BufferedReader lector = new BufferedReader(new InputStreamReader(
                    sktCliente.getInputStream()));

            ProtocoloServidor.procesar(id, lector,escritor, KPrivada, KPublica);

            escritor.close();
            lector.close();

        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException e) {
            try {
				sktCliente.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            e.printStackTrace();
        }
    }
}
