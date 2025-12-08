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

public class AgendamentoForm extends BaseScreen {
    private JComboBox<Medico> comboMedico;
    private JComboBox<Pessoa> comboPaciente;
    private JComboBox<Convenio> comboConvenio;
    private JTextField txtDataHora;
    private JCheckBox chkRetorno;
    private JTextField txtMotivo;

    public AgendamentoForm(Usuario usuario) {
        super("Agendamento de Consulta");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        carregarCombos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        header.setBackground(COLOR_PRIMARY);
        JLabel title = new JLabel("Nova Consulta");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_SURFACE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Medico
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Médico:"), gbc);

        gbc.gridy = 1;
        comboMedico = new JComboBox<>();
        formPanel.add(comboMedico, gbc);

        // Paciente
        gbc.gridy = 2;
        formPanel.add(createLabel("Paciente:"), gbc);

        gbc.gridy = 3;
        comboPaciente = new JComboBox<>();
        formPanel.add(comboPaciente, gbc);

        // Convenio
        gbc.gridy = 4;
        formPanel.add(createLabel("Convênio:"), gbc);

        gbc.gridy = 5;
        comboConvenio = new JComboBox<>();
        formPanel.add(comboConvenio, gbc);

        // Data
        gbc.gridy = 6;
        formPanel.add(createLabel("Data/Hora (dd/MM/yyyy HH:mm):"), gbc);

        gbc.gridy = 7;
        txtDataHora = new JTextField();
        formPanel.add(txtDataHora, gbc);

        // Motivo
        gbc.gridy = 8;
        formPanel.add(createLabel("Motivo:"), gbc);

        gbc.gridy = 9;
        txtMotivo = new JTextField();
        formPanel.add(txtMotivo, gbc);

        // Retorno
        gbc.gridy = 10;
        chkRetorno = new JCheckBox("É Retorno?");
        chkRetorno.setBackground(COLOR_SURFACE);
        chkRetorno.setForeground(COLOR_TEXT);
        formPanel.add(chkRetorno, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footer.setBackground(COLOR_BACKGROUND);

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(COLOR_SURFACE);
        btnCancel.setForeground(COLOR_TEXT);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("Agendar");
        btnSave.addActionListener(e -> agendar());

        footer.add(btnCancel);
        footer.add(btnSave);
        add(footer, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(COLOR_TEXT);
        return lbl;
    }

    private void carregarCombos() {
        try {
            MedicoDao medicoDao = new MedicoDao();
            List<Medico> medicos = medicoDao.buscarTodos();
            for (Medico m : medicos)
                comboMedico.addItem(m);

            PessoaDao pessoaDao = new PessoaDao();
            List<Pessoa> pacientes = pessoaDao.buscarTodos();
            for (Pessoa p : pacientes)
                comboPaciente.addItem(p);

            ConvenioDao convenioDao = new ConvenioDao();
            List<Convenio> convenios = convenioDao.buscarTodos();
            for (Convenio c : convenios)
                comboConvenio.addItem(c);

        } catch (SQLException e) {
            showError("Erro ao carregar dados: " + e.getMessage());
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

            ConsultaDao dao = new ConsultaDao();
            if (dao.agendar(consulta)) {
                showSuccess("Agendamento realizado com sucesso!");
                this.dispose();
            } else {
                showWarning("Conflito de horário! Escolha outro horário.");
            }
        } catch (Exception e) {
            showError("Erro ao agendar: " + e.getMessage());
        }
    }
}
