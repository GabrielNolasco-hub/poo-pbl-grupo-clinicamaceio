package br.com.clinicamaceio.application.usecase;

import br.com.clinicamaceio.domain.aggregate.AgendaRoot;
import br.com.clinicamaceio.domain.aggregate.Prontuario;
import br.com.clinicamaceio.domain.entity.Consulta;

/**
 * Caso de Uso: Registrar a realização de uma consulta e gerar registro no prontuário.
 *
 * Branch: feat/application | Responsável: Deco (AlexandreAlbuquerque-hub)
 */
public class RealizarConsultaUseCase {

    private final AgendaRoot agenda;

    public RealizarConsultaUseCase(AgendaRoot agenda) {
        this.agenda = agenda;
    }

    /**
     * Marca a consulta como realizada e adiciona registro ao prontuário do paciente.
     *
     * @param consultaId  ID da consulta
     * @param observacoes Observações do atendimento
     * @param prontuario  Prontuário do paciente para registrar o resultado
     * @param prescricao  Prescrição médica (pode ser null)
     * @param diagnostico Diagnóstico dado pelo médico
     * @return RegistroClinico gerado no prontuário
     */
    public Prontuario.RegistroClinico executar(String consultaId,
                                               String observacoes,
                                               Prontuario prontuario,
                                               String prescricao,
                                               String diagnostico) {
        if (consultaId == null || consultaId.isBlank())
            throw new IllegalArgumentException("ID da consulta é obrigatório.");
        if (observacoes == null || observacoes.isBlank())
            throw new IllegalArgumentException("Observações do atendimento são obrigatórias.");

        agenda.realizarConsulta(consultaId, observacoes);

        // Recupera a consulta recém-realizada para registrar no prontuário
        Consulta consulta = agenda.todasAsConsultas().stream()
                .filter(c -> c.getId().equals(consultaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + consultaId));

        return prontuario.adicionarRegistro(consulta, prescricao, diagnostico);
    }
}
