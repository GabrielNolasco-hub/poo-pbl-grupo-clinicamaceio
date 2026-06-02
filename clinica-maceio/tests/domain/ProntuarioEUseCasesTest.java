package domain;

import br.com.clinicamaceio.application.usecase.AgendarConsultaUseCase;
import br.com.clinicamaceio.application.usecase.CancelarConsultaUseCase;
import br.com.clinicamaceio.application.usecase.RealizarConsultaUseCase;
import br.com.clinicamaceio.domain.aggregate.AgendaRoot;
import br.com.clinicamaceio.domain.aggregate.Prontuario;
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
 * Testes TDD do Prontuário e dos Use Cases da camada Application.
 * Branch: feat/aggregates (Prontuário) e feat/application (Use Cases)
 * Responsáveis: Guilherme (gpereirazm) e Deco (AlexandreAlbuquerque-hub)
 */
@DisplayName("Prontuário e Use Cases — Application Layer")
class ProntuarioEUseCasesTest {

    private Paciente paciente;
    private Medico medico;
    private AgendaRoot agenda;

    @BeforeEach
    void setUp() {
        Endereco end = new Endereco("Av. Gustavo Paiva", "3200",
                                    Endereco.BairroMaceio.BENEDITO_BENTES, "57084000");
        paciente = new Paciente(
            "Antônio Benedito Costa",
            new Cpf("529.982.247-25"),
            LocalDate.of(1975, 11, 10),
            "antonio.costa@email.com", "82955554444",
            end
        );
        medico = new Medico(
            "Dra. Lucia Wanderley",
            Crm.parse("67890/AL"),
            Medico.Especialidade.CLINICO_GERAL,
            "lucia.wanderley@clinicamaceio.com"
        );
        agenda = new AgendaRoot();
    }

    // ── Prontuário ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar prontuário para paciente")
    void deveCriarProntuario() {
        Prontuario p = new Prontuario(paciente);
        assertEquals(paciente, p.getPaciente());
        assertEquals(0, p.totalRegistros());
    }

    @Test
    @DisplayName("Deve adicionar registro clínico após consulta realizada")
    void deveAdicionarRegistroAposConsultaRealizada() {
        Consulta c = criarConsultaRealizada();
        Prontuario p = new Prontuario(paciente);
        Prontuario.RegistroClinico reg = p.adicionarRegistro(c, "Dipirona 500mg", "Síndrome gripal");

        assertNotNull(reg);
        assertEquals(1, p.totalRegistros());
        assertEquals("Síndrome gripal", reg.getDiagnostico());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar registro de consulta não realizada")
    void deveLancarExcecaoParaConsultaNaoRealizada() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_SERRARIA);
        Prontuario p = new Prontuario(paciente);
        assertThrows(IllegalStateException.class,
            () -> p.adicionarRegistro(c, null, "Diagnóstico"));
    }

    @Test
    @DisplayName("Deve definir tipo sanguíneo válido")
    void deveDefinirTipoSanguineo() {
        Prontuario p = new Prontuario(paciente);
        p.definirTipoSanguineo("O+");
        assertEquals("O+", p.getTipoSanguineo());
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo sanguíneo inválido")
    void deveLancarExcecaoParaTipoSanguineoInvalido() {
        Prontuario p = new Prontuario(paciente);
        assertThrows(IllegalArgumentException.class, () -> p.definirTipoSanguineo("X+"));
    }

    @Test
    @DisplayName("Prontuário deve exigir paciente não nulo")
    void deveLancarExcecaoParaPacienteNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Prontuario(null));
    }

    // ── Use Cases ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("UseCase: Deve agendar consulta com sucesso")
    void useCaseDeveAgendarConsulta() {
        AgendarConsultaUseCase uc = new AgendarConsultaUseCase(agenda);
        Consulta c = uc.executar(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);

        assertNotNull(c);
        assertEquals(Consulta.StatusConsulta.AGENDADA, c.getStatus());
        assertEquals(1, agenda.todasAsConsultas().size());
    }

    @Test
    @DisplayName("UseCase: Deve cancelar consulta com motivo")
    void useCaseDeveCancelarConsulta() {
        AgendarConsultaUseCase agendar  = new AgendarConsultaUseCase(agenda);
        CancelarConsultaUseCase cancelar = new CancelarConsultaUseCase(agenda);

        Consulta c = agendar.executar(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_POÇO);
        cancelar.executar(c.getId(), "Paciente viajou para o interior de Alagoas");

        assertEquals(Consulta.StatusConsulta.CANCELADA, c.getStatus());
    }

    @Test
    @DisplayName("UseCase: Deve lançar exceção ao cancelar sem motivo")
    void useCaseLancarExcecaoAoCancelarSemMotivo() {
        AgendarConsultaUseCase agendar  = new AgendarConsultaUseCase(agenda);
        CancelarConsultaUseCase cancelar = new CancelarConsultaUseCase(agenda);

        Consulta c = agendar.executar(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);
        assertThrows(IllegalArgumentException.class,
            () -> cancelar.executar(c.getId(), ""));
    }

    @Test
    @DisplayName("UseCase: Deve realizar consulta e gerar registro no prontuário")
    void useCaseDeveRealizarConsultaEGerarRegistro() {
        AgendarConsultaUseCase agendar  = new AgendarConsultaUseCase(agenda);
        RealizarConsultaUseCase realizar = new RealizarConsultaUseCase(agenda);
        Prontuario prontuario = new Prontuario(paciente);

        Consulta c = agendar.executar(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_SERRARIA);

        Prontuario.RegistroClinico reg = realizar.executar(
            c.getId(),
            "Paciente relatou dor de garganta. Exame físico sem alterações graves.",
            prontuario,
            "Amoxicilina 500mg — 7 dias",
            "Faringite bacteriana"
        );

        assertEquals(Consulta.StatusConsulta.REALIZADA, c.getStatus());
        assertNotNull(reg);
        assertEquals("Faringite bacteriana", reg.getDiagnostico());
        assertEquals(1, prontuario.totalRegistros());
    }

    @Test
    @DisplayName("UseCase AgendarConsulta não deve aceitar AgendaRoot nulo")
    void useCaseNaoDeveAceitarAgendaNula() {
        assertThrows(IllegalArgumentException.class,
            () -> new AgendarConsultaUseCase(null));
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Consulta criarConsultaRealizada() {
        Consulta c = new Consulta(paciente, medico, horarioAmanha(9),
                Consulta.TipoConsulta.PRESENCIAL, Consulta.UnidadeMaceio.UBS_PAJUCARA);
        c.realizar("Atendimento realizado com sucesso. Sem intercorrências.");
        return c;
    }

    private Horario horarioAmanha(int hora) {
        LocalDateTime dt = LocalDateTime.now().plusDays(1)
                .withHour(hora).withMinute(0).withSecond(0).withNano(0);
        while (dt.getDayOfWeek().getValue() >= 6) dt = dt.plusDays(1);
        return new Horario(dt);
    }
}
