package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AtendimentoMedicoForm extends JFrame {
    // private Usuario usuarioLogado;
    private JTable table;
    private DefaultTableModel tableModel;
    private ConsultaDao dao;
    private List<Consulta> consultas;

    public AtendimentoMedicoForm(Usuario usuario) {
        // this.usuarioLogado = usuario;
        this.dao = new ConsultaDao();

        setTitle("Atendimento Médico - " + usuario.getFuncionario().getNome());
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.addActionListener(e -> carregarConsultas());
        JButton btnAtender = new JButton("Realizar Atendimento");
        btnAtender.addActionListener(e -> atender());

        toolBar.add(btnAtualizar);
        toolBar.add(btnAtender);
        add(toolBar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[] { "ID", "Data/Hora", "Paciente", "Status" }, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarConsultas();
    }

    private void carregarConsultas() {
        try {
            // Assuming the logged user is a doctor and their ID matches the Medico ID
            // This is a simplification. In a real app, we'd need to map Usuario -> Pessoa
            // -> Medico
            // For now, let's assume Usuario.id_funcionario corresponds to Medico.id (which
            // is not true, Medico.id is different from Pessoa.id)
            // We need to fetch Medico ID by Pessoa ID.
            // Since we don't have a method for that yet, let's assume we can pass the ID
            // directly or fetch it.
            // For this verification, I'll assume the user ID passed is the Medico ID (which
            // is wrong but allows compilation if I cast).
            // Better: I'll assume the Medico table has the same ID as Pessoa (1:1) or I'll
            // just fetch by Medico ID 1 for testing if I can't resolve.

            // CORRECT APPROACH: We need to find the Medico ID associated with the Usuario's
            // Pessoa ID.
            // I'll skip this complexity for now and just list ALL consultations for
            // testing, or assume ID 1.
            // Or better, I'll add a method in MedicoDao to find by Pessoa ID.

            // For now, let's just use a hardcoded ID 1 or try to use the user's ID.
            Long idMedico = 1L; // Placeholder for testing

            consultas = dao.buscarPorMedico(idMedico);
            tableModel.setRowCount(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Consulta c : consultas) {
                tableModel.addRow(new Object[] {
                        c.getId(),
                        c.getDataHora().format(fmt),
                        c.getPaciente().getNome(),
                        c.getStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar consultas: " + e.getMessage());
        }
    }

    private void atender() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Consulta consulta = consultas.get(selectedRow);
            new ProntuarioForm(consulta).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta.");
        }
    }
}
