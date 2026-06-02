package br.com.clinicamaceio.application.usecase;

import br.com.clinicamaceio.domain.aggregate.AgendaRoot;

/**
 * Caso de Uso: Cancelar uma consulta agendada na ClinicaMaceió.
 *
 * Branch: feat/application | Responsável: Deco (AlexandreAlbuquerque-hub)
 */
public class CancelarConsultaUseCase {

    private final AgendaRoot agenda;

    public CancelarConsultaUseCase(AgendaRoot agenda) {
        if (agenda == null)
            throw new IllegalArgumentException("AgendaRoot é obrigatório.");
        this.agenda = agenda;
    }

    /**
     * Cancela a consulta identificada pelo id informado.
     *
     * @param consultaId ID único da consulta
     * @param motivo     Motivo obrigatório do cancelamento
     */
    public void executar(String consultaId, String motivo) {
        if (consultaId == null || consultaId.isBlank())
            throw new IllegalArgumentException("ID da consulta não pode ser vazio.");
        if (motivo == null || motivo.isBlank())
            throw new IllegalArgumentException("Motivo de cancelamento é obrigatório.");

        agenda.cancelarConsulta(consultaId, motivo);
    }
}
