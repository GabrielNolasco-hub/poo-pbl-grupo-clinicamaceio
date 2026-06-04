package br.com.clinicamaceio.application.usecase;

import br.com.clinicamaceio.domain.aggregate.AgendaRoot;
import br.com.clinicamaceio.domain.entity.Consulta;
import br.com.clinicamaceio.domain.entity.Medico;
import br.com.clinicamaceio.domain.entity.Paciente;
import br.com.clinicamaceio.domain.valueobject.Horario;

/**
 * Caso de Uso: Agendar uma consulta na ClinicaMaceió.
 * Orquestra as entidades e o aggregate root, sem conter regras de domínio.
 *
 * Branch: feat/application | Responsável: Deco (AlexandreAlbuquerque-hub)
 */
public class AgendarConsultaUseCase {

    private final AgendaRoot agenda;

    public AgendarConsultaUseCase(AgendaRoot agenda) {
        if (agenda == null)
            throw new IllegalArgumentException("AgendaRoot é obrigatório.");
        this.agenda = agenda;
    }

    /**
     * Executa o agendamento de uma consulta.
     *
     * @return a Consulta criada e registrada na agenda
     */
    public Consulta executar(Paciente paciente,
                             Medico medico,
                             Horario horario,
                             Consulta.TipoConsulta tipo,
                             Consulta.UnidadeMaceio unidade) {

        Consulta consulta = new Consulta(paciente, medico, horario, tipo, unidade);
        agenda.agendarConsulta(consulta);
        return consulta;
    }
}
 
