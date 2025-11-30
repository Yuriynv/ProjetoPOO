package dao;

import model.Perfil;
import util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerfilDAO
{

    public List<Perfil> listarTodos() throws SQLException
    {
        List<Perfil> perfis = new ArrayList<>();
        String sql = "SELECT * FROM perfis ORDER BY nome_perfil";
        
        try (Connection conn = ConexaoBD.getConexao();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next())
            {
                Perfil perfil = new Perfil();
                perfil.setId(rs.getInt("id"));
                perfil.setNomePerfil(rs.getString("nome_perfil"));
                perfil.setDescricao(rs.getString("descricao"));
                perfil.setPermissoes(rs.getString("permissoes"));
                perfil.setDataCriacao(rs.getTimestamp("data_criacao"));
                perfis.add(perfil);
            }
        }
        
        return perfis;
    }
    
    public Perfil buscarPorId(int id) throws SQLException
    {
        String sql = "SELECT * FROM perfis WHERE id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {    
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next())
            {
                Perfil perfil = new Perfil();
                perfil.setId(rs.getInt("id"));
                perfil.setNomePerfil(rs.getString("nome_perfil"));
                perfil.setDescricao(rs.getString("descricao"));
                perfil.setPermissoes(rs.getString("permissoes"));
                perfil.setDataCriacao(rs.getTimestamp("data_criacao"));
                return perfil;
            }
        }
        
        return null;
    }
    
    public boolean inserir(Perfil perfil) throws SQLException
    {
        String sql = "INSERT INTO perfis (nome_perfil, descricao, permissoes) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexaoBD.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            
            stmt.setString(1, perfil.getNomePerfil());
            stmt.setString(2, perfil.getDescricao());
            stmt.setString(3, perfil.getPermissoes());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean atualizar(Perfil perfil) throws SQLException
    {
        String sql = "UPDATE perfis SET nome_perfil = ?, descricao = ?, permissoes = ? WHERE id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, perfil.getNomePerfil());
            stmt.setString(2, perfil.getDescricao());
            stmt.setString(3, perfil.getPermissoes());
            stmt.setInt(4, perfil.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean excluir(int id) throws SQLException
    {
        // Verifica se existem usuários com este perfil
        if (temUsuariosVinculados(id))
        {
            throw new SQLException("Não é possível excluir. Existem usuários vinculados a este perfil.");
        }
        
        String sql = "DELETE FROM perfis WHERE id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {    
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private boolean temUsuariosVinculados(int idPerfil) throws SQLException
    {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE id_perfil = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {    
            stmt.setInt(1, idPerfil);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next())
            {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }
}