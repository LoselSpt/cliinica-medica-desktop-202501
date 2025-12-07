package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.entidades.Pessoa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDao extends BaseDao {

    public boolean agendar(Consulta consulta) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtCheck = null;
        PreparedStatement stmtInsert = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Check for conflict
            String sqlCheck = "SELECT COUNT(*) FROM consultas WHERE id_medico = ? AND data_hora = ? AND status != 'CANCELADA'";
            stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setLong(1, consulta.getMedico().getId());
            stmtCheck.setTimestamp(2, Timestamp.valueOf(consulta.getDataHora()));
            rs = stmtCheck.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return false; // Conflict found
            }

            // Insert new consultation
            String sqlInsert = "INSERT INTO consultas (id_medico, id_paciente, id_convenio, id_secretaria, data_hora, is_retorno, status, motivo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmtInsert = conn.prepareStatement(sqlInsert);
            stmtInsert.setLong(1, consulta.getMedico().getId());
            stmtInsert.setLong(2, consulta.getPaciente().getId());

            if (consulta.getConvenio() != null) {
                stmtInsert.setLong(3, consulta.getConvenio().getId());
            } else {
                stmtInsert.setNull(3, Types.BIGINT);
            }

            if (consulta.getSecretaria() != null) {
                // Secretaria ID is the Pessoa ID (PK/FK)
                stmtInsert.setLong(4, consulta.getSecretaria().getId());
            } else {
                stmtInsert.setNull(4, Types.BIGINT);
            }

            stmtInsert.setTimestamp(5, Timestamp.valueOf(consulta.getDataHora()));
            stmtInsert.setBoolean(6, consulta.isRetorno());
            stmtInsert.setString(7, consulta.getStatus());
            stmtInsert.setString(8, consulta.getMotivo());

            stmtInsert.executeUpdate();

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    System.err.println("Erro ao realizar rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    System.err.println("Erro ao restaurar auto-commit: " + ex.getMessage());
                }
            }
            fecharRecursos(conn, stmtInsert, rs);
            if (stmtCheck != null)
                stmtCheck.close();
        }
    }

    public List<Consulta> buscarPorMedico(Long idMedico) throws SQLException {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as nome_paciente, m.crm " +
                "FROM consultas c " +
                "JOIN pessoas p ON c.id_paciente = p.id " +
                "JOIN medico m ON c.id_medico = m.id " +
                "WHERE c.id_medico = ? AND c.status != 'CANCELADA' " +
                "ORDER BY c.data_hora";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idMedico);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Consulta c = new Consulta();
                    c.setId(rs.getLong("id"));
                    c.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                    c.setStatus(rs.getString("status"));
                    c.setMotivo(rs.getString("motivo"));
                    c.setRetorno(rs.getBoolean("is_retorno"));

                    Medico m = new Medico();
                    m.setId(rs.getLong("id_medico"));
                    m.setCrm(rs.getString("crm"));
                    c.setMedico(m);

                    Pessoa p = new Pessoa();
                    p.setId(rs.getLong("id_paciente"));
                    p.setNome(rs.getString("nome_paciente"));
                    c.setPaciente(p);

                    // Load Convenio/Secretaria if needed, skipping for brevity in list

                    consultas.add(c);
                }
            }
        }
        return consultas;
    }

    public void cancelar(Long id) throws SQLException {
        String sql = "UPDATE consultas SET status = 'CANCELADA' WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Consulta> buscarTodas() throws SQLException {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as nome_paciente, m.crm, med_p.nome as nome_medico " +
                "FROM consultas c " +
                "JOIN pessoas p ON c.id_paciente = p.id " +
                "JOIN medico m ON c.id_medico = m.id " +
                "JOIN pessoas med_p ON m.id_pessoa = med_p.id " +
                "ORDER BY c.data_hora DESC";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Consulta c = new Consulta();
                c.setId(rs.getLong("id"));
                c.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                c.setStatus(rs.getString("status"));
                c.setMotivo(rs.getString("motivo"));
                c.setRetorno(rs.getBoolean("is_retorno"));

                Medico m = new Medico();
                m.setId(rs.getLong("id_medico"));
                m.setCrm(rs.getString("crm"));
                m.setNome(rs.getString("nome_medico"));
                c.setMedico(m);

                Pessoa p = new Pessoa();
                p.setId(rs.getLong("id_paciente"));
                p.setNome(rs.getString("nome_paciente"));
                c.setPaciente(p);

                consultas.add(c);
            }
        }
        return consultas;
    }
}
