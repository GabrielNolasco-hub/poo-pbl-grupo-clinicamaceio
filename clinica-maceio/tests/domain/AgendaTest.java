package domain;

import br.com.clinicamaceio.domain.aggregate.AgendaRoot;
import br.com.clinicamaceio.domain.entity.Consulta;
import br.com.clinicamaceio.domain.entity.Medico;
import br.com.clinicamaceio.domain.entity.Paciente;
import br.com.clinicamaceio.domain.valueobject.Cpf;
import br.com.clinicamaceio.domain.valueobject.Crm;
import br.com.clinicamaceio.domain.valueobject.Endereco;
import br.com.clinicamaceio.domain.valueobject.Horario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes TDD das entidades Consulta e do Aggregate AgendaRoot.
 * Branch: feat/aggregates | Responsável: Guilherme (gpereirazm)
 */
@DisplayName("Consulta e AgendaRoot — Aggregate Domain")
class AgendaTest {

    private Paciente paciente;
    private Medico medico;
    private AgendaRoot agenda;

    @BeforeEach
    void setUp() {
        Endereco end = new Endereco("Rua das Alagoas", "10",
                                    Endereco.BairroMaceio.JATIUCA, "57036000");
        paciente = new Paciente(
            "José Alagoas Santos",
            new Cpf("529.982.247-25"),
            LocalDate.of(1990, 6, 20),
            "jose.alagoas@email.com", "82977778888",
            end
        );
        medico = new Medico(
            "Dr. Rodrigo Tenório",
            Crm.parse("56789/AL"),
            Medico.Especialidade.CLINICO_GERAL,
            "rodrigo.tenorio@clinicamaceio.com"
        );
        agenda = new AgendaRoot();
    }

