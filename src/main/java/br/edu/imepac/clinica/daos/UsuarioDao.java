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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    public void save(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (login, senha, status, id_perfil, id_funcionario) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getStatus().name());
            stmt.setLong(4, usuario.getPerfil().getId());
            stmt.setLong(5, usuario.getFuncionario().getId());

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getLong(1));
            }
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
    }

    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET login = ?, senha = ?, status = ?, id_perfil = ?, id_funcionario = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getLogin());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getStatus().name());
            stmt.setLong(4, usuario.getPerfil().getId());
            stmt.setLong(5, usuario.getFuncionario().getId());
            stmt.setLong(6, usuario.getId());

            stmt.executeUpdate();
        } finally {
            fecharRecursos(conn, stmt, null);
        }
    }

    public Usuario findByLogin(String login) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE login = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // Simplified fetch for checking existence
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setLogin(rs.getString("login"));
                return u;
            }
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
        return null;
    }

    public List<Usuario> buscarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        // Updated SQL to fetch IDs
        String sql = "SELECT u.id, u.login, u.status, u.senha, " +
                "p.id as id_perfil, p.nome as nome_perfil, " +
                "pes.id as id_pessoa, pes.nome as nome_pessoa " +
                "FROM usuarios u " +
                "JOIN perfis p ON u.id_perfil = p.id " +
                "JOIN pessoas pes ON u.id_funcionario = pes.id";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getLong("id"));
                u.setLogin(rs.getString("login"));
                u.setSenha(rs.getString("senha")); // Fetch password too if needed for update logic
                u.setStatus(EnumStatusUsuario.valueOf(rs.getString("status")));

                Perfil perfil = new Perfil();
                perfil.setId(rs.getLong("id_perfil")); // Set ID
                perfil.setNome(rs.getString("nome_perfil"));
                u.setPerfil(perfil);

                Pessoa pessoa = new Pessoa();
                pessoa.setId(rs.getLong("id_pessoa")); // Set ID
                pessoa.setNome(rs.getString("nome_pessoa"));
                u.setFuncionario(pessoa);

                usuarios.add(u);
            }
        }
        return usuarios;
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
