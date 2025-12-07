package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.ProntuarioDao;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Prontuario;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ProntuarioForm extends JFrame {
    private Consulta consulta;
    private JTextArea txtHistorico;
    private JTextArea txtReceituario;
    private JTextArea txtExames;
    private ProntuarioDao dao;

    public ProntuarioForm(Consulta consulta) {
        this.consulta = consulta;
        this.dao = new ProntuarioDao();

        setTitle("Prontuário - Paciente: " + consulta.getPaciente().getNome());
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel panelHeader = new JPanel(new GridLayout(2, 1));
        panelHeader.add(new JLabel("Paciente: " + consulta.getPaciente().getNome()));
        panelHeader.add(new JLabel("Médico: " + consulta.getMedico().getCrm())); // Assuming Medico has name in Pessoa
                                                                                 // part, but here accessing CRM
        add(panelHeader, BorderLayout.NORTH);

        // Body
        JTabbedPane tabs = new JTabbedPane();

        txtHistorico = new JTextArea();
        tabs.addTab("Histórico", new JScrollPane(txtHistorico));

        txtReceituario = new JTextArea();
        tabs.addTab("Receituário", new JScrollPane(txtReceituario));

        txtExames = new JTextArea();
        tabs.addTab("Exames", new JScrollPane(txtExames));

        add(tabs, BorderLayout.CENTER);

        // Footer
        JButton btnSalvar = new JButton("Salvar Prontuário");
        btnSalvar.addActionListener(e -> salvar());
        add(btnSalvar, BorderLayout.SOUTH);

        carregarDados();
    }

    private void carregarDados() {
        try {
            Prontuario p = dao.buscarPorConsulta(consulta.getId());
            if (p != null) {
                txtHistorico.setText(p.getHistorico());
                txtReceituario.setText(p.getReceituario());
                txtExames.setText(p.getExames());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar prontuário: " + e.getMessage());
        }
    }

    private void salvar() {
        try {
            Prontuario p = new Prontuario(
                    consulta.getId(),
                    txtHistorico.getText(),
                    txtReceituario.getText(),
                    txtExames.getText());
            dao.salvar(p);
            JOptionPane.showMessageDialog(this, "Prontuário salvo com sucesso!");
            this.dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }
}
