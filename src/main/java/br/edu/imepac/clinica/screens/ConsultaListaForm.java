package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.EnumFuncionalidades;
import br.edu.imepac.clinica.entidades.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsultaListaForm extends BaseScreen {

    // private Usuario usuarioLogado;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Consulta> consultas;

    public ConsultaListaForm(Usuario usuario) {
        super("Lista de Consultas");
        // this.usuarioLogado = usuario;

        setSize(800, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.addActionListener(e -> carregarConsultas());
        toolBar.add(btnAtualizar);

        if (usuario.getPerfil().getFuncionalidades().contains(EnumFuncionalidades.DELETAR_CONSULTA)) {
            JButton btnCancelar = new JButton("Cancelar Consulta");
            btnCancelar.addActionListener(e -> cancelarConsulta());
            toolBar.add(btnCancelar);
        }

        add(toolBar, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new Object[] { "ID", "Data/Hora", "Médico", "Paciente", "Status" }, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        carregarConsultas();
    }

    private void carregarConsultas() {
        try {
            ConsultaDao dao = new ConsultaDao();
            consultas = dao.buscarTodas();

            // consultas = java.util.Collections.emptyList();

            tableModel.setRowCount(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Consulta c : consultas) {
                tableModel.addRow(new Object[] {
                        c.getId(),
                        c.getDataHora().format(fmt),
                        c.getMedico().getNome(),
                        c.getPaciente().getNome(),
                        c.getStatus()
                });
            }
        } catch (Exception e) {
            showError("Erro ao carregar consultas: " + e.getMessage());
        }
    }

    private void cancelarConsulta() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Consulta consulta = consultas.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja cancelar a consulta?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ConsultaDao dao = new ConsultaDao();
                    dao.cancelar(consulta.getId());
                    showSuccess("Consulta cancelada com sucesso!");
                    carregarConsultas();
                } catch (SQLException e) {
                    showError("Erro ao cancelar consulta: " + e.getMessage());
                }
            }
        } else {
            showWarning("Selecione uma consulta.");
        }
    }
}
