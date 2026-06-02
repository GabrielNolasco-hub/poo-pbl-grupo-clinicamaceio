package br.com.clinicamaceio.domain.aggregate;

import br.com.clinicamaceio.domain.entity.Consulta;
import br.com.clinicamaceio.domain.entity.Paciente;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate que representa o Prontuário Eletrônico de um paciente.
 * Mantém o histórico de registros clínicos e garante integridade dos dados.
 * Apenas consultas REALIZADAS geram registros no prontuário.
 *
 * Branch: feat/aggregates | Responsável: Guilherme (gpereirazm)
 */
public class Prontuario {

    /**
     * Registro imutável de um atendimento clínico (Value Object interno).
     */
    public static final class RegistroClinico {
        private final String id;
        private final LocalDateTime dataRegistro;
        private final String medicoNome;
        private final String especialidade;
        private final String descricao;
        private final String prescricao;
        private final String diagnostico;

        public RegistroClinico(String medicoNome, String especialidade,
                               String descricao, String prescricao, String diagnostico) {
            if (descricao == null || descricao.isBlank())
                throw new IllegalArgumentException("Descrição do registro clínico é obrigatória.");
            this.id            = UUID.randomUUID().toString();
            this.dataRegistro  = LocalDateTime.now();
            this.medicoNome    = medicoNome;
            this.especialidade = especialidade;
            this.descricao     = descricao;
            this.prescricao    = prescricao;
            this.diagnostico   = diagnostico;
        }

        public String getId()             { return id; }
        public LocalDateTime getData()    { return dataRegistro; }
        public String getMedicoNome()     { return medicoNome; }
        public String getEspecialidade()  { return especialidade; }
        public String getDescricao()      { return descricao; }
        public String getPrescricao()     { return prescricao; }
        public String getDiagnostico()    { return diagnostico; }

        @Override
        public String toString() {
            return "[" + dataRegistro.toLocalDate() + "] Dr(a). " + medicoNome +
                   " (" + especialidade + "): " + diagnostico;
        }
    }

    private final String id;
    private final Paciente paciente;
    private final List<RegistroClinico> registros;
    private String alergias;
    private String historicoFamiliar;
    private String tipoSanguineo;

    public Prontuario(Paciente paciente) {
        if (paciente == null)
            throw new IllegalArgumentException("Prontuário requer um paciente válido.");
        this.id        = UUID.randomUUID().toString();
        this.paciente  = paciente;
        this.registros = new ArrayList<>();
    }

    // ── Comportamentos ────────────────────────────────────────────────────

    /**
     * Adiciona um registro ao prontuário somente se a consulta estiver REALIZADA.
     */
    public RegistroClinico adicionarRegistro(Consulta consulta,
                                             String prescricao,
                                             String diagnostico) {
        if (consulta.getStatus() != Consulta.StatusConsulta.REALIZADA)
            throw new IllegalStateException("Somente consultas REALIZADAS geram registro no prontuário.");
        if (!consulta.getPaciente().equals(paciente))
            throw new IllegalArgumentException("Esta consulta não pertence ao paciente deste prontuário.");

        RegistroClinico registro = new RegistroClinico(
            consulta.getMedico().getNome(),
            consulta.getMedico().getEspecialidade().name(),
            consulta.getObservacoes(),
            prescricao,
            diagnostico
        );
        registros.add(registro);
        return registro;
    }

    public void definirAlergias(String alergias) {
        this.alergias = alergias;
    }

    public void definirHistoricoFamiliar(String historico) {
        this.historicoFamiliar = historico;
    }

    public void definirTipoSanguineo(String tipo) {
        String[] validos = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};
        for (String v : validos) { if (v.equalsIgnoreCase(tipo)) { this.tipoSanguineo = tipo.toUpperCase(); return; } }
        throw new IllegalArgumentException("Tipo sanguíneo inválido: " + tipo);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public String getId()                          { return id; }
    public Paciente getPaciente()                  { return paciente; }
    public String getAlergias()                    { return alergias; }
    public String getHistoricoFamiliar()           { return historicoFamiliar; }
    public String getTipoSanguineo()               { return tipoSanguineo; }
    public List<RegistroClinico> getRegistros()    { return Collections.unmodifiableList(registros); }
    public int totalRegistros()                    { return registros.size(); }
}
