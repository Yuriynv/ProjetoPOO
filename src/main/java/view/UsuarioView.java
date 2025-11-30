package view;

import dao.PerfilDAO;
import dao.UsuarioDAO;
import model.Perfil;
import model.Usuario;
import util.PermissoesUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Tela de CRUD de Usuários
 */
public class UsuarioView extends JFrame {
    
    private JTable tabelaUsuarios;
    private DefaultTableModel modeloTabela;
    private JTextField txtNome, txtEmail, txtSenha;
    private JComboBox<Perfil> comboPerfil;
    private JButton btnNovo, btnSalvar, btnEditar, btnExcluir, btnCancelar;
    
    private UsuarioDAO usuarioDAO;
    private PerfilDAO perfilDAO;
    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado; // Usuário que está usando o sistema
    private boolean modoEdicao = false;
    
    public UsuarioView(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        usuarioDAO = new UsuarioDAO();
        perfilDAO = new PerfilDAO();
        inicializarComponentes();
        carregarUsuarios();
        carregarPerfis();
        aplicarPermissoes(); // Controla acesso aos botões
    }
    
    private void inicializarComponentes() {
        setTitle("Gerenciamento de Usuários");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Layout principal
        setLayout(new BorderLayout(10, 10));
        
        // Painel superior - Formulário
        JPanel painelFormulario = criarPainelFormulario();
        add(painelFormulario, BorderLayout.NORTH);
        
        // Painel central - Tabela
        JPanel painelTabela = criarPainelTabela();
        add(painelTabela, BorderLayout.CENTER);
        
        // Painel inferior - Botões de ação
        JPanel painelBotoes = criarPainelBotoes();
        add(painelBotoes, BorderLayout.SOUTH);
        
        // Estado inicial
        habilitarCampos(false);
    }
    
    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setLayout(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nome:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        painel.add(txtNome, gbc);
        
        // Email
        gbc.gridx = 2; gbc.weightx = 0;
        painel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        painel.add(txtEmail, gbc);
        
        // Senha
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painel.add(new JLabel("Senha:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtSenha = new JPasswordField(20);
        painel.add(txtSenha, gbc);
        
        // Perfil
        gbc.gridx = 2; gbc.weightx = 0;
        painel.add(new JLabel("Perfil:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 1.0;
        comboPerfil = new JComboBox<>();
        painel.add(comboPerfil, gbc);
        
        return painel;
    }
    
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Usuários Cadastrados"));
        
        // Modelo da tabela
        String[] colunas = {"ID", "Nome", "Email", "Perfil", "Data Cadastro"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaUsuarios = new JTable(modeloTabela);
        tabelaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaUsuarios.getTableHeader().setReorderingAllowed(false);
        
        // Ajusta largura das colunas
        tabelaUsuarios.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaUsuarios.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabelaUsuarios.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabelaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(100);
        tabelaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
        painel.add(scrollPane, BorderLayout.CENTER);
        
        return painel;
    }
    
    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnNovo = new JButton("Novo");
        btnNovo.addActionListener(e -> novo());
        painel.add(btnNovo);
        
        btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());
        painel.add(btnSalvar);
        
        btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editar());
        painel.add(btnEditar);
        
        btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluir());
        painel.add(btnExcluir);
        
        btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cancelar());
        painel.add(btnCancelar);
        
