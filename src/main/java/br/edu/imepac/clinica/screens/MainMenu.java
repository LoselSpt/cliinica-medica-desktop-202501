package br.edu.imepac.clinica.screens;

import br.edu.imepac.clinica.entidades.EnumFuncionalidades;
import br.edu.imepac.clinica.entidades.Usuario;
import br.edu.imepac.clinica.screens.especialidades.EspecialidadeAddForm;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Set;

public class MainMenu extends JFrame {
    private Usuario usuarioLogado;

    public MainMenu(Usuario usuario) {
        this.usuarioLogado = usuario;
        setTitle("Clínica Médica - " + usuario.getFuncionario().getNome() + " [" + usuario.getPerfil().getNome() + "]");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        Set<EnumFuncionalidades> funcs = usuarioLogado.getPerfil().getFuncionalidades();

        // Menu Cadastros
        JMenu menuCadastros = new JMenu("Cadastros");
        boolean hasCadastros = false;

        if (funcs.contains(EnumFuncionalidades.CADASTRAR_ESPECIALIDADE)) {
            JMenuItem item = new JMenuItem("Especialidades");
            item.addActionListener(e -> new EspecialidadeAddForm().setVisible(true));
            menuCadastros.add(item);
            hasCadastros = true;
        }

        if (funcs.contains(EnumFuncionalidades.CADASTRAR_MEDICO)) {
            JMenuItem item = new JMenuItem("Médicos");
            // item.addActionListener(e -> new MedicoListaForm().setVisible(true));
            menuCadastros.add(item);
            hasCadastros = true;
        }

        if (funcs.contains(EnumFuncionalidades.CADASTRAR_PACIENTE)) {
            JMenuItem item = new JMenuItem("Pacientes");
            item.addActionListener(e -> new PacienteListaForm().setVisible(true));
            menuCadastros.add(item);
            hasCadastros = true;
        }

        if (funcs.contains(EnumFuncionalidades.CADASTRAR_CONVENIO)) {
            JMenuItem item = new JMenuItem("Convênios");
            // item.addActionListener(e -> new ConvenioListaForm().setVisible(true));
            menuCadastros.add(item);
            hasCadastros = true;
        }

        if (hasCadastros)
            menuBar.add(menuCadastros);

        // Menu Atendimento
        JMenu menuAtendimento = new JMenu("Atendimento");
        boolean hasAtendimento = false;

        if (funcs.contains(EnumFuncionalidades.AGENDAR_CONSULTA)) {
            JMenuItem item = new JMenuItem("Agendar Consulta");
            item.addActionListener(e -> new AgendamentoForm(usuarioLogado).setVisible(true));
            menuAtendimento.add(item);
            hasAtendimento = true;
        }

        if (funcs.contains(EnumFuncionalidades.REALIZAR_CONSULTA)) {
            JMenuItem item = new JMenuItem("Realizar Consulta (Médico)");
            item.addActionListener(e -> new AtendimentoMedicoForm(usuarioLogado).setVisible(true));
            menuAtendimento.add(item);
            hasAtendimento = true;
        }

        if (hasAtendimento)
            menuBar.add(menuAtendimento);

        setJMenuBar(menuBar);
    }
}
