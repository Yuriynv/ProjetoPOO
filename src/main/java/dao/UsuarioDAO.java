package dao;

import model.Usuario;
import util.ConexaoBD;
import util.HashUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO para operações de Usuário no banco de dados
 */
public class UsuarioDAO {
    
    /**
     * Valida login do usuário
     * @param email Email do usuário
     * @param senha Senha em texto plano
     * @return Usuario se credenciais válidas, null caso contrário
     * @throws SQLException
     */
    public Usuario validarLogin(String email, String senha) throws SQLException {
        String sql = "SELECT u.*, p.nome_perfil, p.permissoes FROM usuarios u " +
                     "INNER JOIN perfis p ON u.id_perfil = p.id " +
                     "WHERE u.email = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String senhaHash = rs.getString("senha");
                
                // Valida a senha
                if (HashUtil.validarSenha(senha, senhaHash)) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setIdPerfil(rs.getInt("id_perfil"));
                    usuario.setNomePerfil(rs.getString("nome_perfil"));
                    usuario.setPermissoes(rs.getString("permissoes"));
                    usuario.setDataCadastro(rs.getTimestamp("data_cadastro"));
                    return usuario;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Lista todos os usuários cadastrados
     * @return Lista de usuários
     * @throws SQLException
     */
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, p.nome_perfil FROM usuarios u " +
                     "INNER JOIN perfis p ON u.id_perfil = p.id " +
                     "ORDER BY u.nome";
        
        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
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
    
    /**
     * Busca um usuário por ID
     * @param id ID do usuário
     * @return Usuario encontrado ou null
     * @throws SQLException
     */
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT u.*, p.nome_perfil FROM usuarios u " +
                     "INNER JOIN perfis p ON u.id_perfil = p.id " +
                     "WHERE u.id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
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
    
    /**
     * Insere um novo usuário
     * @param usuario Usuario a ser inserido
     * @return true se inserido com sucesso
     * @throws SQLException
     */
    public boolean inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha, id_perfil) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, HashUtil.gerarHashSHA256(usuario.getSenha()));
            stmt.setInt(4, usuario.getIdPerfil());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Atualiza um usuário existente
     * @param usuario Usuario com dados atualizados
     * @param atualizarSenha Se true, atualiza também a senha
     * @return true se atualizado com sucesso
     * @throws SQLException
     */
    public boolean atualizar(Usuario usuario, boolean atualizarSenha) throws SQLException {
        String sql;
        
        if (atualizarSenha) {
            sql = "UPDATE usuarios SET nome = ?, email = ?, senha = ?, id_perfil = ? WHERE id = ?";
        } else {
            sql = "UPDATE usuarios SET nome = ?, email = ?, id_perfil = ? WHERE id = ?";
        }
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            
            if (atualizarSenha) {
                stmt.setString(3, HashUtil.gerarHashSHA256(usuario.getSenha()));
                stmt.setInt(4, usuario.getIdPerfil());
                stmt.setInt(5, usuario.getId());
            } else {
                stmt.setInt(3, usuario.getIdPerfil());
                stmt.setInt(4, usuario.getId());
            }
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Exclui um usuário
     * @param id ID do usuário a ser excluído
     * @return true se excluído com sucesso
     * @throws SQLException
     */
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Verifica se o email já está cadastrado
     * @param email Email a ser verificado
     * @param idUsuario ID do usuário (para edição, ignorar o próprio)
     * @return true se o email já existe
     * @throws SQLException
     */
    public boolean emailExiste(String email, int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ? AND id != ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setInt(2, idUsuario);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        
        return false;
    }
}