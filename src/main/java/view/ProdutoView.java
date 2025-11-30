package view;

import dao.ProdutoDAO;
import model.Produto;
import model.Usuario;
import util.PermissoesUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Tela de CRUD de Produtos
 */
public class ProdutoView extends JFrame {
    
    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private JTextField txtNome, txtDescricao, txtPreco, txtQuantidade;
    private JButton btnNovo, btnSalvar, btnEditar, btnExcluir, btnCancelar;
    
    private ProdutoDAO produtoDAO;
    private Produto produtoSelecionado;
    private Usuario usuarioLogado; // Usuário que está usando o sistema
    private boolean modoEdicao = false;
    
    public ProdutoView(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        produtoDAO = new ProdutoDAO();
        inicializarComponentes();
        carregarProdutos();
        aplicarPermissoes(); // Controla acesso aos botões
    }
    
    private void inicializarComponentes() {
        setTitle("Gerenciamento de Produtos");
        setSize(900, 600);
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
        painel.setBorder(BorderFactory.createTitledBorder("Dados do Produto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nome:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        painel.add(txtNome, gbc);
        
        // Descrição
        gbc.gridx = 2; gbc.weightx = 0;
        painel.add(new JLabel("Descrição:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtDescricao = new JTextField(20);
        painel.add(txtDescricao, gbc);
        
        // Preço
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        painel.add(new JLabel("Preço (R$):"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPreco = new JTextField(10);
        painel.add(txtPreco, gbc);
        
        // Quantidade em Estoque
        gbc.gridx = 2; gbc.weightx = 0;
        painel.add(new JLabel("Quantidade:"), gbc);
        
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtQuantidade = new JTextField(10);
        painel.add(txtQuantidade, gbc);
        
        return painel;
    }
    
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Produtos Cadastrados"));
        
        String[] colunas = {"ID", "Nome", "Descrição", "Preço (R$)", "Quantidade", "Data Cadastro"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaProdutos.getTableHeader().setReorderingAllowed(false);
        
        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabelaProdutos.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabelaProdutos.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabelaProdutos.getColumnModel().getColumn(5).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
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
    
    private void carregarProdutos() {
        try {
            List<Produto> produtos = produtoDAO.listarTodos();
            modeloTabela.setRowCount(0);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            
            for (Produto produto : produtos) {
                Object[] linha = {
                    produto.getId(),
                    produto.getNome(),
                    produto.getDescricao(),
                    String.format("%.2f", produto.getPreco()),
                    produto.getQuantidadeEstoque(),
                    sdf.format(produto.getDataCadastro())
                };
                modeloTabela.addRow(linha);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar produtos: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void novo() {
        limparCampos();
        habilitarCampos(true);
        modoEdicao = false;
        produtoSelecionado = null;
        txtNome.requestFocus();
    }
    
    private void salvar() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            Produto produto = new Produto();
            produto.setNome(txtNome.getText().trim());
            produto.setDescricao(txtDescricao.getText().trim());
            produto.setPreco(new BigDecimal(txtPreco.getText().replace(",", ".")));
            produto.setQuantidadeEstoque(Integer.parseInt(txtQuantidade.getText()));
            
            boolean sucesso;
            
            if (modoEdicao) {
                produto.setId(produtoSelecionado.getId());
                sucesso = produtoDAO.atualizar(produto);
            } else {
                sucesso = produtoDAO.inserir(produto);
            }
            
            if (sucesso) {
                JOptionPane.showMessageDialog(this,
                    "Produto salvo com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                carregarProdutos();
                cancelar();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erro ao salvar produto!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Formato inválido para preço ou quantidade!",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar produto: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editar() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecione um produto para editar!",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            produtoSelecionado = produtoDAO.buscarPorId(id);
            
            if (produtoSelecionado != null) {
                txtNome.setText(produtoSelecionado.getNome());
                txtDescricao.setText(produtoSelecionado.getDescricao());
                txtPreco.setText(produtoSelecionado.getPreco().toString().replace(".", ","));
                txtQuantidade.setText(String.valueOf(produtoSelecionado.getQuantidadeEstoque()));
                
                habilitarCampos(true);
                modoEdicao = true;
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erro ao buscar produto: " + ex.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void excluir() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecione um produto para excluir!",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int opcao = JOptionPane.showConfirmDialog(this,
            "Deseja realmente excluir este produto?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION);
        
        if (opcao == JOptionPane.YES_OPTION) {
            try {
                int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
                
                if (produtoDAO.excluir(id)) {
                    JOptionPane.showMessageDialog(this,
                        "Produto excluído com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    carregarProdutos();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Erro ao excluir produto!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao excluir produto: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cancelar() {
        limparCampos();
        habilitarCampos(false);
        modoEdicao = false;
        produtoSelecionado = null;
        tabelaProdutos.clearSelection();
    }
    
    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do produto!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
            return false;
        }
        
        if (txtPreco.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o preço!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtPreco.requestFocus();
            return false;
        }
        
        try {
            BigDecimal preco = new BigDecimal(txtPreco.getText().replace(",", "."));
            if (preco.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Preço deve ser maior que zero!", "Aviso", JOptionPane.WARNING_MESSAGE);
                txtPreco.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço inválido!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtPreco.requestFocus();
            return false;
        }
        
        if (txtQuantidade.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a quantidade!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtQuantidade.requestFocus();
            return false;
        }
        
        try {
            int quantidade = Integer.parseInt(txtQuantidade.getText());
            if (quantidade < 0) {
                JOptionPane.showMessageDialog(this, "Quantidade não pode ser negativa!", "Aviso", JOptionPane.WARNING_MESSAGE);
                txtQuantidade.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida!", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtQuantidade.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void limparCampos() {
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtQuantidade.setText("");
    }
    
    private void habilitarCampos(boolean habilitar) {
        txtNome.setEnabled(habilitar);
        txtDescricao.setEnabled(habilitar);
        txtPreco.setEnabled(habilitar);
        txtQuantidade.setEnabled(habilitar);
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
            btnNovo.setToolTipText("Você não tem permissão para criar produtos");
        }
        
        // Botão Editar - precisa permissão ATUALIZAR
        if (!PermissoesUtil.podeAtualizar(usuarioLogado)) {
            btnEditar.setEnabled(false);
            btnEditar.setToolTipText("Você não tem permissão para editar produtos");
        }
        
        // Botão Excluir - precisa permissão EXCLUIR
        if (!PermissoesUtil.podeExcluir(usuarioLogado)) {
            btnExcluir.setEnabled(false);
            btnExcluir.setToolTipText("Você não tem permissão para excluir produtos");
        }
        
        // Visitantes só podem visualizar
        if (!PermissoesUtil.podeCriar(usuarioLogado) && 
            !PermissoesUtil.podeAtualizar(usuarioLogado) && 
            !PermissoesUtil.podeExcluir(usuarioLogado)) {
            setTitle("Visualização de Produtos (Somente Leitura)");
        }
    }
}