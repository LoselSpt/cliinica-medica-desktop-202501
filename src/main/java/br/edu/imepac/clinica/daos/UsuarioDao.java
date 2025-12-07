package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.EnumFuncionalidades;
import br.edu.imepac.clinica.entidades.EnumStatusUsuario;
import br.edu.imepac.clinica.entidades.Perfil;
import br.edu.imepac.clinica.entidades.Pessoa;
import br.edu.imepac.clinica.entidades.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class UsuarioDao extends BaseDao {

    public Usuario autenticar(String login, String senha) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            // 1. Fetch User and Profile basic info
            String sql = "SELECT u.id, u.login, u.senha, u.status, " +
                    "p.id as id_perfil, p.nome as nome_perfil, " +
                    "pes.id as id_pessoa, pes.nome as nome_pessoa, pes.email, pes.telefone " +
                    "FROM usuarios u " +
                    "JOIN perfis p ON u.id_perfil = p.id " +
                    "JOIN pessoas pes ON u.id_funcionario = pes.id " +
                    "WHERE u.login = ? AND u.senha = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, senha);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getLong("id"));
                usuario.setLogin(rs.getString("login"));
                usuario.setSenha(rs.getString("senha"));
                usuario.setStatus(EnumStatusUsuario.valueOf(rs.getString("status")));

                Pessoa funcionario = new Pessoa();
                funcionario.setId(rs.getLong("id_pessoa"));
                funcionario.setNome(rs.getString("nome_pessoa"));
                funcionario.setEmail(rs.getString("email"));
                funcionario.setTelefone(rs.getString("telefone"));
                usuario.setFuncionario(funcionario);

                Perfil perfil = new Perfil();
                perfil.setId(rs.getLong("id_perfil"));
                perfil.setNome(rs.getString("nome_perfil"));

                // 2. Fetch Functionalities
                Set<EnumFuncionalidades> funcs = buscarFuncionalidades(conn, perfil.getId());
                perfil.setFuncionalidades(funcs);

                usuario.setPerfil(perfil);
                return usuario;
            }
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
        return null;
    }

    private Set<EnumFuncionalidades> buscarFuncionalidades(Connection conn, Long idPerfil) throws SQLException {
        Set<EnumFuncionalidades> funcs = new HashSet<>();
        String sql = "SELECT funcionalidade FROM perfil_funcionalidades WHERE id_perfil = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPerfil);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        funcs.add(EnumFuncionalidades.valueOf(rs.getString("funcionalidade")));
                    } catch (IllegalArgumentException e) {
                        // Ignore invalid enums in DB
                        System.err.println("Funcionalidade inválida no banco: " + rs.getString("funcionalidade"));
                    }
                }
            }
        }
        return funcs;
    }
}
