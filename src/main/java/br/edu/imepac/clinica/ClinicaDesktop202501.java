package br.edu.imepac.clinica;

import br.edu.imepac.clinica.screens.MainMenu;
import br.edu.imepac.clinica.screens.LoginForm;
import javax.swing.SwingUtilities;

public class ClinicaDesktop202501 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
