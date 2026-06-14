/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utileria;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author PC
 */
public class Encriptar {
    public static String MD2 = "MD2";
    public static String MD5 = "MD5";
    public static String SHA1 = "SHA-1";
    public static String SHA256 = "SHA-256";
    public static String SHA384 = "SHA-384";
    public static String SHA512 = "SHA-512";

    public static String getStringMessageDialog(String cadena, String algoritm) {
        byte[] digest = null;
        byte[] buffer = cadena.getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algoritm);
            messageDigest.reset();
            messageDigest.update(buffer);
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error creando digest");
        }
        return toHexadecimal(digest);
    }

    private static String toHexadecimal(byte[] digest) {
        // CORRECCIÓN: originalmente era " " (espacio), lo que metía un espacio
        // al inicio de cada hash. Debe ser cadena vacía.
        StringBuilder hash = new StringBuilder();
        for (byte aux : digest) {
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1) {
                hash.append("0");
            }
            hash.append(Integer.toHexString(b));
        }
        return hash.toString();
    }
}
