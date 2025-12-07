package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Convenio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConvenioDao extends BaseDao {

    public List<Convenio> buscarTodos() throws SQLException {
        List<Convenio> convenios = new ArrayList<>();
        String sql = "SELECT * FROM convenios";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Convenio c = new Convenio();
                c.setId(rs.getLong("id"));
                c.setNome(rs.getString("nome"));
                c.setCnpj(rs.getString("cnpj"));
                convenios.add(c);
            }
        }
        return convenios;
    }

    public void salvar(Convenio convenio) throws SQLException {
        String sql = "INSERT INTO convenios (nome, cnpj) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, convenio.getNome());
            stmt.setString(2, convenio.getCnpj());
            stmt.executeUpdate();
        }
    }
}
