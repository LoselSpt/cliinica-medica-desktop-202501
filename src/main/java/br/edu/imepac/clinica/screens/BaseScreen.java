/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.imepac.clinica.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author evertonhf
 */
public class BaseScreen extends JFrame {

    // Theme Constants - Professional Dark Palette
    public static final Color COLOR_BACKGROUND = Color.decode("#121212"); // Almost Black
    public static final Color COLOR_SURFACE = Color.decode("#1E1E1E"); // Dark Gray
    public static final Color COLOR_PRIMARY = Color.decode("#001f3f"); // Navy Blue
    public static final Color COLOR_PRIMARY_DARK = Color.decode("#001226"); // Darker Navy
    public static final Color COLOR_ACCENT = Color.decode("#3A6EA5"); // Lighter Blue for accents
    public static final Color COLOR_TEXT = Color.decode("#E0E0E0"); // Light Gray text
    public static final Color COLOR_TEXT_SECONDARY = Color.decode("#A0A0A0"); // Dimmed text
    public static final Color COLOR_BORDER = Color.decode("#333333");

    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);

    static {
        setupGlobalUI();
    }

    public BaseScreen() {
        super();
        initBase();
    }

    public BaseScreen(String title) {
        super(title);
        initBase();
    }

    private void initBase() {
        getContentPane().setBackground(COLOR_BACKGROUND);
    }

    private static void setupGlobalUI() {
        try {
            UIManager.put("Panel.background", new ColorUIResource(COLOR_BACKGROUND));
            UIManager.put("Frame.background", new ColorUIResource(COLOR_BACKGROUND));

            UIManager.put("Label.foreground", new ColorUIResource(COLOR_TEXT));
            UIManager.put("Label.font", new FontUIResource(FONT_REGULAR));

            UIManager.put("Button.background", new ColorUIResource(COLOR_PRIMARY));
            UIManager.put("Button.foreground", new ColorUIResource(Color.WHITE));
            UIManager.put("Button.font", new FontUIResource(FONT_BOLD));
            UIManager.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
            UIManager.put("Button.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_PRIMARY_DARK, 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)));

            UIManager.put("TextField.background", new ColorUIResource(COLOR_SURFACE));
            UIManager.put("TextField.foreground", new ColorUIResource(COLOR_TEXT));
            UIManager.put("TextField.caretForeground", new ColorUIResource(COLOR_TEXT));
            UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDER),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));

            UIManager.put("PasswordField.background", new ColorUIResource(COLOR_SURFACE));
            UIManager.put("PasswordField.foreground", new ColorUIResource(COLOR_TEXT));
            UIManager.put("PasswordField.caretForeground", new ColorUIResource(COLOR_TEXT));
            UIManager.put("PasswordField.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_BORDER),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));

            UIManager.put("Table.background", new ColorUIResource(COLOR_SURFACE));
            UIManager.put("Table.foreground", new ColorUIResource(COLOR_TEXT));
            UIManager.put("Table.gridColor", new ColorUIResource(COLOR_BORDER));
            UIManager.put("Table.selectionBackground", new ColorUIResource(COLOR_ACCENT));
            UIManager.put("Table.selectionForeground", new ColorUIResource(Color.WHITE));
            UIManager.put("TableHeader.background", new ColorUIResource(COLOR_PRIMARY));
            UIManager.put("TableHeader.foreground", new ColorUIResource(Color.WHITE));
            UIManager.put("TableHeader.font", new FontUIResource(FONT_BOLD));

            UIManager.put("OptionPane.background", new ColorUIResource(COLOR_BACKGROUND));
            UIManager.put("OptionPane.messageForeground", new ColorUIResource(COLOR_TEXT));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setImageIcon(String fileName, JLabel component) {
        URL resource = getClass().getClassLoader().getResource("images/" + fileName);

        if (resource != null) {
            ImageIcon icon = new ImageIcon(resource);
            Image imagem = icon.getImage().getScaledInstance(
                    component.getWidth(), // largura do JLabel
                    component.getHeight(), // altura do JLabel
                    Image.SCALE_SMOOTH // suaviza a imagem
            );
            component.setIcon(new ImageIcon(imagem));
        } else {
            System.err.println("⚠️ Imagem não encontrada em: images/" + fileName);
        }
    }

    protected void showError(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Erro", javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    protected void showWarning(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
    }

    protected void showSuccess(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Sucesso",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    // Deprecated but kept for compatibility if needed, though setupGlobalUI handles
    // most
    protected void applyTheme() {
        // Re-apply if needed for specific components not covered by UIManager
        this.getContentPane().setBackground(COLOR_BACKGROUND);
    }
}
