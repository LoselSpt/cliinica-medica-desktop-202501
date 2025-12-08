package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class LoginForm extends BaseScreen {
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;

    public LoginForm() {
        super("Login - Clínica Médica");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout()); // Center everything

        initComponents();

        // Ensure centering after components are added
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); // Use GridBag for internal layout too
        mainPanel.setBackground(COLOR_SURFACE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // Title
        JLabel lblTitle = new JLabel("Bem-vindo", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_TEXT);
        mainPanel.add(lblTitle, gbc);

        gbc.gridy++;
        JLabel lblSubtitle = new JLabel("Acesse sua conta", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(COLOR_TEXT_SECONDARY);
        mainPanel.add(lblSubtitle, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(30, 5, 5, 5); // Spacer

        // Login Field
        JLabel lblLogin = new JLabel("Usuário");
        mainPanel.add(lblLogin, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        txtLogin = new JTextField(20);
        txtLogin.setPreferredSize(new Dimension(250, 40));
        mainPanel.add(txtLogin, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(15, 5, 5, 5); // Spacer

        // Password Field
        JLabel lblSenha = new JLabel("Senha");
        mainPanel.add(lblSenha, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        txtSenha = new JPasswordField(20);
        txtSenha.setPreferredSize(new Dimension(250, 40));
        mainPanel.add(txtSenha, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(30, 5, 5, 5); // Spacer

        // Login Button
        btnEntrar = new JButton("ENTRAR");
        btnEntrar.setPreferredSize(new Dimension(250, 45));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.addActionListener(this::autenticar);
        mainPanel.add(btnEntrar, gbc);

        // Add main panel to frame
        add(mainPanel);
    }

    private void autenticar(ActionEvent e) {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            showWarning("Por favor, preencha todos os campos.");
            return;
        }

        try {
            UsuarioDao dao = new UsuarioDao();
            Usuario usuario = dao.autenticar(login, senha);

            if (usuario != null) {
                new MainMenu(usuario).setVisible(true);
                this.dispose();
            } else {
                showError("Login ou senha inválidos!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Erro ao conectar ao banco: " + ex.getMessage());
        }
    }
}
