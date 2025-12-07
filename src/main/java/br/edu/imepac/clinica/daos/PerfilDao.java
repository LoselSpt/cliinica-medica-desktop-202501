package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Perfil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PerfilDao extends BaseDao {

    public List<Perfil> listAll() throws SQLException {
        List<Perfil> perfis = new ArrayList<>();
        String sql = "SELECT * FROM perfis";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Perfil perfil = new Perfil();
                perfil.setId(rs.getLong("id"));
                perfil.setNome(rs.getString("nome"));
                perfis.add(perfil);
            }
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
        return perfis;
    }

    public Perfil findByName(String nome) throws SQLException {
        String sql = "SELECT * FROM perfis WHERE nome = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nome);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Perfil perfil = new Perfil();
                perfil.setId(rs.getLong("id"));
                perfil.setNome(rs.getString("nome"));
                return perfil;
            }
        } finally {
            fecharRecursos(conn, stmt, rs);
        }
        return null;
    }
}
