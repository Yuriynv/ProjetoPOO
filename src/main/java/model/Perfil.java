package model;

import java.sql.Timestamp;

public class Perfil
{
    private int id;
    private String nomePerfil;
    private String descricao;
    private String permissoes;
    private Timestamp dataCriacao;
    
    // Construtores
    public Perfil() {    }
    
    public Perfil(int id, String nomePerfil, String descricao, String permissoes)
    {
        this.id = id;
        this.nomePerfil = nomePerfil;
        this.descricao = descricao;
        this.permissoes = permissoes;
    }
    
    // Getters e Setters
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    
    public String getNomePerfil() {return nomePerfil;}
    public void setNomePerfil(String nomePerfil) {this.nomePerfil = nomePerfil;}
    
    public String getDescricao() {return descricao;}
    public void setDescricao(String descricao) {this.descricao = descricao;}
    
    public String getPermissoes() {return permissoes;}
    public void setPermissoes(String permissoes) {this.permissoes = permissoes;}
    
    public Timestamp getDataCriacao() {return dataCriacao;}
    public void setDataCriacao(Timestamp dataCriacao) {this.dataCriacao = dataCriacao;}
    
    @Override
    public String toString()
    {
        return nomePerfil; // Para uso em JComboBox
    }
}