package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.EspecialidadeDao;
import br.edu.imepac.clinica.daos.MedicoDao;
import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.entidades.Medico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class MedicoAddForm extends BaseScreen {

    private JTextField txtNome;
    private JTextField txtCrm;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JComboBox<Especialidade> cmbEspecialidade;

    public MedicoAddForm() {
        super("Cadastro de Médico");
        setSize(400, 400);
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("Nome:"));
        txtNome = new JTextField();
        add(txtNome);

        add(new JLabel("CRM:"));
        txtCrm = new JTextField();
        add(txtCrm);

        add(new JLabel("Telefone:"));
        txtTelefone = new JTextField();
        add(txtTelefone);

        add(new JLabel("Email:"));
        txtEmail = new JTextField();
        add(txtEmail);

        add(new JLabel("Especialidade:"));
        cmbEspecialidade = new JComboBox<>();
        carregarEspecialidades();
        add(cmbEspecialidade);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(this::salvarMedico);
        add(new JLabel(""));
        add(btnSalvar);

        setLocationRelativeTo(null);
    }

    private void carregarEspecialidades() {
        EspecialidadeDao dao = new EspecialidadeDao();
        List<Especialidade> lista = dao.listarTodos();
        for (Especialidade e : lista) {
            cmbEspecialidade.addItem(e);
        }
    }

    private void salvarMedico(ActionEvent e) {
        String nome = txtNome.getText();
        String crm = txtCrm.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();
        Especialidade esp = (Especialidade) cmbEspecialidade.getSelectedItem();

        if (nome.isEmpty() || crm.isEmpty() || esp == null) {
            showWarning("Preencha Nome, CRM e Especialidade!");
            return;
        }

        try {
            Medico medico = new Medico();
            medico.setNome(nome);
            medico.setCrm(crm);
            medico.setTelefone(telefone);
            medico.setEmail(email);
            medico.setEspecialidade(esp);

            MedicoDao dao = new MedicoDao();
            dao.salvar(medico);

            showSuccess("Médico cadastrado com sucesso!");
            this.dispose();

        } catch (SQLException ex) {
            showError("Erro ao salvar médico: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
