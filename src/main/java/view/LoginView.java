package view;

import dao.UsuarioDAO;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

// Tela de Login do Sistema
public class LoginView extends JFrame
{    
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JLabel lblMensagem;
    
    private UsuarioDAO usuarioDAO;
    
    public LoginView()
    {
        usuarioDAO = new UsuarioDAO();
        inicializarComponentes();
    }
    
    private void inicializarComponentes()
    {
        setTitle("Sistema de Gerenciamento - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel principal
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel lblTitulo = new JLabel("Sistema de Gerenciamento", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        painelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        
        // Painel central com campos
        JPanel painelCentro = new JPanel();
        painelCentro.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelCentro.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        painelCentro.add(txtEmail, gbc);
        
        // Senha
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painelCentro.add(new JLabel("Senha:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtSenha = new JPasswordField(20);
        painelCentro.add(txtSenha, gbc);
        
        // Mensagem
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        lblMensagem = new JLabel(" ", SwingConstants.CENTER);
        lblMensagem.setForeground(Color.RED);
        painelCentro.add(lblMensagem, gbc);
        
        painelPrincipal.add(painelCentro, BorderLayout.CENTER);
        
        // Painel inferior com botão
        JPanel painelInferior = new JPanel();
        painelInferior.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        btnEntrar = new JButton("Entrar");
        btnEntrar.setPreferredSize(new Dimension(150, 35));
        btnEntrar.addActionListener(e -> realizarLogin());
        painelInferior.add(btnEntrar);
        
        painelPrincipal.add(painelInferior, BorderLayout.SOUTH);
        
        // Adiciona painel principal
        add(painelPrincipal);
        
        // Enter para fazer login
        txtSenha.addActionListener(e -> realizarLogin());
        
        // Informações de login padrão
        JLabel lblInfo = new JLabel("<html><center>Login padrão:<br>admin@sistema.com / admin123</center></html>");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 10));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        painelPrincipal.add(lblInfo, BorderLayout.PAGE_END);
    }
    
    private void realizarLogin()
    {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        
        // Validações
        if (email.isEmpty() || senha.isEmpty())
        {
            lblMensagem.setText("Preencha todos os campos!");
            return;
        }
        
        try
        {
            Usuario usuario = usuarioDAO.validarLogin(email, senha);
            
            if (usuario != null)
            {
                lblMensagem.setText("");
                // Abre menu principal
                MenuPrincipalView menu = new MenuPrincipalView(usuario);
                menu.setVisible(true);
                // Fecha tela de login
                dispose();
            }
            else
            {
                lblMensagem.setText("Email ou senha inválidos!");
                txtSenha.setText("");
                txtSenha.requestFocus();
            }
            
        }
        catch (SQLException ex)
        {
            lblMensagem.setText("Erro ao conectar ao banco!");
            JOptionPane.showMessageDialog(this,
                "Erro ao realizar login: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            LoginView login = new LoginView();
            login.setVisible(true);
        });
    }
}