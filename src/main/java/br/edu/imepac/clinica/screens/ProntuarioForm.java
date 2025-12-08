package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.daos.ProntuarioDao;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.Prontuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class ProntuarioForm extends BaseScreen {
    private Consulta consulta;
    private JTextArea txtHistorico;
    private JTextArea txtReceituario;
    private JTextArea txtExames;
    private JTextArea txtObservacoes;
    private ProntuarioDao dao;
    private ConsultaDao consultaDao;

    public ProntuarioForm(Consulta consulta) {
        super("Prontuário Eletrônico");
        this.consulta = consulta;
        this.dao = new ProntuarioDao();
        this.consultaDao = new ConsultaDao();

        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
        carregarDados();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Prontuário: " + consulta.getPaciente().getNome());
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("Médico: " + consulta.getMedico().getCrm());
        subtitle.setFont(FONT_REGULAR);
        subtitle.setForeground(Color.LIGHT_GRAY);
        header.add(subtitle, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.setBackground(COLOR_SURFACE);
        tabs.setForeground(COLOR_TEXT);

        txtHistorico = createTextArea();
        tabs.addTab("Histórico", new JScrollPane(txtHistorico));

        txtExames = createTextArea();
        tabs.addTab("Exames", new JScrollPane(txtExames));

        txtReceituario = createTextArea();
        tabs.addTab("Receituário", new JScrollPane(txtReceituario));

        txtObservacoes = createTextArea();
        tabs.addTab("Observações", new JScrollPane(txtObservacoes));

        add(tabs, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footer.setBackground(COLOR_BACKGROUND);

        JButton btnCancel = new JButton("Fechar");
        btnCancel.setBackground(COLOR_SURFACE);
        btnCancel.setForeground(COLOR_TEXT);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSalvar = new JButton("Finalizar Consulta");
        btnSalvar.setBackground(COLOR_PRIMARY); // Dark Blue
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.addActionListener(e -> salvar());

        footer.add(btnCancel);
        footer.add(btnSalvar);
        add(footer, BorderLayout.SOUTH);
    }

    private JTextArea createTextArea() {
        JTextArea txt = new JTextArea();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBackground(COLOR_SURFACE);
        txt.setForeground(COLOR_TEXT);
        txt.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return txt;
    }

    private void carregarDados() {
        try {
            Prontuario p = dao.buscarPorConsulta(consulta.getId());
            if (p != null) {
                // Split historico to extract observacoes if present
                String fullHistorico = p.getHistorico();
                if (fullHistorico != null && fullHistorico.contains("[OBSERVAÇÕES]")) {
                    String[] parts = fullHistorico.split("\\[OBSERVAÇÕES\\]");
                    txtHistorico.setText(parts[0].trim());
                    if (parts.length > 1) {
                        txtObservacoes.setText(parts[1].trim());
                    }
                } else {
                    txtHistorico.setText(fullHistorico);
                }

                txtReceituario.setText(p.getReceituario());
                txtExames.setText(p.getExames());
            }
        } catch (SQLException e) {
            showError("Erro ao carregar prontuário: " + e.getMessage());
        }
    }

    private void salvar() {
        try {
            // Combine Historico and Observacoes
            String historicoFinal = txtHistorico.getText();
            String obs = txtObservacoes.getText().trim();
            if (!obs.isEmpty()) {
                historicoFinal += "\n\n[OBSERVAÇÕES]\n" + obs;
            }

            Prontuario p = new Prontuario(
                    consulta.getId(),
                    historicoFinal,
                    txtReceituario.getText(),
                    txtExames.getText());
            dao.salvar(p);

            // Update Status to ATENDIDO
            consultaDao.atualizarStatus(consulta.getId(), "ATENDIDO");

            showSuccess("Consulta finalizada e prontuário salvo com sucesso!");
            this.dispose();
        } catch (SQLException e) {
            showError("Erro ao salvar: " + e.getMessage());
        }
    }
}
