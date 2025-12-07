package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class LoginForm extends JFrame {
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;

    public LoginForm() {
        setTitle("Login - Clínica Médica");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Login:"));
        txtLogin = new JTextField();
        add(txtLogin);

        add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        add(txtSenha);

        btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(this::autenticar);
        add(new JLabel("")); // Placeholder
        add(btnEntrar);
    }

    private void autenticar(ActionEvent e) {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());

        try {
            UsuarioDao dao = new UsuarioDao();
            Usuario usuario = dao.autenticar(login, senha);

            if (usuario != null) {
                JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuario.getFuncionario().getNome());
                new MainMenu(usuario).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Login ou senha inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco: " + ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
