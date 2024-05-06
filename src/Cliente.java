import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class Cliente extends Thread {
	public static final int PUERTO = 3400;
	public static final String SERVIDOR = "localhost";
	public static long[][] data;
	private int id;

	public static void main(String[] args)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InterruptedException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Ingresa un número: ");
		int num = scanner.nextInt();
		System.out.println(num);
		Cliente.data = new long[num][4];
		Cliente[] clientes = new Cliente[num];
		for(int i = 0; i<num; i++) {
			clientes[i] = new Cliente(i);
			clientes[i].start();;
		}
		
		for(int i = 0; i<num; i++) {
			clientes[i].join();
		}
		
		imprimirMatrizEnArchivo(data, "doc/dataClientes.txt");
		
	}
	
	public Cliente(int id) {
		this.id = id;
	}

	public void run() {
		Socket socket = null;
		PrintWriter escritor = null;
		BufferedReader lector = null;

		System.out.println("Cliente ...");
		try {

			// crea el socket en el lado cliente
			socket = new Socket(SERVIDOR, PUERTO);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// crea un flujo para leer lo que escribe el cliente por el teclado
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

			// se ejecuta el protocolo en el lado cliente
			ProtocoloCliente.procesar(id, stdIn, lector, escritor);
			// se cierran los flujos y el socket
			stdIn.close();
			escritor.close();
			lector.close();
			socket.close();
		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}
	public static void imprimirMatrizEnArchivo(long[][] matriz, String nombreArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo, false))) {
            // Se sobrescribe el archivo cada vez que se ejecuta el programa
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[0].length; j++) {
                    writer.write(String.valueOf(matriz[i][j]));
                    if (j < matriz[0].length - 1) {
                        writer.write(","); // Tabulación entre elementos
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