        return painel;
    }
    
    private void carregarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            modeloTabela.setRowCount(0);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (Usuario usuario : usuarios) {
                Object[] linha = {
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getNomePerfil(),
                    sdf.format(usuario.getDataCadastro())
                };
                modeloTabela.addRow(linha);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar usuários: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void carregarPerfis() {
        try {
            List<Perfil> perfis = perfilDAO.listarTodos();
            comboPerfil.removeAllItems();
            
            for (Perfil perfil : perfis) {
                comboPerfil.addItem(perfil);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar perfis: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void novo() {
        limparCampos();
        habilitarCampos(true);
        modoEdicao = false;
        usuarioSelecionado = null;
        txtNome.requestFocus();
    }
    
    private void salvar() {
        // Validações
        if (!validarCampos()) {
            return;
        }
        
        try {
            Usuario usuario = new Usuario();
            usuario.setNome(txtNome.getText().trim());
            usuario.setEmail(txtEmail.getText().trim());
            usuario.setSenha(txtSenha.getText());
            
            Perfil perfilSelecionado = (Perfil) comboPerfil.getSelectedItem();
            usuario.setIdPerfil(perfilSelecionado.getId());
            
            boolean sucesso;
            
            if (modoEdicao) {
                usuario.setId(usuarioSelecionado.getId());
                boolean atualizarSenha = !txtSenha.getText().isEmpty();
                sucesso = usuarioDAO.atualizar(usuario, atualizarSenha);
            } else {
                sucesso = usuarioDAO.inserir(usuario);
            }
            
            if (sucesso) {
                JOptionPane.showMessageDialog(this,
                    "Usuário salvo com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                carregarUsuarios();
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro ao salvar usuário!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar usuário: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editar() {
        int linhaSelecionada = tabelaUsuarios.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecione um usuário para editar!",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            usuarioSelecionado = usuarioDAO.buscarPorId(id);
            
            if (usuarioSelecionado != null) {
                txtNome.setText(usuarioSelecionado.getNome());
                txtEmail.setText(usuarioSelecionado.getEmail());
                txtSenha.setText(""); // Não mostra senha
                
                // Seleciona perfil
                for (int i = 0; i < comboPerfil.getItemCount(); i++) {
                    if (comboPerfil.getItemAt(i).getId() == usuarioSelecionado.getIdPerfil()) {
                        comboPerfil.setSelectedIndex(i);
                        break;
                    }
                }
                
                habilitarCampos(true);
                modoEdicao = true;
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao buscar usuário: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluir() {
        int linhaSelecionada = tabelaUsuarios.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecione um usuário para excluir!",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int opcao = JOptionPane.showConfirmDialog(this,
            "Deseja realmente excluir este usuário?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION);
        
        if (opcao == JOptionPane.YES_OPTION) {
            try {
                int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
                
                if (usuarioDAO.excluir(id)) {
                    JOptionPane.showMessageDialog(this,
                        "Usuário excluído com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    carregarUsuarios();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao excluir usuário!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao excluir usuário: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelar() {
        limparCampos();
        habilitarCampos(false);
        modoEdicao = false;
        usuarioSelecionado = null;
        tabelaUsuarios.clearSelection();
    }
    
    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o email!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        if (!txtEmail.getText().contains("@")) {
            JOptionPane.showMessageDialog(this, "Email inválido!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }
        
        if (!modoEdicao && txtSenha.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a senha!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtSenha.requestFocus();
            return false;
        }
        
        if (!txtSenha.getText().isEmpty() && txtSenha.getText().length() < 6) {
            JOptionPane.showMessageDialog(this, "Senha deve ter no mínimo 6 caracteres!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtSenha.requestFocus();
            return false;
        }
        
        if (comboPerfil.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um perfil!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void limparCampos() {
        txtNome.setText("");
        txtEmail.setText("");
        txtSenha.setText("");
        if (comboPerfil.getItemCount() > 0) {
            comboPerfil.setSelectedIndex(0);
        }
    }
    
    private void habilitarCampos(boolean habilitar) {
        txtNome.setEnabled(habilitar);
        txtEmail.setEnabled(habilitar);
        txtSenha.setEnabled(habilitar);
        comboPerfil.setEnabled(habilitar);
        btnSalvar.setEnabled(habilitar);
        btnCancelar.setEnabled(habilitar);
    }
    
    /**
     * Aplica permissões aos botões baseado no perfil do usuário logado
     */
    private void aplicarPermissoes() {
        // Botão Novo - precisa permissão CRIAR
        if (!PermissoesUtil.podeCriar(usuarioLogado)) {
            btnNovo.setEnabled(false);
            btnNovo.setToolTipText("Você não tem permissão para criar usuários");
        }
        
        // Botão Editar - precisa permissão ATUALIZAR
        if (!PermissoesUtil.podeAtualizar(usuarioLogado)) {
            btnEditar.setEnabled(false);
            btnEditar.setToolTipText("Você não tem permissão para editar usuários");
        }
        
        // Botão Excluir - precisa permissão EXCLUIR
        if (!PermissoesUtil.podeExcluir(usuarioLogado)) {
            btnExcluir.setEnabled(false);
            btnExcluir.setToolTipText("Você não tem permissão para excluir usuários");
        }
        
        // Visitantes só podem visualizar
        if (!PermissoesUtil.podeCriar(usuarioLogado) && 
            !PermissoesUtil.podeAtualizar(usuarioLogado) && 
            !PermissoesUtil.podeExcluir(usuarioLogado)) {
            setTitle("Visualização de Usuários (Somente Leitura)");
        }
    }
}