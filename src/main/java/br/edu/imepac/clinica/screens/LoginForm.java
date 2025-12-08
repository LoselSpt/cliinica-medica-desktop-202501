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
        setSize(400, 500); // Slightly larger for better spacing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); // Center everything

        initComponents();

        // Center on screen AFTER setting size and components
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_SURFACE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));

        // Title
        JLabel lblTitle = new JLabel("Bem-vindo");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitle);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblSubtitle = new JLabel("Acesse sua conta");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(COLOR_TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblSubtitle);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Login Field
        JLabel lblLogin = new JLabel("Usuário");
        lblLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblLogin);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        txtLogin = new JTextField();
        txtLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(txtLogin);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password Field
        JLabel lblSenha = new JLabel("Senha");
        lblSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblSenha);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        txtSenha = new JPasswordField();
        txtSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtSenha.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(txtSenha);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Login Button
        btnEntrar = new JButton("ENTRAR");
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.addActionListener(this::autenticar);
        mainPanel.add(btnEntrar);

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
