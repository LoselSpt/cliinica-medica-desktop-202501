package br.edu.imepac.clinica;

import br.edu.imepac.clinica.daos.*;
import br.edu.imepac.clinica.entidades.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class MainVerification extends BaseDao {

    public static void main(String[] args) {
        new MainVerification().run();
    }

    public void run() {
        try {
            System.out.println("Iniciando verificação...");
            setupDatabase();
            insertData();

            testMedicoDao();
            testConsultaDao();
            testUsuarioDao();

            System.out.println("Verificação concluída com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDatabase() throws SQLException {
        System.out.println("Configurando banco de dados...");
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS consultas");
            stmt.executeUpdate("DROP TABLE IF EXISTS medico");
            stmt.executeUpdate("DROP TABLE IF EXISTS usuarios");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS especialidade (id BIGINT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100) NOT NULL, descricao VARCHAR(255))");
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS pessoas (id BIGINT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100) NOT NULL, telefone VARCHAR(20), email VARCHAR(100))");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS medico (id BIGINT AUTO_INCREMENT PRIMARY KEY, crm VARCHAR(20) NOT NULL, id_pessoa BIGINT NOT NULL, id_especialidade BIGINT NOT NULL, FOREIGN KEY (id_pessoa) REFERENCES pessoas(id), FOREIGN KEY (id_especialidade) REFERENCES especialidade(id))");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS usuarios (id BIGINT AUTO_INCREMENT PRIMARY KEY, login VARCHAR(50) NOT NULL UNIQUE, senha VARCHAR(255) NOT NULL, status VARCHAR(20) NOT NULL, perfil VARCHAR(50) NOT NULL)");

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS consultas (id BIGINT AUTO_INCREMENT PRIMARY KEY, id_medico BIGINT NOT NULL, id_paciente BIGINT NOT NULL, data_hora DATETIME NOT NULL, motivo VARCHAR(255), FOREIGN KEY (id_medico) REFERENCES medico(id), FOREIGN KEY (id_paciente) REFERENCES pessoas(id))");

            stmt.executeUpdate("DELETE FROM consultas");
            stmt.executeUpdate("DELETE FROM medico");
            stmt.executeUpdate("DELETE FROM usuarios");
        }
    }

    private void insertData() throws SQLException {
        System.out.println("Inserindo dados de teste...");
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM especialidade WHERE nome = 'Cardiologia'");
            rs.next();
            long count = rs.getLong(1);
            long espId = 1;
            if (count == 0) {
                stmt.executeUpdate("INSERT INTO especialidade (nome, descricao) VALUES ('Cardiologia', 'Coração')",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet gk = stmt.getGeneratedKeys();
                if (gk.next())
                    espId = gk.getLong(1);
            } else {
                rs = stmt.executeQuery("SELECT id FROM especialidade WHERE nome = 'Cardiologia'");
                if (rs.next())
                    espId = rs.getLong(1);
            }

            stmt.executeUpdate(
                    "INSERT INTO pessoas (nome, telefone, email) VALUES ('Dr. House', '123', 'house@mail.com')",
                    Statement.RETURN_GENERATED_KEYS);
            ResultSet gk = stmt.getGeneratedKeys();
            long idMedicoPessoa = 0;
            if (gk.next())
                idMedicoPessoa = gk.getLong(1);

            stmt.executeUpdate(
                    "INSERT INTO pessoas (nome, telefone, email) VALUES ('Paciente Zero', '456', 'zero@mail.com')",
                    Statement.RETURN_GENERATED_KEYS);
            gk = stmt.getGeneratedKeys();
            long idPacientePessoa = 0;
            if (gk.next())
                idPacientePessoa = gk.getLong(1);

            PreparedStatement pstmt = conn
                    .prepareStatement("INSERT INTO medico (crm, id_pessoa, id_especialidade) VALUES (?, ?, ?)");
            pstmt.setString(1, "CRM123");
            pstmt.setLong(2, idMedicoPessoa);
            pstmt.setLong(3, espId);
            pstmt.executeUpdate();

            stmt.executeUpdate(
                    "INSERT INTO usuarios (login, senha, status, perfil) VALUES ('admin', 'admin', 'ATIVO', 'ADMIN')");
        }
    }

    private void testMedicoDao() throws SQLException {
        System.out.println("Testando MedicoDao...");
        MedicoDao dao = new MedicoDao();
        List<Medico> medicos = dao.buscarTodos();
        if (medicos.isEmpty())
            throw new RuntimeException("Nenhum médico encontrado!");
        Medico m = medicos.get(0);
        System.out.println("Médico encontrado: " + m.getNome() + ", CRM: " + m.getCrm() + ", Esp: "
                + m.getEspecialidade().getNome());
        if (!"Dr. House".equals(m.getNome()))
            throw new RuntimeException("Nome incorreto! Esperado: Dr. House, Atual: " + m.getNome());
    }

    private void testConsultaDao() throws SQLException {
        System.out.println("Testando ConsultaDao...");
        ConsultaDao dao = new ConsultaDao();

        MedicoDao mDao = new MedicoDao();
        Medico medico = mDao.buscarTodos().get(0);

        long idPaciente = 0;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT id FROM pessoas WHERE nome = 'Paciente Zero'");
            if (rs.next())
                idPaciente = rs.getLong(1);
        }

        Pessoa paciente = new Pessoa();
        paciente.setId(idPaciente);

        LocalDateTime data = LocalDateTime.now().plusDays(1).withNano(0);

        Consulta c1 = new Consulta(null, medico, paciente, null, null, data, false, "AGENDADA", "Checkup");

        boolean agendou = dao.agendar(c1);
        System.out.println("Agendamento 1: " + agendou);
        if (!agendou)
            throw new RuntimeException("Falha ao agendar consulta válida!");

        boolean conflito = dao.agendar(c1);
        System.out.println("Agendamento 2 (Conflito): " + conflito);
        if (conflito)
            throw new RuntimeException("Deveria ter detectado conflito!");
    }

    private void testUsuarioDao() throws SQLException {
        System.out.println("Testando UsuarioDao...");
        UsuarioDao dao = new UsuarioDao();
        Usuario u = dao.autenticar("admin", "admin");
        if (u == null)
            throw new RuntimeException("Falha na autenticação!");
        System.out.println("Usuário autenticado: " + u.getLogin() + ", Perfil: " + u.getPerfil().getNome());
        if (u.getPerfil().getFuncionalidades().isEmpty())
            throw new RuntimeException("Funcionalidades vazias!");
    }
}
