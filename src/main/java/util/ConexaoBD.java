package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD
{
    // Configurações do banco de dados - AJUSTE CONFORME SEU AMBIENTE
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_usuarios";
    private static final String USUARIO = "root";
    private static final String SENHA = "";
    
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    public static Connection getConexao() throws SQLException
    {
        try
        {
            // Carrega o driver JDBC (???????)
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        }
        catch (ClassNotFoundException e)
        {
            throw new SQLException("Driver JDBC não encontrado: " + e.getMessage());
        }
    }
    
    public static void fecharConexao(Connection conn)
    {
        if (conn != null)
        {
            try
            {
                conn.close();
            }
            catch (SQLException e)
            {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
    
    public static boolean testarConexao()
    {
        try (Connection conn = getConexao())
        {
            return conn != null && !conn.isClosed();
        }
        catch (SQLException e)
        {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }
}