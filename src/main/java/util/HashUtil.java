package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil
{

    public static String gerarHashSHA256(String texto)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes());
            
            // converte bytes para hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash)
            {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Erro ao gerar hash: " + e.getMessage());
        }
    }
    
    public static String gerarHashMD5(String texto)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(texto.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Erro ao gerar hash: " + e.getMessage());
        }
    }
    
    public static boolean validarSenha(String senha, String hashArmazenado)
    {
        String hashSenha = gerarHashSHA256(senha);
        return hashSenha.equals(hashArmazenado);
    }
}