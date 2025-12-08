package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.PessoaDao;
import br.edu.imepac.clinica.entidades.Pessoa;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PacienteListaForm extends BaseScreen {
    private JTable table;
    private DefaultTableModel tableModel;
    private PessoaDao dao;

    public PacienteListaForm() {
        super("Gestão de Pacientes");
        dao = new PessoaDao();
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
        carregarDados();
    }

    private void initComponents() {
        // Header / Toolbar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_PRIMARY);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Pacientes");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        topPanel.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        JButton btnNovo = new JButton("Novo Paciente");
        btnNovo.addActionListener(e -> new PacienteAddForm(this::carregarDados).setVisible(true));

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setBackground(COLOR_SURFACE);
        btnAtualizar.setForeground(COLOR_TEXT);
        btnAtualizar.addActionListener(e -> carregarDados());

        actions.add(btnAtualizar);
        actions.add(btnNovo);
        topPanel.add(actions, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[] { "ID", "Nome", "Telefone", "Email" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_BACKGROUND);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarDados() {
        try {
            tableModel.setRowCount(0);
            List<Pessoa> pessoas = dao.buscarTodos();
            for (Pessoa p : pessoas) {
                tableModel.addRow(new Object[] { p.getId(), p.getNome(), p.getTelefone(), p.getEmail() });
            }
        } catch (SQLException e) {
            showError("Erro ao carregar dados: " + e.getMessage());
        }
    }
}
