package br.com.clinicamaceio.domain.aggregate;

import br.com.clinicamaceio.domain.entity.Consulta;
import br.com.clinicamaceio.domain.entity.Medico;
import br.com.clinicamaceio.domain.entity.Paciente;
import br.com.clinicamaceio.domain.valueobject.Horario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregate Root responsável por gerenciar a consistência da agenda médica.
 * É o único ponto de entrada para agendar, confirmar ou cancelar consultas.
 * Garante invariantes: sem conflito de horário para o mesmo médico.
 *
 * Branch: feat/aggregates | Responsável: Guilherme (gpereirazm)
 */
public class AgendaRoot {

    /** Duração padrão de cada consulta na ClinicaMaceió: 30 minutos */
    private static final int DURACAO_CONSULTA_MINUTOS = 30;

    private final List<Consulta> consultas;

    public AgendaRoot() {
        this.consultas = new ArrayList<>();
    }

    // ── Comportamentos protegidos pelo Aggregate ───────────────────────────

    /**
     * Adiciona uma nova consulta à agenda, garantindo que não há conflito
     * de horário para o médico informado.
     */
    public void agendarConsulta(Consulta novaConsulta) {
        if (novaConsulta == null)
            throw new IllegalArgumentException("Consulta não pode ser nula.");

        verificarConflitoDeMedico(novaConsulta);
        verificarConflitoDepaciente(novaConsulta);
        consultas.add(novaConsulta);
    }

    public void confirmarConsulta(String consultaId) {
        Consulta consulta = buscarPorId(consultaId);
        consulta.confirmar();
    }

    public void cancelarConsulta(String consultaId, String motivo) {
        Consulta consulta = buscarPorId(consultaId);
        consulta.cancelar(motivo);
    }

    public void registrarFalta(String consultaId) {
        Consulta consulta = buscarPorId(consultaId);
        consulta.registrarFalta();
    }

    public void realizarConsulta(String consultaId, String observacoes) {
        Consulta consulta = buscarPorId(consultaId);
        consulta.realizar(observacoes);
    }

    // ── Consultas / Queries ────────────────────────────────────────────────

    public List<Consulta> consultasDoPaciente(Paciente paciente) {
        return consultas.stream()
                .filter(c -> c.getPaciente().equals(paciente))
                .collect(Collectors.toList());
    }

    public List<Consulta> consultasDoMedico(Medico medico) {
        return consultas.stream()
                .filter(c -> c.getMedico().equals(medico))
                .collect(Collectors.toList());
    }

    public List<Consulta> consultasDoDia(LocalDate dia) {
        return consultas.stream()
                .filter(c -> c.getHorario().getDataHora().toLocalDate().equals(dia))
                .collect(Collectors.toList());
    }

    public List<Consulta> todasAsConsultas() {
        return Collections.unmodifiableList(consultas);
    }

    public long totalAgendadas() {
        return consultas.stream()
                .filter(c -> c.getStatus() == Consulta.StatusConsulta.AGENDADA ||
                             c.getStatus() == Consulta.StatusConsulta.CONFIRMADA)
                .count();
    }

    // ── Regras de invariante (privadas) ───────────────────────────────────

    private void verificarConflitoDeMedico(Consulta nova) {
        boolean conflito = consultas.stream()
            .filter(c -> c.getMedico().equals(nova.getMedico()))
            .filter(c -> c.getStatus() != Consulta.StatusConsulta.CANCELADA)
            .anyMatch(c -> c.getHorario().conflitaCom(nova.getHorario(), DURACAO_CONSULTA_MINUTOS));

        if (conflito)
            throw new IllegalStateException(
                "Conflito de horário: o médico " + nova.getMedico().getNome() +
                " já possui consulta marcada nesse horário.");
    }

    private void verificarConflitoDepaciente(Consulta nova) {
        boolean conflito = consultas.stream()
            .filter(c -> c.getPaciente().equals(nova.getPaciente()))
            .filter(c -> c.getStatus() != Consulta.StatusConsulta.CANCELADA)
            .anyMatch(c -> c.getHorario().conflitaCom(nova.getHorario(), DURACAO_CONSULTA_MINUTOS));

        if (conflito)
            throw new IllegalStateException(
                "Conflito de horário: o paciente " + nova.getPaciente().getNome() +
                " já possui consulta marcada nesse horário.");
    }

    private Consulta buscarPorId(String id) {
        return consultas.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + id));
    }
}
