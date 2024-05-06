import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CifradoSimetrico {
	private static final String PADDING = "AES/CBC/PKCS5Padding";
	
	
	public static String cifrar(SecretKey llave, String texto,  IvParameterSpec ivSpec) {
		byte[] textoCifrado;
		
		try {
			Cipher cifrador = Cipher.getInstance(PADDING);
			byte[] textoClaro = texto.getBytes();
			
			cifrador.init(Cipher.ENCRYPT_MODE, llave, ivSpec);
			textoCifrado = cifrador.doFinal(textoClaro);
			
			return bytesToHex(textoCifrado);
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			return null;
		}
	}
	
	public static String descifrar(SecretKey llave, String texto, IvParameterSpec ivSpec) {
		byte[] textoClaro;
		byte[] textoCifrado = hexToBytes(texto);
		try {
			Cipher cifrador = Cipher.getInstance(PADDING);
			cifrador.init(Cipher.DECRYPT_MODE, llave, ivSpec);
			textoClaro = cifrador.doFinal(textoCifrado);
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			return null;
		}
		return new String(textoClaro);
	}
	
	 public static String bytesToHex(byte[] bytes) {
	        StringBuilder sb = new StringBuilder();
	        for (byte b : bytes) {
	            sb.append(String.format("%02X", b));
	        }
	        return sb.toString();
    }
	 
	 public static byte[] hexToBytes(String hexString) {
		    int longitud = hexString.length();
		    byte[] bytes = new byte[longitud / 2];
		    for (int i = 0; i < longitud; i += 2) {
		        bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
		                + Character.digit(hexString.charAt(i+1), 16));
		    }
		    return bytes;
		}
}
