package dao;

import model.Usuario;
import util.ConexaoBD;
import util.HashUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO 
{
    public Usuario validarLogin(String email, String senha) throws SQLException
    {
        String sql = "SELECT u.*, p.nome_perfil FROM usuarios u " +
                     "INNER JOIN perfis p ON u.id_perfil = p.id " +
                     "WHERE u.email = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql))
        {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                String senhaHash = rs.getString("senha");
                
                // Valida a senha
                if (HashUtil.validarSenha(senha, senhaHash)) 
                {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setIdPerfil(rs.getInt("id_perfil"));
                    usuario.setNomePerfil(rs.getString("nome_perfil"));
                    usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
                    return usuario;
                }
            }
        }
        
        return null;
    }
    
    public List<Usuario> listarTodos() throws SQLException 
    {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, p.nome_perfil FROM usuarios u " +
                     "INNER JOIN perfis p ON u.id_perfil = p.id " +
                     "ORDER BY u.nome";
        
        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) 
            {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setIdPerfil(rs.getInt("id_perfil"));
                usuario.setNomePerfil(rs.getString("nome_perfil"));
                usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
                usuarios.add(usuario);
            }
        }
        
        return usuarios;
    }

    public Usuario buscarPorId(int id) throws SQLException 
    {
        String sql = "SELECT u.*, p.nome_perfil FROM usuarios u " +
                     "INNER JOIN perfis p ON u.id_perfil = p.id " +
                     "WHERE u.id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setIdPerfil(rs.getInt("id_perfil"));
                usuario.setNomePerfil(rs.getString("nome_perfil"));
                usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
                return usuario;
            }
        }
        
        return null;
    }

    public boolean inserir(Usuario usuario) throws SQLException 
    {
        // Verifica se email já existe
        if (emailExiste(usuario.getEmail(), 0)) 
        {
            throw new SQLException("Email já cadastrado no sistema!");
        }
        
        String sql = "INSERT INTO usuarios (nome, email, senha, id_perfil) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, HashUtil.gerarHashSHA256(usuario.getSenha()));
            stmt.setInt(4, usuario.getIdPerfil());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean atualizar(Usuario usuario, boolean atualizarSenha) throws SQLException 
    {
        // Verifica se email já existe (exceto para o próprio usuário)
        if (emailExiste(usuario.getEmail(), usuario.getId())) 
        {
            throw new SQLException("Email já cadastrado para outro usuário!");
        }
        
        String sql;
        
        if (atualizarSenha) 
        {
            sql = "UPDATE usuarios SET nome = ?, email = ?, senha = ?, id_perfil = ? WHERE id = ?";
        } 
        else 
        {
            sql = "UPDATE usuarios SET nome = ?, email = ?, id_perfil = ? WHERE id = ?";
        }
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            
            if (atualizarSenha) 
            {
                stmt.setString(3, HashUtil.gerarHashSHA256(usuario.getSenha()));
                stmt.setInt(4, usuario.getIdPerfil());
                stmt.setInt(5, usuario.getId());
            } 
            else 
            {
                stmt.setInt(3, usuario.getIdPerfil());
                stmt.setInt(4, usuario.getId());
            }
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean excluir(int id) throws SQLException 
    {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean emailExiste(String email, int idUsuario) throws SQLException 
    {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ? AND id != ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setString(1, email);
            stmt.setInt(2, idUsuario);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }
    
    public List<Usuario> buscarPorNome(String nome) throws SQLException 
    {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, p.nome_perfil FROM usuarios u " +
                     "INNER JOIN perfis p ON u.id_perfil = p.id " +
                     "WHERE u.nome LIKE ? ORDER BY u.nome";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) 
            {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setIdPerfil(rs.getInt("id_perfil"));
                usuario.setNomePerfil(rs.getString("nome_perfil"));
                usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
                usuarios.add(usuario);
            }
        }
        
        return usuarios;
    }
    
    public int contarUsuarios() throws SQLException 
    {
        String sql = "SELECT COUNT(*) FROM usuarios";
        
        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) 
        {
            
            if (rs.next()) 
            {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    public List<Usuario> listarPorPerfil(int idPerfil) throws SQLException 
    {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, p.nome_perfil FROM usuarios u " +
                     "INNER JOIN perfis p ON u.id_perfil = p.id " +
                     "WHERE u.id_perfil = ? ORDER BY u.nome";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setInt(1, idPerfil);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) 
            {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setIdPerfil(rs.getInt("id_perfil"));
                usuario.setNomePerfil(rs.getString("nome_perfil"));
                usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
                usuarios.add(usuario);
            }
        }
        
        return usuarios;
    }
}