package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.PessoaDao;
import br.edu.imepac.clinica.entidades.Pessoa;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PacienteListaForm extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private PessoaDao dao;

    public PacienteListaForm() {
        dao = new PessoaDao();
        setTitle("Gestão de Pacientes");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnNovo = new JButton("Novo");
        btnNovo.addActionListener(e -> abrirCadastro(null));
        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.addActionListener(e -> carregarDados());

        toolBar.add(btnNovo);
        toolBar.add(btnAtualizar);
        add(toolBar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[] { "ID", "Nome", "Telefone", "Email" }, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        try {
            tableModel.setRowCount(0);
            List<Pessoa> pessoas = dao.buscarTodos();
            for (Pessoa p : pessoas) {
                tableModel.addRow(new Object[] { p.getId(), p.getNome(), p.getTelefone(), p.getEmail() });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void abrirCadastro(Pessoa pessoa) {
        // Simple dialog for adding
        JDialog dialog = new JDialog(this, "Novo Paciente", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(4, 2));
        dialog.setLocationRelativeTo(this);

        JTextField txtNome = new JTextField();
        JTextField txtTelefone = new JTextField();
        JTextField txtEmail = new JTextField();

        dialog.add(new JLabel("Nome:"));
        dialog.add(txtNome);
        dialog.add(new JLabel("Telefone:"));
        dialog.add(txtTelefone);
        dialog.add(new JLabel("Email:"));
        dialog.add(txtEmail);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            Pessoa p = new Pessoa(null, txtNome.getText(), txtTelefone.getText(), txtEmail.getText());
            try {
                dao.salvar(p);
                JOptionPane.showMessageDialog(dialog, "Salvo com sucesso!");
                dialog.dispose();
                carregarDados();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar: " + ex.getMessage());
            }
        });
        dialog.add(new JLabel(""));
        dialog.add(btnSalvar);

        dialog.setVisible(true);
    }
}