    // ── Consulta ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar consulta presencial na UBS Pajuçará com status AGENDADA")
    void deveCriarConsultaPresencial() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);
        assertEquals(Consulta.StatusConsulta.AGENDADA, c.getStatus());
        assertEquals(Consulta.TipoConsulta.PRESENCIAL, c.getTipo());
    }

    @Test
    @DisplayName("Deve criar consulta de telemedicina com unidade ONLINE")
    void deveCriarConsultaTelemedicina() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(10),
                Consulta.TipoConsulta.TELEMEDICINA, Consulta.UnidadeMaceio.TELEMEDICINA_ONLINE);
        assertEquals(Consulta.TipoConsulta.TELEMEDICINA, c.getTipo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar telemedicina com unidade presencial")
    void deveLancarExcecaoParaTelemedicinaComUnidadeErrada() {
        assertThrows(IllegalArgumentException.class, () ->
            new Consulta(paciente, medico, horarioAmanha(11),
                    Consulta.TipoConsulta.TELEMEDICINA, Consulta.UnidadeMaceio.UBS_POÇO)
        );
    }

    @Test
    @DisplayName("Deve confirmar consulta AGENDADA")
    void deveConfirmarConsultaAgendada() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_SERRARIA);
        c.confirmar();
        assertEquals(Consulta.StatusConsulta.CONFIRMADA, c.getStatus());
    }

    @Test
    @DisplayName("Deve cancelar consulta com motivo")
    void deveCancelarConsultaComMotivo() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_BENEDITO_BENTES);
        c.cancelar("Paciente não pode comparecer");
        assertEquals(Consulta.StatusConsulta.CANCELADA, c.getStatus());
        assertEquals("Paciente não pode comparecer", c.getMotivoCancelamento());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar sem motivo")
    void deveLancarExcecaoAoCancelarSemMotivo() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);
        assertThrows(IllegalArgumentException.class, () -> c.cancelar(""));
        assertThrows(IllegalArgumentException.class, () -> c.cancelar(null));
    }

    @Test
    @DisplayName("Deve lançar exceção ao confirmar consulta que não está AGENDADA")
    void deveLancarExcecaoAoConfirmarConsultaJaCancelada() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_SERRARIA);
        c.cancelar("Motivo qualquer");
        assertThrows(IllegalStateException.class, c::confirmar);
    }

    @Test
    @DisplayName("Deve realizar consulta e registrar observações")
    void deveRealizarConsulta() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_CLIMA_BOM);
        c.realizar("Paciente se queixou de febre. Prescrito antitérmico.");
        assertEquals(Consulta.StatusConsulta.REALIZADA, c.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar consulta com paciente inativo")
    void deveLancarExcecaoParaPacienteInativo() {
        paciente.inativar();
        assertThrows(IllegalStateException.class, () ->
            new Consulta(paciente, medico, horarioAmanha(9),
                    Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA)
        );
    }

    // ── AgendaRoot ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve agendar consulta na agenda sem conflito")
    void deveAgendarConsultaNaAgenda() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);
        agenda.agendarConsulta(c);
        assertEquals(1, agenda.todasAsConsultas().size());
    }

    @Test
    @DisplayName("Deve lançar exceção para conflito de horário do mesmo médico")
    void deveLancarExcecaoParaConflitoDeHorarioDoMedico() {
        Consulta c1 = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);
        agenda.agendarConsulta(c1);

        // Segundo paciente no mesmo horário com o mesmo médico
        Paciente outroPaciente = new Paciente(
            "Fernanda Rego", new Cpf("311.533.890-00"),
            LocalDate.of(1995, 7, 1), "fernanda@email.com", "82966665555",
            new Endereco("Rua B", "2", Endereco.BairroMaceio.FAROL, "57020000")
        );
        Consulta c2 = new Consulta(outroPaciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);

        assertThrows(IllegalStateException.class, () -> agenda.agendarConsulta(c2));
    }

    @Test
    @DisplayName("Deve permitir o mesmo médico com horários distintos")
    void devePermitirMesmoMedicoComHorariosDiferentes() {
        Paciente p2 = new Paciente(
            "Fernanda Rego", new Cpf("311.533.890-00"),
            LocalDate.of(1995, 7, 1), "fernanda@email.com", "82966665555",
            new Endereco("Rua B", "2", Endereco.BairroMaceio.FAROL, "57020000")
        );
        Consulta c1 = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);
        Consulta c2 = new Consulta(p2, medico, horarioAmanha(10),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);

        agenda.agendarConsulta(c1);
        assertDoesNotThrow(() -> agenda.agendarConsulta(c2));
        assertEquals(2, agenda.todasAsConsultas().size());
    }

    @Test
    @DisplayName("Deve confirmar consulta pelo ID na agenda")
    void deveConfirmarConsultaPeloId() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_SERRARIA);
        agenda.agendarConsulta(c);
        agenda.confirmarConsulta(c.getId());
        assertEquals(Consulta.StatusConsulta.CONFIRMADA, c.getStatus());
    }

    @Test
    @DisplayName("Deve cancelar consulta pelo ID na agenda")
    void deveCancelarConsultaPeloId() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_BENEDITO_BENTES);
        agenda.agendarConsulta(c);
        agenda.cancelarConsulta(c.getId(), "Feriado municipal em Maceió");
        assertEquals(Consulta.StatusConsulta.CANCELADA, c.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoParaIdInexistente() {
        assertThrows(IllegalArgumentException.class,
            () -> agenda.confirmarConsulta("id-que-nao-existe"));
    }

    @Test
    @DisplayName("Deve contar corretamente as consultas agendadas")
    void deveContarConsultasAgendadas() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);
        agenda.agendarConsulta(c);
        assertEquals(1, agenda.totalAgendadas());
        agenda.cancelarConsulta(c.getId(), "Motivo");
        assertEquals(0, agenda.totalAgendadas());
    }

    // ── Helper ────────────────────────────────────────────────────────────

    private Horario horarioAmanha(int hora) {
        LocalDateTime dt = LocalDateTime.now().plusDays(1)
                .withHour(hora).withMinute(0).withSecond(0).withNano(0);
        while (dt.getDayOfWeek().getValue() >= 6) dt = dt.plusDays(1);
        return new Horario(dt);
    }
}
