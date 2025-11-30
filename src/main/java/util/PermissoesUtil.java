package util;

import model.Usuario;
import java.util.Arrays;
import java.util.List;

public class PermissoesUtil
{
    
    // Tipos de permissões disponíveis
    public static final String PERMISSAO_CRIAR = "CRIAR";
    public static final String PERMISSAO_LER = "LER";
    public static final String PERMISSAO_ATUALIZAR = "ATUALIZAR";
    public static final String PERMISSAO_EXCLUIR = "EXCLUIR";
    public static final String PERMISSAO_GERENCIAR_USUARIOS = "GERENCIAR_USUARIOS";
    public static final String PERMISSAO_RELATORIOS = "RELATORIOS";
    
    /**
     * Verifica se o usuário tem uma permissão específica
     * @param usuario Usuario logado
     * @param permissao Permissão a verificar (ex: "CRIAR", "EXCLUIR")
     * @return true se o usuário tem a permissão
     */
    public static boolean temPermissao(Usuario usuario, String permissao) 
    {
        if (usuario == null || usuario.getPermissoes() == null) 
        {
            return false;
        }
        
        // Converte a string de permissões em lista
        List<String> permissoes = Arrays.asList(
            usuario.getPermissoes().toUpperCase().split(",")
        );
        
        // Verifica se a permissão existe na lista
        return permissoes.stream()
                .map(String::trim)
                .anyMatch(p -> p.equals(permissao.toUpperCase()));
    }
    
    /**
     * Verifica se o usuário pode criar registros
     */
    public static boolean podeCriar(Usuario usuario) 
    {
        return temPermissao(usuario, PERMISSAO_CRIAR);
    }
    
    /**
     * Verifica se o usuário pode ler/visualizar registros
     */
    public static boolean podeLer(Usuario usuario) 
    {
        return temPermissao(usuario, PERMISSAO_LER);
    }
    
    /**
     * Verifica se o usuário pode atualizar registros
     */
    public static boolean podeAtualizar(Usuario usuario) 
    {
        return temPermissao(usuario, PERMISSAO_ATUALIZAR);
    }
    
    /**
     * Verifica se o usuário pode excluir registros
     */
    public static boolean podeExcluir(Usuario usuario) 
    {
        return temPermissao(usuario, PERMISSAO_EXCLUIR);
    }
    
    /**
     * Verifica se o usuário pode gerenciar outros usuários
     */
    public static boolean podeGerenciarUsuarios(Usuario usuario) 
    {
        return temPermissao(usuario, PERMISSAO_GERENCIAR_USUARIOS);
    }
    
    /**
     * Verifica se o usuário é administrador (tem todas as permissões)
     */
    public static boolean isAdministrador(Usuario usuario) 
    {
        return usuario != null && 
               usuario.getNomePerfil() != null &&
               usuario.getNomePerfil().equalsIgnoreCase("Administrador");
    }
    
    /**
     * Lista todas as permissões do usuário
     * @param usuario Usuario logado
     * @return Lista de permissões
     */
    public static List<String> listarPermissoes(Usuario usuario) 
    {
        if (usuario == null || usuario.getPermissoes() == null) 
        {
            return Arrays.asList();
        }
        
        return Arrays.asList(
            usuario.getPermissoes().toUpperCase().split(",")
        );
    }
    
    /**
     * Retorna descrição amigável da permissão
     */
    public static String getDescricaoPermissao(String permissao) 
    {
        switch (permissao.toUpperCase()) 
        {
            case PERMISSAO_CRIAR:
                return "Criar novos registros";
            case PERMISSAO_LER:
                return "Visualizar registros";
            case PERMISSAO_ATUALIZAR:
                return "Editar registros existentes";
            case PERMISSAO_EXCLUIR:
                return "Excluir registros";
            case PERMISSAO_GERENCIAR_USUARIOS:
                return "Gerenciar usuários do sistema";
            case PERMISSAO_RELATORIOS:
                return "Gerar relatórios";
            default:
                return permissao;
        }
    }
}