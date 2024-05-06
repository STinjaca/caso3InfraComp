import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Servidor {
	
	public static final int PUERTO =3400;
	
	public static PublicKey KPublica;
	private static PrivateKey KPrivada;
	private int numeroThreads;
	public static ArrayList<long[]> data = new ArrayList<long[]>();
	
	public Servidor() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(1024);
		KeyPair keyPair = generator.generateKeyPair();
		
		KPublica = keyPair.getPublic();
		KPrivada = keyPair.getPrivate();
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("El programa está terminando. Se ejecutó el gancho de cierre.");
            imprimirMatrizEnArchivo("dataServidor.txt");
        }));
		Servidor s = new Servidor();
		s.ejecutar();
		
		
	}
	public void ejecutar() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
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
		numeroThreads = 0;
		while (continuar) {
			Socket socket =ss.accept();
			data.add(new long[3]);
			ThreadServidor thread = new ThreadServidor(socket, numeroThreads, KPrivada, KPublica);
			numeroThreads++;
			thread.start();
		}
		ss.close();
	}
	
	public static void imprimirMatrizEnArchivo(String nombreArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, false))) {
            // Se sobrescribe el archivo cada vez que se ejecuta el programa
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < data.get(i).length; j++) {
                    writer.write(String.valueOf(data.get(i)[j]));
                    if (j < data.get(i).length - 1) {
                        writer.write(", "); // Coma y espacio entre elementos
                    }
                }
                writer.newLine(); // Nueva línea al final de cada fila
            }
            System.out.println("La matriz ha sido escrita en el archivo '" + nombreArchivo + "'.");
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

}
