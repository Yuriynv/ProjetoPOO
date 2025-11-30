package dao;

import model.Produto;
import util.ConexaoBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO
{
    public List<Produto> listarTodos() throws SQLException
    {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos ORDER BY nome";
        
        try (Connection conn = ConexaoBD.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            
            while (rs.next())
            {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getBigDecimal("preco"));
                produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                produto.setDataCadastro(rs.getTimestamp("data_cadastro"));
                produtos.add(produto);
            }
        }
        
        return produtos;
    }
    
    public Produto buscarPorId(int id) throws SQLException
    {
        String sql = "SELECT * FROM produtos WHERE id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) 
            {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getBigDecimal("preco"));
                produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                produto.setDataCadastro(rs.getTimestamp("data_cadastro"));
                return produto;
            }
        }
        
        return null;
    }
    
    public boolean inserir(Produto produto) throws SQLException 
    {
        String sql = "INSERT INTO produtos (nome, descricao, preco, quantidade_estoque) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setBigDecimal(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidadeEstoque());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean atualizar(Produto produto) throws SQLException 
    {
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, quantidade_estoque = ? WHERE id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setBigDecimal(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidadeEstoque());
            stmt.setInt(5, produto.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean excluir(int id) throws SQLException 
    {
        String sql = "DELETE FROM produtos WHERE id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public List<Produto> buscarPorNome(String nome) throws SQLException 
    {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE nome LIKE ? ORDER BY nome";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) 
            {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getBigDecimal("preco"));
                produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                produto.setDataCadastro(rs.getTimestamp("data_cadastro"));
                produtos.add(produto);
            }
        }
        
        return produtos;
    }
    
    public List<Produto> listarEstoqueBaixo(int quantidadeMinima) throws SQLException 
    {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos WHERE quantidade_estoque <= ? ORDER BY quantidade_estoque";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            
            stmt.setInt(1, quantidadeMinima);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) 
            {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getBigDecimal("preco"));
                produto.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                produto.setDataCadastro(rs.getTimestamp("data_cadastro"));
                produtos.add(produto);
            }
        }
        
        return produtos;
    }
    
    public boolean atualizarEstoque(int id, int novaQuantidade) throws SQLException 
    {
        String sql = "UPDATE produtos SET quantidade_estoque = ? WHERE id = ?";
        
        try (Connection conn = ConexaoBD.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
}