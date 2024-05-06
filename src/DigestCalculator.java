import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class DigestCalculator {
    
    public static byte[] calcularDigest(byte[] datos) {
        try {
            // Obtener una instancia
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            
            byte[] digestBytes = digest.digest(datos);
            
            return digestBytes;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static String calcularHMACSHA256(SecretKeySpec claveSecreta, String texto)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(claveSecreta);
        byte[] hmacBytes = mac.doFinal(texto.getBytes());
        return bytesToHex(hmacBytes);
    }
    
	 public static String bytesToHex(byte[] bytes) {
	        StringBuilder sb = new StringBuilder();
	        for (byte b : bytes) {
	            sb.append(String.format("%02X", b));
	        }
	        return sb.toString();
 }
	 

}
