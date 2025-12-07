package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.daos.ConvenioDao;
import br.edu.imepac.clinica.daos.MedicoDao;
import br.edu.imepac.clinica.daos.PessoaDao;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Convenio;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.entidades.Pessoa;
import br.edu.imepac.clinica.entidades.Usuario;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AgendamentoForm extends JFrame {
    private JComboBox<Medico> comboMedico;
    private JComboBox<Pessoa> comboPaciente;
    private JComboBox<Convenio> comboConvenio;
    private JTextField txtDataHora;
    private JCheckBox chkRetorno;
    private JTextField txtMotivo;
    private Usuario usuarioLogado;

    public AgendamentoForm(Usuario usuario) {
        this.usuarioLogado = usuario;
        setTitle("Agendamento de Consulta");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 10, 10));

        add(new JLabel("Médico:"));
        comboMedico = new JComboBox<>();
        add(comboMedico);

        add(new JLabel("Paciente:"));
        comboPaciente = new JComboBox<>();
        add(comboPaciente);

        add(new JLabel("Convênio:"));
        comboConvenio = new JComboBox<>();
        add(comboConvenio);

        add(new JLabel("Data/Hora (dd/MM/yyyy HH:mm):"));
        txtDataHora = new JTextField();
        add(txtDataHora);

        add(new JLabel("É Retorno?"));
        chkRetorno = new JCheckBox();
        add(chkRetorno);

        add(new JLabel("Motivo:"));
        txtMotivo = new JTextField();
        add(txtMotivo);

        JButton btnSalvar = new JButton("Agendar");
        btnSalvar.addActionListener(e -> agendar());
        add(new JLabel(""));
        add(btnSalvar);

        carregarCombos();
    }

    private void carregarCombos() {
        try {
            MedicoDao medicoDao = new MedicoDao();
            List<Medico> medicos = medicoDao.buscarTodos();
            for (Medico m : medicos)
                comboMedico.addItem(m);

            PessoaDao pessoaDao = new PessoaDao();
            List<Pessoa> pacientes = pessoaDao.buscarTodos(); // Should filter by patient ideally
            for (Pessoa p : pacientes)
                comboPaciente.addItem(p);

            ConvenioDao convenioDao = new ConvenioDao();
            List<Convenio> convenios = convenioDao.buscarTodos();
            for (Convenio c : convenios)
                comboConvenio.addItem(c);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void agendar() {
        try {
            Consulta consulta = new Consulta();
            consulta.setMedico((Medico) comboMedico.getSelectedItem());
            consulta.setPaciente((Pessoa) comboPaciente.getSelectedItem());
            consulta.setConvenio((Convenio) comboConvenio.getSelectedItem());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            consulta.setDataHora(LocalDateTime.parse(txtDataHora.getText(), formatter));

            consulta.setRetorno(chkRetorno.isSelected());
            consulta.setMotivo(txtMotivo.getText());
            consulta.setStatus("AGENDADA");

            // Set Secretaria (who scheduled) if the user is a secretary
            // For now, we can cast user.getFuncionario() to Secretaria if needed, or just
            // pass the ID
            // But Consulta entity expects Secretaria object.
            // Let's assume for now we don't set it strictly or we create a dummy one with
            // the ID.
            // consulta.setSecretaria(...);

            ConsultaDao dao = new ConsultaDao();
            if (dao.agendar(consulta)) {
                JOptionPane.showMessageDialog(this, "Agendamento realizado com sucesso!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Conflito de horário! Escolha outro horário.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao agendar: " + e.getMessage());
        }
    }
}
