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
        btnNovo.setBackground(COLOR_ACCENT);
        btnNovo.setForeground(Color.WHITE);
        btnNovo.addActionListener(e -> new PacienteAddForm(this::carregarDados).setVisible(true));

        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.setBackground(COLOR_SURFACE);
        btnAtualizar.setForeground(COLOR_TEXT);
        btnAtualizar.addActionListener(e -> carregarDados());

        JButton btnExcluir = new JButton("Excluir Selecionado");
        btnExcluir.setBackground(new Color(192, 57, 43)); // Red
        btnExcluir.setForeground(Color.WHITE);
        btnExcluir.addActionListener(e -> excluirSelecionado());

        actions.add(btnAtualizar);
        actions.add(btnExcluir);
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
        table.getTableHeader().setBackground(COLOR_PRIMARY_DARK);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_BACKGROUND);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarDados() {
        try {
            tableModel.setRowCount(0);
            // Use buscarPacientes to filter out doctors and users
            List<Pessoa> pessoas = dao.buscarPacientes();
            for (Pessoa p : pessoas) {
                tableModel.addRow(new Object[] { p.getId(), p.getNome(), p.getTelefone(), p.getEmail() });
            }
        } catch (SQLException e) {
            showError("Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void excluirSelecionado() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showWarning("Selecione um paciente para excluir.");
            return;
        }

        Long id = (Long) table.getValueAt(row, 0);
        String nome = (String) table.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o paciente '" + nome + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.excluir(id);
                showSuccess("Paciente excluído com sucesso!");
                carregarDados();
            } catch (SQLException e) {
                showError("Erro ao excluir: " + e.getMessage()
                        + "\nVerifique se o paciente possui consultas ou registros vinculados.");
            }
        }
    }
}
