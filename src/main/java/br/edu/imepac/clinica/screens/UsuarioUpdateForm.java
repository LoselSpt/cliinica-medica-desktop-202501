package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.EspecialidadeDao;
import br.edu.imepac.clinica.daos.MedicoDao;
import br.edu.imepac.clinica.daos.PessoaDao;
import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.EnumStatusUsuario;
import br.edu.imepac.clinica.entidades.Especialidade;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.entidades.Pessoa;
import br.edu.imepac.clinica.entidades.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.List;

public class UsuarioUpdateForm extends BaseScreen {

    private Usuario usuario;
    private JTextField txtNome;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JComboBox<EnumStatusUsuario> cmbStatus;
    private Runnable onSuccess;
    private JButton btnVincularMedico;

    public UsuarioUpdateForm(Usuario usuario, Runnable onSuccess) {
        super("Editar Usuário");
        this.usuario = usuario;
        this.onSuccess = onSuccess;

        setSize(400, 450); // Increased height for extra button
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("Nome Completo:"));
        txtNome = new JTextField(usuario.getFuncionario().getNome());
        add(txtNome);

        add(new JLabel("Login:"));
        txtLogin = new JTextField(usuario.getLogin());
        add(txtLogin);

        add(new JLabel("Nova Senha (deixe em branco para manter):"));
        txtSenha = new JPasswordField();
        add(txtSenha);

        add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(EnumStatusUsuario.values());
        cmbStatus.setSelectedItem(usuario.getStatus());
        add(cmbStatus);

        // Check if Link Doctor button is needed
        btnVincularMedico = new JButton("Vincular Médico");
        btnVincularMedico.setBackground(COLOR_ACCENT);
        btnVincularMedico.addActionListener(this::vincularMedico);
        btnVincularMedico.setVisible(false); // Default hidden

        checkMedicoLink();

        add(new JLabel(""));
        add(btnVincularMedico);

        JButton btnSalvar = new JButton("Salvar Alterações");
        btnSalvar.addActionListener(this::salvarUsuario);
        add(new JLabel(""));
        add(btnSalvar);

        setLocationRelativeTo(null);
    }

    private void checkMedicoLink() {
        String role = normalizeString(usuario.getPerfil().getNome());
        if ("MEDICO".equals(role)) {
            try {
                MedicoDao dao = new MedicoDao();
                Medico m = dao.buscarPorIdPessoa(usuario.getFuncionario().getId());
                if (m == null) {
                    btnVincularMedico.setVisible(true);
                    btnVincularMedico.setText("Vincular como Médico");
                } else {
                    btnVincularMedico.setVisible(true);
                    btnVincularMedico.setText("Médico Vinculado ✓");
                    btnVincularMedico.setEnabled(false);
                    btnVincularMedico.setBackground(new Color(39, 174, 96)); // Green
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void vincularMedico(ActionEvent e) {
        try {
            EspecialidadeDao espDao = new EspecialidadeDao();
            List<Especialidade> especialidades = espDao.listarTodos();

            if (especialidades.isEmpty()) {
                showWarning("Nenhuma especialidade cadastrada! Cadastre uma especialidade primeiro.");
                return;
            }

            Especialidade selectedEsp = (Especialidade) JOptionPane.showInputDialog(
                    this,
                    "Selecione a Especialidade do Médico:",
                    "Vincular Médico",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    especialidades.toArray(),
                    especialidades.get(0));

            if (selectedEsp == null) {
                return; // User cancelled
            }

            MedicoDao dao = new MedicoDao();
            Medico m = new Medico();
            m.setId(usuario.getFuncionario().getId());
            m.setCrm("Pendente"); // Placeholder
            m.setEspecialidade(selectedEsp);

            dao.salvarParaPessoaExistente(m);

            showSuccess("Usuário vinculado como Médico com sucesso!");
            checkMedicoLink(); // Update button state

        } catch (SQLException ex) {
            showError("Erro ao vincular médico: " + ex.getMessage());
        }
    }

    private void salvarUsuario(ActionEvent e) {
        String nome = txtNome.getText();
        String login = txtLogin.getText();
        String novaSenha = new String(txtSenha.getPassword());
        EnumStatusUsuario status = (EnumStatusUsuario) cmbStatus.getSelectedItem();

        if (nome.isEmpty() || login.isEmpty()) {
            showWarning("Nome e Login são obrigatórios!");
            return;
        }

        try {
            // 1. Update Pessoa (Name)
            Pessoa pessoa = usuario.getFuncionario();
            pessoa.setNome(nome);
            PessoaDao pessoaDao = new PessoaDao();
            pessoaDao.atualizar(pessoa);

            // 2. Update Usuario
            usuario.setLogin(login);
            usuario.setStatus(status);
            if (!novaSenha.isEmpty()) {
                usuario.setSenha(novaSenha);
            }

            UsuarioDao usuarioDao = new UsuarioDao();
            usuarioDao.update(usuario);

            JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
            if (onSuccess != null)
                onSuccess.run();
            this.dispose();

        } catch (SQLException ex) {
            showError("Erro ao atualizar usuário: " + ex.getMessage());
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
