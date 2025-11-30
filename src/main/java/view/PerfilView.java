package view;

import dao.PerfilDAO;
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
 * Tela de CRUD de Perfis
 */
public class PerfilView extends JFrame {
    
    private JTable tabelaPerfis;
    private DefaultTableModel modeloTabela;
    private JTextField txtNomePerfil, txtDescricao, txtPermissoes;
    private JButton btnNovo, btnSalvar, btnEditar, btnExcluir, btnCancelar;
    
    private PerfilDAO perfilDAO;
    private Perfil perfilSelecionado;
    private Usuario usuarioLogado; // Usuário que está usando o sistema
    private boolean modoEdicao = false;
    
    public PerfilView(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        perfilDAO = new PerfilDAO();
        inicializarComponentes();
        carregarPerfis();
        aplicarPermissoes(); // Apenas administradores podem gerenciar perfis
    }
    
    private void inicializarComponentes() {
        setTitle("Gerenciamento de Perfis");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout(10, 10));
        
        // Painel formulário
        JPanel painelFormulario = criarPainelFormulario();
        add(painelFormulario, BorderLayout.NORTH);
        
        // Painel tabela
        JPanel painelTabela = criarPainelTabela();
        add(painelTabela, BorderLayout.CENTER);
        
        // Painel botões
        JPanel painelBotoes = criarPainelBotoes();
        add(painelBotoes, BorderLayout.SOUTH);
        
        habilitarCampos(false);
    }
    
    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setLayout(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Perfil"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nome do Perfil
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nome do Perfil:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNomePerfil = new JTextField(20);
        painel.add(txtNomePerfil, gbc);
        
        // Descrição
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painel.add(new JLabel("Descrição:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDescricao = new JTextField(20);
        painel.add(txtDescricao, gbc);
        
        // Permissões
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        painel.add(new JLabel("Permissões:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPermissoes = new JTextField(20);
        JLabel lblHint = new JLabel("Ex: CRIAR,LER,ATUALIZAR,EXCLUIR");
        lblHint.setFont(new Font("Arial", Font.ITALIC, 10));
        lblHint.setForeground(Color.GRAY);
        
        JPanel painelPermissoes = new JPanel(new BorderLayout());
        painelPermissoes.add(txtPermissoes, BorderLayout.CENTER);
        painelPermissoes.add(lblHint, BorderLayout.SOUTH);
        painel.add(painelPermissoes, gbc);
        
        return painel;
    }
    
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Perfis Cadastrados"));
        
        String[] colunas = {"ID", "Nome do Perfil", "Descrição", "Permissões", "Data Criação"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaPerfis = new JTable(modeloTabela);
        tabelaPerfis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPerfis.getTableHeader().setReorderingAllowed(false);
        
        tabelaPerfis.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaPerfis.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabelaPerfis.getColumnModel().getColumn(2).setPreferredWidth(180);
        tabelaPerfis.getColumnModel().getColumn(3).setPreferredWidth(200);
        tabelaPerfis.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(tabelaPerfis);
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
    
    private void carregarPerfis() {
        try {
            List<Perfil> perfis = perfilDAO.listarTodos();
            modeloTabela.setRowCount(0);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (Perfil perfil : perfis) {
                Object[] linha = {
                    perfil.getId(),
                    perfil.getNomePerfil(),
                    perfil.getDescricao(),
                    perfil.getPermissoes(),
                    perfil.getDataCriacao() != null ? sdf.format(perfil.getDataCriacao()) : ""
                };
                modeloTabela.addRow(linha);
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
        perfilSelecionado = null;
        txtNomePerfil.requestFocus();
    }
    
    private void salvar() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            Perfil perfil = new Perfil();
            perfil.setNomePerfil(txtNomePerfil.getText().trim());
            perfil.setDescricao(txtDescricao.getText().trim());
            perfil.setPermissoes(txtPermissoes.getText().trim());
            
            boolean sucesso;
            
            if (modoEdicao) {
                perfil.setId(perfilSelecionado.getId());
                sucesso = perfilDAO.atualizar(perfil);
            } else {
                sucesso = perfilDAO.inserir(perfil);
            }
            
            if (sucesso) {
                JOptionPane.showMessageDialog(this,
                    "Perfil salvo com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                carregarPerfis();
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro ao salvar perfil!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar perfil: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editar() {
        int linhaSelecionada = tabelaPerfis.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecione um perfil para editar!",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            perfilSelecionado = perfilDAO.buscarPorId(id);
            
            if (perfilSelecionado != null) {
                txtNomePerfil.setText(perfilSelecionado.getNomePerfil());
                txtDescricao.setText(perfilSelecionado.getDescricao());
                txtPermissoes.setText(perfilSelecionado.getPermissoes());
                
                habilitarCampos(true);
                modoEdicao = true;
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao buscar perfil: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluir() {
        int linhaSelecionada = tabelaPerfis.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecione um perfil para excluir!",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int opcao = JOptionPane.showConfirmDialog(this,
            "Deseja realmente excluir este perfil?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION);
        
        if (opcao == JOptionPane.YES_OPTION) {
            try {
                int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
                
                if (perfilDAO.excluir(id)) {
                    JOptionPane.showMessageDialog(this,
                        "Perfil excluído com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    carregarPerfis();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao excluir perfil!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao excluir perfil: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelar() {
        limparCampos();
        habilitarCampos(false);
        modoEdicao = false;
        perfilSelecionado = null;
        tabelaPerfis.clearSelection();
    }
    
    private boolean validarCampos() {
        if (txtNomePerfil.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do perfil!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNomePerfil.requestFocus();
            return false;
        }
        
        if (txtDescricao.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a descrição!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtDescricao.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void limparCampos() {
        txtNomePerfil.setText("");
        txtDescricao.setText("");
        txtPermissoes.setText("");
    }
    
    private void habilitarCampos(boolean habilitar) {
        txtNomePerfil.setEnabled(habilitar);
        txtDescricao.setEnabled(habilitar);
        txtPermissoes.setEnabled(habilitar);
        btnSalvar.setEnabled(habilitar);
        btnCancelar.setEnabled(habilitar);
    }
    
    /**
     * Aplica permissões - apenas administradores podem gerenciar perfis
     */
    private void aplicarPermissoes() {
        if (!PermissoesUtil.isAdministrador(usuarioLogado)) {
            // Desabilita todos os botões exceto visualização
            btnNovo.setEnabled(false);
            btnEditar.setEnabled(false);
            btnExcluir.setEnabled(false);
            setTitle("Visualização de Perfis (Somente Leitura)");
            
            JOptionPane.showMessageDialog(this,
                "Você não tem permissão para gerenciar perfis!\nApenas Administradores podem editar perfis.",
                "Acesso Restrito",
                JOptionPane.WARNING_MESSAGE);
        }
    }
}