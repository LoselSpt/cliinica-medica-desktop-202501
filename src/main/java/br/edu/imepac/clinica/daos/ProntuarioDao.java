package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Prontuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProntuarioDao extends BaseDao {

    public void salvar(Prontuario prontuario) throws SQLException {
        String sql = "INSERT INTO prontuarios (id_consulta, historico, receituario, exames) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE historico = ?, receituario = ?, exames = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, prontuario.getIdConsulta());
            stmt.setString(2, prontuario.getHistorico());
            stmt.setString(3, prontuario.getReceituario());
            stmt.setString(4, prontuario.getExames());

            // Update part
            stmt.setString(5, prontuario.getHistorico());
            stmt.setString(6, prontuario.getReceituario());
            stmt.setString(7, prontuario.getExames());

            stmt.executeUpdate();
        }
    }

    public Prontuario buscarPorConsulta(Long idConsulta) throws SQLException {
        String sql = "SELECT * FROM prontuarios WHERE id_consulta = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idConsulta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Prontuario(
                            rs.getLong("id_consulta"),
                            rs.getString("historico"),
                            rs.getString("receituario"),
                            rs.getString("exames"));
                }
            }
        }
        return null;
    }
}
