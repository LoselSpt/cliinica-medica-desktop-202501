package br.edu.imepac.clinica.utils;

import br.edu.imepac.clinica.daos.BaseDao;
import br.edu.imepac.clinica.daos.PerfilDao;
import br.edu.imepac.clinica.daos.PessoaDao;
import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.EnumFuncionalidades;
import br.edu.imepac.clinica.entidades.EnumStatusUsuario;
import br.edu.imepac.clinica.entidades.Perfil;
import br.edu.imepac.clinica.entidades.Pessoa;
import br.edu.imepac.clinica.entidades.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SeedDatabase extends BaseDao {

    public static void main(String[] args) {
        SeedDatabase seeder = new SeedDatabase();
        try {
            seeder.seed();
            System.out.println("Database seeded successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void seed() throws SQLException {
        Connection conn = getConnection();

        // 1. Create Profile 'Gerente' if not exists
        Perfil perfilGerente = ensurePerfil(conn, "Gerente");

        // 2. Add Permissions to Gerente
        addPermission(conn, perfilGerente.getId(), EnumFuncionalidades.CADASTRAR_MEDICO);
        addPermission(conn, perfilGerente.getId(), EnumFuncionalidades.CADASTRAR_CONVENIO);
        addPermission(conn, perfilGerente.getId(), EnumFuncionalidades.CADASTRAR_FUNCIONARIO);
        addPermission(conn, perfilGerente.getId(), EnumFuncionalidades.CADASTRAR_ESPECIALIDADE);
        addPermission(conn, perfilGerente.getId(), EnumFuncionalidades.CADASTRAR_USUARIO);

        // 3. Create User 'Losel'
        UsuarioDao usuarioDao = new UsuarioDao();
        Usuario existingUser = usuarioDao.findByLogin("losel");

        if (existingUser != null) {
            System.out.println("User 'losel' already exists. Updating profile...");
            existingUser.setPerfil(perfilGerente);
            // We need to fetch the full object to update correctly or just update the
            // profile ID directly via SQL for simplicity
            // But let's try to use the DAO update if possible.
            // The DAO update requires a full object.
            // Let's just update the profile of the existing user directly here for
            // simplicity and robustness
            updateUserProfile(conn, existingUser.getId(), perfilGerente.getId());
        } else {
            System.out.println("Creating user 'losel'...");
            PessoaDao pessoaDao = new PessoaDao();
            Pessoa pessoa = new Pessoa();
            pessoa.setNome("Losel Manager");
            pessoa.setEmail("losel@manager.com");
            pessoaDao.salvar(pessoa);

            Usuario newUser = new Usuario();
            newUser.setLogin("losel");
            newUser.setSenha("losel123");
            newUser.setStatus(EnumStatusUsuario.ATIVO);
            newUser.setPerfil(perfilGerente);
            newUser.setFuncionario(pessoa);

            usuarioDao.save(newUser);
        }

        // 4. Create Profile 'Secretária' if not exists and add permissions
        Perfil perfilSecretaria = ensurePerfil(conn, "Secretária");
        addPermission(conn, perfilSecretaria.getId(), EnumFuncionalidades.AGENDAR_CONSULTA);
        addPermission(conn, perfilSecretaria.getId(), EnumFuncionalidades.CADASTRAR_PACIENTE);
        addPermission(conn, perfilSecretaria.getId(), EnumFuncionalidades.DELETAR_CONSULTA);

        conn.close();
    }

    private Perfil ensurePerfil(Connection conn, String nome) throws SQLException {
        PerfilDao dao = new PerfilDao();
        Perfil p = dao.findByName(nome);
        if (p != null)
            return p;

        String sql = "INSERT INTO perfis (nome) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p = new Perfil();
                    p.setId(rs.getLong(1));
                    p.setNome(nome);
                    return p;
                }
            }
        }
        return null;
    }

    private void addPermission(Connection conn, Long idPerfil, EnumFuncionalidades func) throws SQLException {
        String checkSql = "SELECT 1 FROM perfil_funcionalidades WHERE id_perfil = ? AND funcionalidade = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setLong(1, idPerfil);
            stmt.setString(2, func.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return; // Already exists
            }
        }

        String sql = "INSERT INTO perfil_funcionalidades (id_perfil, funcionalidade) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idPerfil);
            stmt.setString(2, func.name());
            stmt.executeUpdate();
        }
    }

    private void updateUserProfile(Connection conn, Long userId, Long perfilId) throws SQLException {
        String sql = "UPDATE usuarios SET id_perfil = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, perfilId);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        }
    }
}
