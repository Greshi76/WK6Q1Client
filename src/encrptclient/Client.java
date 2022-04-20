
package encrptclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

/**
 *
 * @author Wayne Alden
 * Q9504941
 */
public class Client {
    
    
    public static void main(String[] args) {
        Socket s = null;
        int serverPort = 9999;
        PublicKey publicKey;
        byte[] bytesPubKey = null, encrypted = null;
        int pubKeyLength;
        String data;
        
        final String password = "Horse";
        System.out.println("Password " + password);
        try {
            s = new Socket("localhost", serverPort);
            try (DataOutputStream out = new DataOutputStream(s.getOutputStream()); 
                 DataInputStream in = new DataInputStream(s.getInputStream())) {
                
                out.writeUTF("Hello");
                data = in.readUTF();
                if(data.equalsIgnoreCase("hello")){
                    out.writeUTF("Key");
                    pubKeyLength = in.readInt();
                    System.out.println(pubKeyLength);
                    bytesPubKey = new byte[pubKeyLength];
                    in.read(bytesPubKey, 0, pubKeyLength);
                    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(bytesPubKey);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    publicKey = keyFactory.generatePublic(pubKeySpec);
                    encrypted = encrypt(publicKey, password);
                    System.out.println("Encrypted length: " + encrypted.length);
                    System.out.println(encrypted);
                    out.writeUTF("password");
                    out.writeInt(encrypted.length);
                    out.write(encrypted, 0, encrypted.length);
                    out.writeUTF("finished");
                }
                
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            s.close();

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        
    }
    
    public static byte [] encrypt(PublicKey publicKey, String Message) throws Exception {
        
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(Message.getBytes("UTF-8"));
    }
}
