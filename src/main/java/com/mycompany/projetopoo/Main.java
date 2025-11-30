package com.mycompany.projetopoo;

import util.ConexaoBD;
import view.LoginView;

import javax.swing.*;

public class Main 
{
    public static void main(String[] args) 
    {
        configurarLookAndFeel();
        
        if (!testarConexaoBancoDados()) 
        {
            exibirErroConexao();
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
    
    private static void configurarLookAndFeel() 
    {
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            
        } 
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) 
        {
            System.err.println("Não foi possível configurar Look and Feel: " + e.getMessage());
        }
    }
    
    private static boolean testarConexaoBancoDados() 
    {
        System.out.println("Testando conexão com banco de dados...");
        
        try 
        {
            if (ConexaoBD.testarConexao()) 
            {
                System.out.println("✓ Conexão com banco de dados estabelecida com sucesso!");
                return true;
            } 
            else 
            {
                System.err.println("✗ Falha ao conectar com banco de dados!");
                return false;
            }
        } 
        catch (Exception e) 
        {
            System.err.println("✗ Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }
    
    private static void exibirErroConexao() 
    {
        String mensagem = "Não foi possível conectar ao banco de dados!\n\n" +
                         "Verifique:\n" +
                         "1. Se o MySQL/PostgreSQL está rodando\n" +
                         "2. Se o banco 'sistema_usuarios' foi criado\n" +
                         "3. As credenciais em util/ConexaoBD.java\n" +
                         "4. Se o driver JDBC está no classpath\n\n" +
                         "Consulte o arquivo README.md para mais informações.";
        
        JOptionPane.showMessageDialog(null,
            mensagem,
            "Erro de Conexão",
            JOptionPane.ERROR_MESSAGE);
    }
}