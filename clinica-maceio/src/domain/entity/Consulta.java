package br.com.clinicamaceio.domain.entity;

import br.com.clinicamaceio.domain.valueobject.Horario;

import java.util.UUID;

/**
 * Entidade central do domínio — representa uma consulta médica na ClinicaMaceió.
 * Pode ser presencial (em um dos postos de Maceió) ou por telemedicina (online).
 *
 * Branch: feat/domain-core | Responsável: Gabriel (GabrielNolasco-hub)
 */
public class Consulta {

    public enum StatusConsulta {
        AGENDADA, CONFIRMADA, REALIZADA, CANCELADA, FALTA
    }

    public enum TipoConsulta {
        PRESENCIAL, TELEMEDICINA
    }

    /** Unidades reais de saúde popular em Maceió/AL */
    public enum UnidadeMaceio {
        UBS_PAJUCARA("UBS Pajuçará — R. Jangadeiros Alagoanos"),
        UBS_POÇO("UBS Poço — Av. Fernandes Lima"),
        UBS_SERRARIA("UBS Serraria — R. Rotary"),
        UBS_BENEDITO_BENTES("UBS Benedito Bentes — Av. Gustavo Paiva"),
        UBS_CLIMA_BOM("UBS Clima Bom — R. São Miguel"),
        TELEMEDICINA_ONLINE("Atendimento Online — Link enviado por e-mail");

        private final String descricao;
        UnidadeMaceio(String descricao) { this.descricao = descricao; }
        public String getDescricao() { return descricao; }
    }

    private final String id;
    private final Paciente paciente;
    private final Medico medico;
    private final Horario horario;
    private final TipoConsulta tipo;
    private final UnidadeMaceio unidade;
    private StatusConsulta status;
    private String linkTelemedicina;
    private String motivoCancelamento;
    private String observacoes;

    public Consulta(Paciente paciente, Medico medico, Horario horario,
                    TipoConsulta tipo, UnidadeMaceio unidade) {
        validar(paciente, medico, horario, tipo, unidade);

        this.id       = UUID.randomUUID().toString();
        this.paciente = paciente;
        this.medico   = medico;
        this.horario  = horario;
        this.tipo     = tipo;
        this.unidade  = unidade;
        this.status   = StatusConsulta.AGENDADA;
    }

    // ── Validações privadas ────────────────────────────────────────────────

    private void validar(Paciente paciente, Medico medico, Horario horario,
                         TipoConsulta tipo, UnidadeMaceio unidade) {
        if (paciente == null)  throw new IllegalArgumentException("Paciente não pode ser nulo.");
        if (medico == null)    throw new IllegalArgumentException("Médico não pode ser nulo.");
        if (horario == null)   throw new IllegalArgumentException("Horário não pode ser nulo.");
        if (tipo == null)      throw new IllegalArgumentException("Tipo de consulta não pode ser nulo.");
        if (unidade == null)   throw new IllegalArgumentException("Unidade não pode ser nula.");

        if (!paciente.isAtivo())
            throw new IllegalStateException("Paciente inativo não pode realizar consultas.");
        if (!medico.isDisponivel())
            throw new IllegalStateException("Médico indisponível no momento.");

        if (tipo == TipoConsulta.TELEMEDICINA && unidade != UnidadeMaceio.TELEMEDICINA_ONLINE)
            throw new IllegalArgumentException("Consultas de telemedicina devem usar a unidade TELEMEDICINA_ONLINE.");
    }

    // ── Comportamentos de domínio ──────────────────────────────────────────

    public void confirmar() {
        if (status != StatusConsulta.AGENDADA)
            throw new IllegalStateException("Somente consultas AGENDADAS podem ser confirmadas.");
        this.status = StatusConsulta.CONFIRMADA;
    }

    public void realizar(String observacoes) {
        if (status != StatusConsulta.CONFIRMADA && status != StatusConsulta.AGENDADA)
            throw new IllegalStateException("Consulta não pode ser realizada no estado atual: " + status);
        this.status = StatusConsulta.REALIZADA;
        this.observacoes = observacoes;
    }

    public void cancelar(String motivo) {
        if (status == StatusConsulta.REALIZADA)
            throw new IllegalStateException("Não é possível cancelar uma consulta já realizada.");
        if (motivo == null || motivo.isBlank())
            throw new IllegalArgumentException("Motivo de cancelamento é obrigatório.");
        this.status = StatusConsulta.CANCELADA;
        this.motivoCancelamento = motivo;
    }

    public void registrarFalta() {
        if (status != StatusConsulta.AGENDADA && status != StatusConsulta.CONFIRMADA)
            throw new IllegalStateException("Falta só pode ser registrada em consultas AGENDADAS ou CONFIRMADAS.");
        this.status = StatusConsulta.FALTA;
    }

    public void definirLinkTelemedicina(String link) {
        if (tipo != TipoConsulta.TELEMEDICINA)
            throw new IllegalStateException("Link de telemedicina só se aplica a consultas online.");
        if (link == null || link.isBlank())
            throw new IllegalArgumentException("Link de telemedicina não pode ser vazio.");
        this.linkTelemedicina = link;
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public String getId()                  { return id; }
    public Paciente getPaciente()          { return paciente; }
    public Medico getMedico()              { return medico; }
    public Horario getHorario()            { return horario; }
    public TipoConsulta getTipo()          { return tipo; }
    public UnidadeMaceio getUnidade()      { return unidade; }
    public StatusConsulta getStatus()      { return status; }
    public String getLinkTelemedicina()    { return linkTelemedicina; }
    public String getMotivoCancelamento()  { return motivoCancelamento; }
    public String getObservacoes()         { return observacoes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consulta)) return false;
        return id.equals(((Consulta) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Consulta{id=" + id.substring(0, 8) + "..., paciente=" + paciente.getNome() +
               ", medico=" + medico.getNome() + ", horario=" + horario + ", status=" + status + "}";
    }
}
