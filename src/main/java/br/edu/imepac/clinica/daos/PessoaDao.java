package br.edu.imepac.clinica.daos;

import br.edu.imepac.clinica.entidades.Pessoa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PessoaDao extends BaseDao {

    public List<Pessoa> buscarTodos() throws SQLException {
        List<Pessoa> pessoas = new ArrayList<>();
        String sql = "SELECT * FROM pessoas";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Pessoa p = new Pessoa();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setTelefone(rs.getString("telefone"));
                p.setEmail(rs.getString("email"));
                pessoas.add(p);
            }
        }
        return pessoas;
    }

    public List<Pessoa> buscarPacientes() throws SQLException {
        List<Pessoa> pessoas = new ArrayList<>();
        // Filter out Medicos and Users (Funcionarios)
        String sql = "SELECT * FROM pessoas p " +
                "WHERE p.id NOT IN (SELECT id_pessoa FROM medico) " +
                "AND p.id NOT IN (SELECT id_funcionario FROM usuarios)";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Pessoa p = new Pessoa();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setTelefone(rs.getString("telefone"));
                p.setEmail(rs.getString("email"));
                pessoas.add(p);
            }
        }
        return pessoas;
    }

    public void salvar(Pessoa pessoa) throws SQLException {
        String sql = "INSERT INTO pessoas (nome, telefone, email) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, pessoa.getNome());
            stmt.setString(2, pessoa.getTelefone());
            stmt.setString(3, pessoa.getEmail());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    pessoa.setId(rs.getLong(1));
                }
            }
        }
    }

    public void atualizar(Pessoa pessoa) throws SQLException {
        String sql = "UPDATE pessoas SET nome = ?, telefone = ?, email = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pessoa.getNome());
            stmt.setString(2, pessoa.getTelefone());
            stmt.setString(3, pessoa.getEmail());
            stmt.setLong(4, pessoa.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM pessoas WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public int contarTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pessoas";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int contarPacientes() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pessoas p " +
                "WHERE p.id NOT IN (SELECT id_pessoa FROM medico) " +
                "AND p.id NOT IN (SELECT id_funcionario FROM usuarios)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Helper for fixing login
    public Pessoa buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM pessoas WHERE nome = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pessoa p = new Pessoa();
                    p.setId(rs.getLong("id"));
                    p.setNome(rs.getString("nome"));
                    p.setTelefone(rs.getString("telefone"));
                    p.setEmail(rs.getString("email"));
                    return p;
                }
            }
        }
        return null;
    }
}
