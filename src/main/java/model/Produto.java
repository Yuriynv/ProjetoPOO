package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Produto
{   
    private int id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int quantidadeEstoque;
    private Timestamp dataCadastro;
    
    // Construtores
    public Produto() {    }
    
    public Produto(int id, String nome, String descricao, BigDecimal preco, int quantidadeEstoque) 
    {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }
    
    // Getters e Setters
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    
    public String getDescricao() {return descricao;}
    public void setDescricao(String descricao) {this.descricao = descricao;}
    
    public BigDecimal getPreco() {return preco;}
    public void setPreco(BigDecimal preco) {this.preco = preco;}
    
    public int getQuantidadeEstoque() {return quantidadeEstoque;}
    public void setQuantidadeEstoque(int quantidadeEstoque) {this.quantidadeEstoque = quantidadeEstoque;}
    
    public Timestamp getDataCadastro() {return dataCadastro;}    
    public void setDataCadastro(Timestamp dataCadastro) {this.dataCadastro = dataCadastro;}
    
    @Override
    public String toString() 
    {
        return nome;
    }
}