import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

}
