package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.entidades.Medico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MedicoDao extends BaseDao {

    public List<Medico> buscarTodos() throws SQLException {
        List<Medico> medicos = new ArrayList<>();
        String sql = "SELECT m.id, m.crm, p.id as id_pessoa, p.nome, p.telefone, p.email, " +
                "e.id as id_especialidade, e.nome as nome_especialidade, e.descricao " +
                "FROM medico m " +
                "JOIN pessoas p ON m.id_pessoa = p.id " +
                "JOIN especialidade e ON m.id_especialidade = e.id";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Especialidade especialidade = new Especialidade();
                especialidade.setId(rs.getLong("id_especialidade"));
                especialidade.setNome(rs.getString("nome_especialidade"));
                especialidade.setDescricao(rs.getString("descricao"));

                Medico medico = new Medico();
                medico.setId(rs.getLong("id"));
                medico.setCrm(rs.getString("crm"));
                medico.setNome(rs.getString("nome"));
                medico.setTelefone(rs.getString("telefone"));
                medico.setEmail(rs.getString("email"));
                medico.setEspecialidade(especialidade);

                medicos.add(medico);
            }
        } finally {
            fecharRecursos(conn, stmt, rs);
        }

        return medicos;
    }

    public void salvar(Medico medico) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtPessoa = null;
        PreparedStatement stmtMedico = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // 1. Inserir Pessoa
            String sqlPessoa = "INSERT INTO pessoas (nome, telefone, email) VALUES (?, ?, ?)";
            stmtPessoa = conn.prepareStatement(sqlPessoa, PreparedStatement.RETURN_GENERATED_KEYS);
            stmtPessoa.setString(1, medico.getNome());
            stmtPessoa.setString(2, medico.getTelefone());
            stmtPessoa.setString(3, medico.getEmail());
            stmtPessoa.executeUpdate();

            rs = stmtPessoa.getGeneratedKeys();
            if (rs.next()) {
                medico.setId(rs.getLong(1));
            } else {
                throw new SQLException("Falha ao criar Pessoa, nenhum ID obtido.");
            }

            // 2. Inserir Medico
            String sqlMedico = "INSERT INTO medico (crm, id_pessoa, id_especialidade) VALUES (?, ?, ?)";
            stmtMedico = conn.prepareStatement(sqlMedico);
            stmtMedico.setString(1, medico.getCrm());
            stmtMedico.setLong(2, medico.getId()); // Usar ID de Pessoa

            if (medico.getEspecialidade() != null) {
                stmtMedico.setLong(3, medico.getEspecialidade().getId());
            } else {
                stmtMedico.setNull(3, Types.BIGINT);
            }

            stmtMedico.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            fecharRecursos(conn, stmtPessoa, rs);
            if (stmtMedico != null)
                stmtMedico.close();
        }
    }

    public void salvarParaPessoaExistente(Medico medico) throws SQLException {
        String sql = "INSERT INTO medico (crm, id_pessoa, id_especialidade) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, medico.getCrm());
            stmt.setLong(2, medico.getId()); // ID herdado de Pessoa

            if (medico.getEspecialidade() != null) {
                stmt.setLong(3, medico.getEspecialidade().getId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }

            stmt.executeUpdate();
        }
    }

    public void atualizar(Medico medico) throws SQLException {
        // Não implementado
    }

    public Medico buscarPorIdPessoa(Long idPessoa) throws SQLException {
        String sql = "SELECT m.id, m.crm, p.id as id_pessoa, p.nome, p.telefone, p.email, " +
                "e.id as id_especialidade, e.nome as nome_especialidade, e.descricao " +
                "FROM medico m " +
                "JOIN pessoas p ON m.id_pessoa = p.id " +
                "LEFT JOIN especialidade e ON m.id_especialidade = e.id " + // Alterado para LEFT JOIN caso seja nulo
                "WHERE m.id_pessoa = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, idPessoa);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Especialidade especialidade = null;
                if (rs.getLong("id_especialidade") != 0) {
                    especialidade = new Especialidade();
                    especialidade.setId(rs.getLong("id_especialidade"));
                    especialidade.setNome(rs.getString("nome_especialidade"));
                    especialidade.setDescricao(rs.getString("descricao"));
                }

                Medico medico = new Medico();
                medico.setId(rs.getLong("id"));
                medico.setCrm(rs.getString("crm"));
                medico.setNome(rs.getString("nome"));
                medico.setTelefone(rs.getString("telefone"));
                medico.setEmail(rs.getString("email"));
                medico.setEspecialidade(especialidade);

                return medico;
            }
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
        return null;
    }
}
