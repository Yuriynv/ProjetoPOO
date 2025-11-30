package model;

import java.sql.Timestamp;

public class Usuario 
{
    private int id;
    private String nome;
    private String email;
    private String senha;
    private int idPerfil;
    private String nomePerfil; // Para exibição
    private Timestamp dataCadastro;
    
    // Construtores
    public Usuario() {    }
    
    public Usuario(int id, String nome, String email, int idPerfil) 
    {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.idPerfil = idPerfil;
    }
    
    // Getters e Setters
    public int getId() {return id;}    
    public void setId(int id) {this.id = id;}
    
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    
    public String getSenha() {return senha;}
    public void setSenha(String senha) {this.senha = senha;}
    
    public int getIdPerfil() {return idPerfil;}    
    public void setIdPerfil(int idPerfil) {this.idPerfil = idPerfil;}
    
    public String getNomePerfil() {return nomePerfil;}    
    public void setNomePerfil(String nomePerfil) {this.nomePerfil = nomePerfil;}
    
    public Timestamp getDataCadastro() {return dataCadastro;}
    public void setDataCadastro(Timestamp dataCadastro) {this.dataCadastro = dataCadastro;}
    
    @Override
    public String toString()
    {
        return nome;
    }
}