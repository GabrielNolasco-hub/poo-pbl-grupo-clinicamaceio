package br.com.clinicamaceio.presentation.view;

import br.com.clinicamaceio.application.usecase.AgendarConsultaUseCase;
import br.com.clinicamaceio.application.usecase.CancelarConsultaUseCase;
import br.com.clinicamaceio.domain.aggregate.AgendaRoot;
import br.com.clinicamaceio.domain.entity.Consulta;
import br.com.clinicamaceio.domain.entity.Medico;
import br.com.clinicamaceio.domain.entity.Paciente;
import br.com.clinicamaceio.domain.valueobject.Cpf;
import br.com.clinicamaceio.domain.valueobject.Crm;
import br.com.clinicamaceio.domain.valueobject.Endereco;
import br.com.clinicamaceio.domain.valueobject.Horario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface gráfica principal — ClinicaMaceió Sistema de Agendamento.
 * Integra com a camada domain via use cases da camada application.
 *
 * Branch: feat/infrastructure-presentation | Responsável: Eric (EricPessoa-git)
 */
public class TelaPrincipal extends JFrame {

    private final AgendaRoot agenda;
    private final AgendarConsultaUseCase agendarUseCase;
    private final CancelarConsultaUseCase cancelarUseCase;

    private JTable tabelaConsultas;
    private DefaultTableModel modeloTabela;
    private JLabel labelStatus;

    // Dados de demonstração pré-carregados com contexto de Maceió
    private Paciente pacienteDemo;
    private Medico medicoDemoClinico;
    private Medico medicoDemoPediatra;

    public TelaPrincipal() {
        this.agenda          = new AgendaRoot();
        this.agendarUseCase  = new AgendarConsultaUseCase(agenda);
        this.cancelarUseCase = new CancelarConsultaUseCase(agenda);

        carregarDadosDemostracao();
        inicializarUI();
    }

    private void carregarDadosDemostracao() {
        try {
            Endereco endPaciente = new Endereco(
                "Rua das Alagoas", "123",
                Endereco.BairroMaceio.PAJUCARA, "57030000"
            );
            pacienteDemoClinico = new Paciente(
                "Maria das Graças Silva",
                new Cpf("529.982.247-25"),
                LocalDate.of(1985, 3, 15),
                "maria.gracas@email.com", "82988887777",
                endPaciente
            );

            medicoDemoClinico = new Medico(
                "Dr. Carlos Alberto Souza",
                Crm.parse("23456/AL"),
                Medico.Especialidade.CLINICO_GERAL,
                "carlos.souza@clinicamaceio.com"
            );

            medicoDemoPediatra = new Medico(
                "Dra. Ana Beatriz Lima",
                Crm.parse("34567/AL"),
                Medico.Especialidade.PEDIATRIA,
                "ana.lima@clinicamaceio.com"
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar dados de demonstração: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Paciente pacienteDemoClinico; // Alias para compatibilidade

    private void inicializarUI() {
        setTitle("ClinicaMaceió — Sistema de Agendamento e Telemedicina");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 248, 252));

        // Painel de cabeçalho
        JPanel header = criarCabecalho();
        add(header, BorderLayout.NORTH);

        // Painel central com tabela
        JPanel centro = criarPainelCentral();
        add(centro, BorderLayout.CENTER);

        // Painel de ações
        JPanel acoes = criarPainelAcoes();
        add(acoes, BorderLayout.SOUTH);
    }

    private JPanel criarCabecalho() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 102, 153));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titulo = new JLabel("🏥 ClinicaMaceió — Agendamento e Telemedicina");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel subtitulo = new JLabel("Maceió/AL — Saúde acessível para todos");
        subtitulo.setForeground(new Color(200, 230, 255));
        subtitulo.setFont(new Font("Arial", Font.ITALIC, 12));

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(subtitulo, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel criarPainelCentral() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));
        panel.setOpaque(false);

        String[] colunas = {"ID (resumo)", "Paciente", "Médico", "Horário", "Tipo", "Unidade", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaConsultas = new JTable(modeloTabela);
        tabelaConsultas.setFont(new Font("Arial", Font.PLAIN, 12));
        tabelaConsultas.setRowHeight(24);
        tabelaConsultas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabelaConsultas.getTableHeader().setBackground(new Color(0, 102, 153));
        tabelaConsultas.getTableHeader().setForeground(Color.WHITE);

        panel.add(new JScrollPane(tabelaConsultas), BorderLayout.CENTER);

        labelStatus = new JLabel("Pronto. Use os botões abaixo para gerenciar consultas.");
        labelStatus.setFont(new Font("Arial", Font.ITALIC, 11));
        labelStatus.setForeground(new Color(80, 80, 80));
        panel.add(labelStatus, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelAcoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(235, 240, 248));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));

        JButton btnAgendar    = criarBotao("📅 Agendar Consulta",   new Color(0, 128, 0));
        JButton btnCancelar   = criarBotao("❌ Cancelar Consulta",  new Color(180, 50, 50));
        JButton btnAtualizar  = criarBotao("🔄 Atualizar Lista",    new Color(0, 102, 153));

        btnAgendar.addActionListener(e -> agendarConsultaDemostracao());
        btnCancelar.addActionListener(e -> cancelarConsultaSelecionada());
        btnAtualizar.addActionListener(e -> atualizarTabela());

        panel.add(btnAgendar);
        panel.add(btnCancelar);
        panel.add(btnAtualizar);
        return panel;
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 38));
        return btn;
    }

    private void agendarConsultaDemostracao() {
        try {
            LocalDateTime amanha = LocalDateTime.now().plusDays(1)
                                                .withHour(9).withMinute(0).withSecond(0).withNano(0);
            // Avança para dia útil se necessário
            while (amanha.getDayOfWeek().getValue() >= 6) amanha = amanha.plusDays(1);

            Horario horario = new Horario(amanha);
            Consulta consulta = agendarUseCase.executar(
                pacienteDemoClinico,
                medicoDemoClinico,
                horario,
                Consulta.TipoConsulta.PRESENCIAL,
                Consulta.UnidadeMaceio.UBS_PAJUCARA
            );
            atualizarTabela();
            setStatus("✅ Consulta agendada: " + consulta.getPaciente().getNome() +
                      " com " + consulta.getMedico().getNome());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cancelarConsultaSelecionada() {
        int linha = tabelaConsultas.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta na tabela.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String idResumido = (String) modeloTabela.getValueAt(linha, 0);
        List<Consulta> todas = agenda.todasAsConsultas();

        agenda.todasAsConsultas().stream()
            .filter(c -> c.getId().startsWith(idResumido.replace("...", "")))
            .findFirst()
            .ifPresent(c -> {
                String motivo = JOptionPane.showInputDialog(this, "Motivo do cancelamento:");
                if (motivo != null && !motivo.isBlank()) {
                    try {
                        cancelarUseCase.executar(c.getId(), motivo);
                        atualizarTabela();
                        setStatus("❌ Consulta cancelada: " + c.getPaciente().getNome());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Consulta c : agenda.todasAsConsultas()) {
            modeloTabela.addRow(new Object[]{
                c.getId().substring(0, 8) + "...",
                c.getPaciente().getNome(),
                c.getMedico().getNome(),
                c.getHorario().toString(),
                c.getTipo().name(),
                c.getUnidade().name(),
                c.getStatus().name()
            });
        }
    }

    private void setStatus(String msg) {
        labelStatus.setText(msg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new TelaPrincipal().setVisible(true);
        });
    }
}
