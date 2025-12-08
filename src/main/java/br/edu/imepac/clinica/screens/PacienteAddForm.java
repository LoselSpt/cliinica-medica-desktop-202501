package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.PessoaDao;
import br.edu.imepac.clinica.entidades.Pessoa;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class PacienteAddForm extends BaseScreen {
    private JTextField txtNome;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private PessoaDao dao;
    private Runnable onSuccessCallback;

    public PacienteAddForm(Runnable onSuccessCallback) {
        super("Cadastro de Paciente");
        this.onSuccessCallback = onSuccessCallback;
        this.dao = new PessoaDao();

        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        header.setBackground(COLOR_PRIMARY);
        JLabel title = new JLabel("Novo Paciente");
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

        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Nome Completo:"), gbc);

        gbc.gridy = 1;
        txtNome = new JTextField();
        formPanel.add(txtNome, gbc);

        // Telefone
        gbc.gridy = 2;
        formPanel.add(createLabel("Telefone:"), gbc);

        gbc.gridy = 3;
        txtTelefone = new JTextField();
        formPanel.add(txtTelefone, gbc);

        // Email
        gbc.gridy = 4;
        formPanel.add(createLabel("Email:"), gbc);

        gbc.gridy = 5;
        txtEmail = new JTextField();
        formPanel.add(txtEmail, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footer.setBackground(COLOR_BACKGROUND);

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(COLOR_SURFACE);
        btnCancel.setForeground(COLOR_TEXT);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("Salvar");
        btnSave.addActionListener(e -> salvarPaciente());

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

    private void salvarPaciente() {
        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String email = txtEmail.getText().trim();

        if (nome.isEmpty() || telefone.isEmpty()) {
            showWarning("Nome e Telefone são obrigatórios!");
            return;
        }

        Pessoa p = new Pessoa(null, nome, telefone, email);
        try {
            dao.salvar(p);
            showSuccess("Paciente cadastrado com sucesso!");
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Erro ao salvar paciente: " + ex.getMessage());
        }
    }
}
