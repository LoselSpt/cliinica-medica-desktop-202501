package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.MedicoDao;
import br.edu.imepac.clinica.daos.PerfilDao;
import br.edu.imepac.clinica.daos.PessoaDao;
import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.EnumStatusUsuario;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.entidades.Perfil;
import br.edu.imepac.clinica.entidades.Pessoa;
import br.edu.imepac.clinica.entidades.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.List;

public class UsuarioAddForm extends BaseScreen {

    private JTextField txtNome;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JComboBox<Perfil> cmbPerfil;

    public UsuarioAddForm() {
        super("Cadastro de Usuário");
        setSize(400, 300);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Nome Completo:"));
        txtNome = new JTextField();
        add(txtNome);

        add(new JLabel("Login:"));
        txtLogin = new JTextField();
        add(txtLogin);

        add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        add(txtSenha);

        add(new JLabel("Perfil:"));
        cmbPerfil = new JComboBox<>();
        carregarPerfis();
        add(cmbPerfil);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(this::salvarUsuario);
        add(new JLabel(""));
        add(btnSalvar);

        setLocationRelativeTo(null);
    }

    private void carregarPerfis() {
        try {
            PerfilDao dao = new PerfilDao();
            List<Perfil> perfis = dao.listAll();
            for (Perfil p : perfis) {
                cmbPerfil.addItem(p);
            }
        } catch (SQLException e) {
            showError("Erro ao carregar perfis: " + e.getMessage());
        }
    }

    private void salvarUsuario(ActionEvent e) {
        String nome = txtNome.getText();
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());
        Perfil perfil = (Perfil) cmbPerfil.getSelectedItem();

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty() || perfil == null) {
            showWarning("Preencha todos os campos!");
            return;
        }

        try {
            // 1. Create Pessoa
            Pessoa pessoa = new Pessoa();
            pessoa.setNome(nome);
            // Optional: Add email/phone fields if needed, for now just name

            PessoaDao pessoaDao = new PessoaDao();
            pessoaDao.salvar(pessoa);

            // 2. Create Usuario
            Usuario usuario = new Usuario();
            usuario.setLogin(login);
            usuario.setSenha(senha);
            usuario.setStatus(EnumStatusUsuario.ATIVO);
            usuario.setPerfil(perfil);
            usuario.setFuncionario(pessoa);

            UsuarioDao usuarioDao = new UsuarioDao();
            usuarioDao.save(usuario);

            // 3. If Profile is MEDICO, create Medico record
            if (normalizeString(perfil.getNome()).equals("MEDICO")) {
                MedicoDao medicoDao = new MedicoDao();
                // Check if already exists (unlikely since we just created Person)
                if (medicoDao.buscarPorIdPessoa(pessoa.getId()) == null) {
                    Medico medico = new Medico();
                    medico.setId(pessoa.getId());
                    medico.setCrm("Pendente"); // Placeholder
                    medico.setEspecialidade(null);
                    medicoDao.salvarParaPessoaExistente(medico);
                }
            }

            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            this.dispose();

        } catch (SQLException ex) {
            showError("Erro ao salvar usuário: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String normalizeString(String str) {
        if (str == null)
            return "";
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").toUpperCase();
    }
}
