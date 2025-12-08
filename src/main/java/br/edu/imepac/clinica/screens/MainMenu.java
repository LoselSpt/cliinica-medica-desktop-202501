package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.daos.MedicoDao;
import br.edu.imepac.clinica.daos.PessoaDao;
import br.edu.imepac.clinica.daos.UsuarioDao;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.EnumFuncionalidades;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.entidades.Usuario;
import br.edu.imepac.clinica.screens.especialidades.EspecialidadeAddForm;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class MainMenu extends BaseScreen {
    private Usuario usuarioLogado;
    private JPanel contentPanel;

    public MainMenu(Usuario usuario) {
        this.usuarioLogado = usuario;
        setTitle("HealthWay - " + usuario.getFuncionario().getNome());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(COLOR_PRIMARY);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // App Title in Sidebar
        JLabel appTitle = new JLabel("HEALTHWAY");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(appTitle);

        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // User Info
        JLabel lblUser = new JLabel(usuarioLogado.getFuncionario().getNome());
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(Color.LIGHT_GRAY);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblUser);

        JLabel lblRole = new JLabel(usuarioLogado.getPerfil().getNome());
        lblRole.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRole.setForeground(COLOR_ACCENT);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblRole);

        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Navigation Buttons
        Set<EnumFuncionalidades> funcs = usuarioLogado.getPerfil().getFuncionalidades();
        String role = normalizeString(usuarioLogado.getPerfil().getNome());

        addSidebarButton(sidebar, "Dashboard", e -> showDashboard());

        // Fix for GERENTE: Add Especialidades and Convenios
        if (role.equals("GERENTE")) {
            addSidebarButton(sidebar, "Especialidades", e -> new EspecialidadeAddForm().setVisible(true));
            addSidebarButton(sidebar, "Convênios", e -> new ConvenioAddForm().setVisible(true));
        } else if (funcs.contains(EnumFuncionalidades.CADASTRAR_ESPECIALIDADE)) {
            addSidebarButton(sidebar, "Especialidades", e -> new EspecialidadeAddForm().setVisible(true));
        }

        if (funcs.contains(EnumFuncionalidades.AGENDAR_CONSULTA)) {
            addSidebarButton(sidebar, "Agendar Consulta", e -> new AgendamentoForm(usuarioLogado).setVisible(true));
        }
        if (funcs.contains(EnumFuncionalidades.REALIZAR_CONSULTA)) {
            addSidebarButton(sidebar, "Atendimento Médico",
                    e -> new AtendimentoMedicoForm(usuarioLogado).setVisible(true));
        }
        if (funcs.contains(EnumFuncionalidades.CADASTRAR_PACIENTE)) {
            addSidebarButton(sidebar, "Pacientes", e -> new PacienteListaForm().setVisible(true));
        }
        if (funcs.contains(EnumFuncionalidades.CADASTRAR_MEDICO)) {
            addSidebarButton(sidebar, "Médicos", e -> new MedicoListaForm().setVisible(true));
        }
        if (funcs.contains(EnumFuncionalidades.DELETAR_CONSULTA)) {
            addSidebarButton(sidebar, "Gerenciar Consultas",
                    e -> new ConsultaListaForm(usuarioLogado).setVisible(true));
        }
        if (funcs.contains(EnumFuncionalidades.CADASTRAR_USUARIO)) {
            addSidebarButton(sidebar, "Usuários", e -> new UsuarioAddForm().setVisible(true));
        }

        sidebar.add(Box.createVerticalGlue()); // Push Logout to bottom

        JButton btnLogout = createSidebarButton("Sair");
        btnLogout.setBackground(new Color(192, 57, 43)); // Red for logout
        btnLogout.addActionListener(e -> {
            this.dispose();
            new LoginForm().setVisible(true);
        });
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // --- Main Content Area ---
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(COLOR_BACKGROUND);
        add(contentPanel, BorderLayout.CENTER);

        // Show Dashboard by default
        showDashboard();
    }

    private void addSidebarButton(JPanel sidebar, String text, java.awt.event.ActionListener action) {
        JButton btn = createSidebarButton(text);
        btn.addActionListener(action);
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(COLOR_PRIMARY_DARK); // Dark Blue
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.getText().equals("Sair"))
                    btn.setBackground(COLOR_ACCENT);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getText().equals("Sair"))
                    btn.setBackground(COLOR_PRIMARY_DARK);
            }
        });

        return btn;
    }

    private void showDashboard() {
        contentPanel.removeAll();

        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BorderLayout());
        dashboard.setBackground(COLOR_BACKGROUND);
        dashboard.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JLabel lblDash = new JLabel("Dashboard");
        lblDash.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblDash.setForeground(COLOR_TEXT);
        dashboard.add(lblDash, BorderLayout.NORTH);

        // Cards Container (Common for all)
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(COLOR_BACKGROUND);
        cardsPanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        // Fetch Real Data
        String totalPacientes = "0";
        String consultasHoje = "0";

        try {
            PessoaDao pessoaDao = new PessoaDao();
            // Use contarPacientes to exclude doctors and users
            totalPacientes = String.valueOf(pessoaDao.contarPacientes());

            ConsultaDao consultaDao = new ConsultaDao();
            consultasHoje = String.valueOf(consultaDao.contarConsultasHoje());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        cardsPanel.add(createCard("Pacientes Cadastrados", totalPacientes, new Color(41, 128, 185)));
        cardsPanel.add(createCard("Consultas Hoje", consultasHoje, new Color(39, 174, 96)));
        cardsPanel.add(createCard("Alertas Pendentes", "0", new Color(231, 76, 60)));

        JPanel cardsWrapper = new JPanel(new BorderLayout());
        cardsWrapper.setBackground(COLOR_BACKGROUND);
        cardsWrapper.add(cardsPanel, BorderLayout.NORTH);

        // --- Role Based Content ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(COLOR_SURFACE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                new EmptyBorder(20, 20, 20, 20)));

        String role = normalizeString(usuarioLogado.getPerfil().getNome());

        if (role.equals("GERENTE")) {
            createGerenteView(bottomPanel);
        } else if (role.equals("MEDICO")) {
            createMedicoView(bottomPanel);
        } else {
            // Default / SECRETARIA
            createSecretariaView(bottomPanel);
        }

        cardsWrapper.add(bottomPanel, BorderLayout.CENTER);

        dashboard.add(cardsWrapper, BorderLayout.CENTER);

        contentPanel.add(dashboard, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void createGerenteView(JPanel panel) {
        JLabel lbl = new JLabel("Usuários do Sistema");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXT);
        panel.add(lbl, BorderLayout.NORTH);

        // Added "Ação" column
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Login", "Perfil", "Nome", "Status", "Ação" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only button is editable
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(COLOR_PRIMARY_DARK);
        table.getTableHeader().setForeground(Color.WHITE);

        // Use custom renderer/editor for "Editar" button
        table.getColumn("Ação").setCellRenderer(new ButtonRenderer("Editar"));
        table.getColumn("Ação").setCellEditor(new UserEditButtonEditor(new JCheckBox(), model, this::showDashboard)); // Refresh
                                                                                                                      // on
                                                                                                                      // success

        try {
            UsuarioDao dao = new UsuarioDao();
            List<Usuario> usuarios = dao.buscarTodos();
            for (Usuario u : usuarios) {
                model.addRow(new Object[] {
                        u.getId(),
                        u.getLogin(),
                        u.getPerfil().getNome(),
                        u.getFuncionario().getNome(),
                        u.getStatus(),
                        "Editar"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(COLOR_BACKGROUND);
        panel.add(scroll, BorderLayout.CENTER);
    }

    private void createMedicoView(JPanel panel) {
        JLabel lbl = new JLabel("Minhas Próximas Consultas");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(COLOR_TEXT);
        panel.add(lbl, BorderLayout.NORTH);

        // Add "Ação" column for the button
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "ID", "Data/Hora", "Paciente", "Status", "Motivo", "Ação" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only the button column is editable
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(35); // Taller rows for buttons
        table.getTableHeader().setBackground(COLOR_PRIMARY_DARK);
        table.getTableHeader().setForeground(Color.WHITE);

        // Button Renderer and Editor
        table.getColumn("Ação").setCellRenderer(new ButtonRenderer("Atender"));
        table.getColumn("Ação").setCellEditor(new DoctorButtonEditor(new JCheckBox()));

        try {
            MedicoDao medicoDao = new MedicoDao();
            Medico medico = medicoDao.buscarPorIdPessoa(usuarioLogado.getFuncionario().getId());

            if (medico != null) {
                ConsultaDao dao = new ConsultaDao();
                List<Consulta> consultas = dao.buscarPorMedico(medico.getId());
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                for (Consulta c : consultas) {
                    model.addRow(new Object[] {
                            c.getId(),
                            c.getDataHora().format(fmt),
                            c.getPaciente().getNome(),
                            c.getStatus(),
                            c.getMotivo(),
                            "Atender" // Button text
                    });
                }
            } else {
                panel.add(new JLabel(
                        "Aviso: Seu usuário não está vinculado a um perfil de Médico completo. Contate o Gerente."),
                        BorderLayout.SOUTH);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(COLOR_BACKGROUND);
        panel.add(scroll, BorderLayout.CENTER);
    }

    private void createSecretariaView(JPanel panel) {
        JLabel lblRecent = new JLabel("Atalhos Rápidos");
        lblRecent.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRecent.setForeground(COLOR_TEXT);
        panel.add(lblRecent, BorderLayout.NORTH);

        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        quickActions.setBackground(COLOR_SURFACE);

        JButton btnNewPatient = new JButton("Novo Paciente");
        btnNewPatient.setBackground(COLOR_PRIMARY); // Dark Blue
        btnNewPatient.setForeground(Color.WHITE);
        btnNewPatient.addActionListener(e -> new PacienteListaForm().setVisible(true));
        quickActions.add(btnNewPatient);

        JButton btnNewAppt = new JButton("Nova Consulta");
        btnNewAppt.setBackground(COLOR_PRIMARY); // Dark Blue
        btnNewAppt.setForeground(Color.WHITE);
        btnNewAppt.addActionListener(e -> new AgendamentoForm(usuarioLogado).setVisible(true));
        quickActions.add(btnNewAppt);

        panel.add(quickActions, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(color);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(200, 120));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValue.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    private String normalizeString(String str) {
        if (str == null)
            return "";
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").toUpperCase();
    }

    // --- Button Renderer/Editor Inner Classes ---

    class ButtonRenderer extends JButton implements TableCellRenderer {
        private String label;

        public ButtonRenderer(String label) {
            this.label = label;
            setOpaque(true);
            setBackground(COLOR_PRIMARY);
            setForeground(Color.WHITE);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? label : value.toString());
            return this;
        }
    }

    // Editor for Doctor Actions
    class DoctorButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;

        public DoctorButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(COLOR_PRIMARY);
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table;
            label = (value == null) ? "Atender" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int row = table.getSelectedRow();
                Long idConsulta = (Long) table.getValueAt(row, 0);
                String status = (String) table.getValueAt(row, 3);

                if ("ATENDIDO".equals(status) || "CANCELADA".equals(status)) {
                    JOptionPane.showMessageDialog(button, "Esta consulta já foi finalizada ou cancelada.");
                } else {
                    try {
                        Consulta c = new Consulta();
                        c.setId(idConsulta);

                        ConsultaDao dao = new ConsultaDao();
                        List<Consulta> all = dao.buscarTodas();
                        for (Consulta con : all) {
                            if (con.getId().equals(idConsulta)) {
                                c = con;
                                break;
                            }
                        }

                        ProntuarioForm form = new ProntuarioForm(c);
                        form.addWindowListener(new java.awt.event.WindowAdapter() {
                            @Override
                            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                                showDashboard();
                            }
                        });
                        form.setVisible(true);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    // Editor for User Edit Actions
    class UserEditButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private Runnable refreshCallback;

        public UserEditButtonEditor(JCheckBox checkBox, DefaultTableModel model, Runnable refreshCallback) {
            super(checkBox);
            this.refreshCallback = refreshCallback;
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(COLOR_ACCENT);
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table;
            label = (value == null) ? "Editar" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int row = table.getSelectedRow();
                Long idUsuario = (Long) table.getValueAt(row, 0);

                try {
                    // Fetch full user to edit
                    UsuarioDao dao = new UsuarioDao();
                    List<Usuario> all = dao.buscarTodos(); // Inefficient but safe
                    Usuario target = null;
                    for (Usuario u : all) {
                        if (u.getId().equals(idUsuario)) {
                            target = u;
                            break;
                        }
                    }

                    if (target != null) {
                        new UsuarioUpdateForm(target, refreshCallback).setVisible(true);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
