package view;

import model.Usuario;

import javax.swing.*;
import java.awt.*;

// Menu Principal do Sistema
public class MenuPrincipalView extends JFrame 
{
    
    private Usuario usuarioLogado;
    
    public MenuPrincipalView(Usuario usuario) 
    {
        this.usuarioLogado = usuario;
        inicializarComponentes();
    }
    
    private void inicializarComponentes() 
    {
        setTitle("Sistema de Gerenciamento - Menu Principal");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Painel de cabeçalho
        JPanel painelCabecalho = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("Menu Principal", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        painelCabecalho.add(lblTitulo, BorderLayout.CENTER);
        
        JLabel lblUsuario = new JLabel("Usuário: " + usuarioLogado.getNome() + " (" + usuarioLogado.getNomePerfil() + ")");
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUsuario.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        painelCabecalho.add(lblUsuario, BorderLayout.SOUTH);
        
        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);
        
        // Painel central com botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(4, 1, 10, 10));
        
        // Botão Usuários
        JButton btnUsuarios = criarBotaoMenu("Gerenciar Usuários", 
            "Cadastrar, editar e excluir usuários do sistema");
        btnUsuarios.addActionListener(e -> abrirTelaUsuarios());
        painelBotoes.add(btnUsuarios);
        
        // Botão Perfis
        JButton btnPerfis = criarBotaoMenu("Gerenciar Perfis", 
            "Cadastrar, editar e excluir perfis de usuário");
        btnPerfis.addActionListener(e -> abrirTelaPerfis());
        painelBotoes.add(btnPerfis);
        
        // Botão Produtos
        JButton btnProdutos = criarBotaoMenu("Gerenciar Produtos", 
            "Cadastrar, editar e excluir produtos");
        btnProdutos.addActionListener(e -> abrirTelaProdutos());
        painelBotoes.add(btnProdutos);
        
        // Botão Sair
        JButton btnSair = criarBotaoMenu("Sair", "Fazer logout do sistema");
        btnSair.setBackground(new Color(220, 53, 69));
        btnSair.setForeground(Color.WHITE);
        btnSair.addActionListener(e -> sair());
        painelBotoes.add(btnSair);
        
        painelPrincipal.add(painelBotoes, BorderLayout.CENTER);
        
        // Rodapé
        JLabel lblRodape = new JLabel("Sistema de Gerenciamento v1.0 - Desenvolvido em Java Swing", SwingConstants.CENTER);
        lblRodape.setFont(new Font("Arial", Font.PLAIN, 10));
        lblRodape.setForeground(Color.GRAY);
        painelPrincipal.add(lblRodape, BorderLayout.SOUTH);
        
        add(painelPrincipal);
    }
    
    private JButton criarBotaoMenu(String titulo, String descricao) 
    {
        JButton botao = new JButton();
        botao.setLayout(new BorderLayout(10, 5));
        botao.setPreferredSize(new Dimension(500, 60));
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setFocusPainted(false);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        botao.add(lblTitulo, BorderLayout.NORTH);
        
        JLabel lblDesc = new JLabel(descricao);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 12));
        lblDesc.setForeground(Color.GRAY);
        botao.add(lblDesc, BorderLayout.CENTER);
        
        return botao;
    }
    
    private void abrirTelaUsuarios() 
    {
        UsuarioView telaUsuarios = new UsuarioView();
        telaUsuarios.setVisible(true);
    }
    
    private void abrirTelaPerfis() 
    {
        PerfilView telaPerfis = new PerfilView();
        telaPerfis.setVisible(true);
    }
    
    private void abrirTelaProdutos() 
    {
        ProdutoView telaProdutos = new ProdutoView();
        telaProdutos.setVisible(true);
    }
    
    private void sair() 
    {
        int opcao = JOptionPane.showConfirmDialog(this,
            "Deseja realmente sair do sistema?",
            "Confirmar Saída",
            JOptionPane.YES_NO_OPTION);
        
        if (opcao == JOptionPane.YES_OPTION) 
        {
            dispose();
            LoginView login = new LoginView();
            login.setVisible(true);
        }
    }
}