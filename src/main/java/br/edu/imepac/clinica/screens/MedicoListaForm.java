package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.MedicoDao;
import br.edu.imepac.clinica.entidades.Medico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MedicoListaForm extends BaseScreen {

    private JTable table;
    private DefaultTableModel tableModel;

    public MedicoListaForm() {
        super("Lista de Médicos");
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnNovo = new JButton("Novo Médico");
        btnNovo.addActionListener(e -> {
            new MedicoAddForm().setVisible(true);
            // Reload after close? For simplicity, manual refresh or listener.
            // Let's just add a refresh button.
        });
        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.addActionListener(e -> carregarMedicos());

        toolBar.add(btnNovo);
        toolBar.add(btnAtualizar);
        add(toolBar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[] { "ID", "Nome", "CRM", "Especialidade" }, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarMedicos();
    }

    private void carregarMedicos() {
        try {
            MedicoDao dao = new MedicoDao();
            List<Medico> medicos = dao.buscarTodos();
            tableModel.setRowCount(0);

            for (Medico m : medicos) {
                tableModel.addRow(new Object[] {
                        m.getId(),
                        m.getNome(),
                        m.getCrm(),
                        m.getEspecialidade().getNome()
                });
            }
        } catch (SQLException e) {
            showError("Erro ao carregar médicos: " + e.getMessage());
        }
    }
}
