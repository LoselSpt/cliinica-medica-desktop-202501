package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.daos.ConsultaDao;
import br.edu.imepac.clinica.daos.MedicoDao;
import br.edu.imepac.clinica.entidades.Consulta;
import br.edu.imepac.clinica.entidades.EnumFuncionalidades;
import br.edu.imepac.clinica.entidades.Medico;
import br.edu.imepac.clinica.entidades.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultaListaForm extends BaseScreen {

    private JTable table;
    private DefaultTableModel tableModel;
    private List<Consulta> allConsultas;
    private Usuario usuarioLogado;

    // Filters
    private JTextField txtData;
    private JComboBox<Medico> comboMedico;
    private JComboBox<String> comboStatus;

    public ConsultaListaForm(Usuario usuario) {
        super("Agenda de Consultas");
        this.usuarioLogado = usuario;
        this.allConsultas = new ArrayList<>();

        setSize(1000, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        initComponents();
        carregarDadosIniciais();
        carregarConsultas();
    }

    private void initComponents() {
        // --- Header & Filters ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_PRIMARY);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Agenda de Consultas");
        title.setFont(FONT_TITLE);
        title.setForeground(Color.WHITE);
        topPanel.add(title, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setOpaque(false);

        // Date Filter
        JLabel lblData = new JLabel("Data (dd/MM/yyyy):");
        lblData.setForeground(Color.LIGHT_GRAY);
        txtData = new JTextField(10);

        // Doctor Filter
        JLabel lblMed = new JLabel("Médico:");
        lblMed.setForeground(Color.LIGHT_GRAY);
        comboMedico = new JComboBox<>();
        comboMedico.addItem(null); // All

        // Status Filter
        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setForeground(Color.LIGHT_GRAY);
        comboStatus = new JComboBox<>(new String[] { "Todos", "AGENDADA", "REALIZADA", "CANCELADA" });

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> aplicarFiltros());

        filterPanel.add(lblData);
        filterPanel.add(txtData);
        filterPanel.add(lblMed);
        filterPanel.add(comboMedico);
        filterPanel.add(lblStatus);
        filterPanel.add(comboStatus);
        filterPanel.add(btnFiltrar);

        topPanel.add(filterPanel, BorderLayout.CENTER);

        // Actions Toolbar (Right side or below filters)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> carregarConsultas());
        actionPanel.add(btnAtualizar);

        if (usuarioLogado.getPerfil().getFuncionalidades().contains(EnumFuncionalidades.DELETAR_CONSULTA)) {
            JButton btnCancelar = new JButton("Cancelar Selecionada");
            btnCancelar.setBackground(new Color(192, 57, 43));
            btnCancelar.addActionListener(e -> cancelarConsulta());
            actionPanel.add(btnCancelar);
        }

        topPanel.add(actionPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // --- Table ---
        tableModel = new DefaultTableModel(new Object[] { "ID", "Data/Hora", "Médico", "Paciente", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Custom Header
        table.getTableHeader().setBackground(COLOR_PRIMARY_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(FONT_BOLD);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_BACKGROUND);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void carregarDadosIniciais() {
        try {
            MedicoDao medicoDao = new MedicoDao();
            List<Medico> medicos = medicoDao.buscarTodos();
            for (Medico m : medicos) {
                comboMedico.addItem(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void carregarConsultas() {
        try {
            ConsultaDao dao = new ConsultaDao();
            allConsultas = dao.buscarTodas();
            aplicarFiltros();
        } catch (Exception e) {
            showError("Erro ao carregar consultas: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String dataFilter = txtData.getText().trim();
        Medico medicoFilter = (Medico) comboMedico.getSelectedItem();
        String statusFilter = (String) comboStatus.getSelectedItem();

        List<Consulta> filtered = allConsultas.stream()
                .filter(c -> {
                    boolean matchData = dataFilter.isEmpty()
                            || c.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).contains(dataFilter);
                    boolean matchMedico = medicoFilter == null
                            || (c.getMedico() != null && c.getMedico().getId().equals(medicoFilter.getId()));
                    boolean matchStatus = statusFilter.equals("Todos") || c.getStatus().equalsIgnoreCase(statusFilter);
                    return matchData && matchMedico && matchStatus;
                })
                .collect(Collectors.toList());

        atualizarTabela(filtered);
    }

    private void atualizarTabela(List<Consulta> lista) {
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Consulta c : lista) {
            tableModel.addRow(new Object[] {
                    c.getId(),
                    c.getDataHora().format(fmt),
                    c.getMedico().getNome(),
                    c.getPaciente().getNome(),
                    c.getStatus()
            });
        }
    }

    private void cancelarConsulta() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) table.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja cancelar a consulta ID " + id + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ConsultaDao dao = new ConsultaDao();
                    dao.cancelar(id);
                    showSuccess("Consulta cancelada com sucesso!");
                    carregarConsultas();
                } catch (SQLException e) {
                    showError("Erro ao cancelar consulta: " + e.getMessage());
                }
            }
        } else {
            showWarning("Selecione uma consulta na tabela.");
        }
    }
}
