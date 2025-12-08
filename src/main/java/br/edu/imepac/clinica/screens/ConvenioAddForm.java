package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.ConvenioDao;
import br.edu.imepac.clinica.entidades.Convenio;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ConvenioAddForm extends BaseScreen {
    private JTextField txtEmpresa;
    private JTextField txtCnpj;
    private ConvenioDao dao;

    public ConvenioAddForm() {
        super("Cadastro de Convênio");
        dao = new ConvenioDao();

        setSize(500, 300); // Reduced height since we removed a field
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        header.setBackground(COLOR_PRIMARY);
        JLabel title = new JLabel("Novo Convênio");
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

        // Empresa (Mapped to Nome)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("Nome da Empresa:"), gbc);

        gbc.gridy = 1;
        txtEmpresa = new JTextField();
        formPanel.add(txtEmpresa, gbc);

        // CNPJ
        gbc.gridy = 2;
        formPanel.add(createLabel("CNPJ:"), gbc);

        gbc.gridy = 3;
        txtCnpj = new JTextField();
        formPanel.add(txtCnpj, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footer.setBackground(COLOR_BACKGROUND);

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(COLOR_SURFACE);
        btnCancel.setForeground(COLOR_TEXT);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("Salvar");
        btnSave.setBackground(COLOR_PRIMARY); // Dark Blue
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> salvarConvenio());

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

    private void salvarConvenio() {
        String empresa = txtEmpresa.getText().trim();
        String cnpj = txtCnpj.getText().trim();

        if (empresa.isEmpty() || cnpj.isEmpty()) {
            showWarning("Empresa e CNPJ são obrigatórios!");
            return;
        }

        Convenio c = new Convenio();
        c.setNome(empresa); // Fixed: setEmpresa -> setNome
        c.setCnpj(cnpj);
        // Removed setTelefone as it is not in the entity

        try {
            dao.salvar(c);
            showSuccess("Convênio cadastrado com sucesso!");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Erro ao salvar convênio: " + ex.getMessage());
        }
    }
}
