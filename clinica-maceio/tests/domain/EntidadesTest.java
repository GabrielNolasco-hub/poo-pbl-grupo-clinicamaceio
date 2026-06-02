package domain;

import br.com.clinicamaceio.domain.entity.Medico;
import br.com.clinicamaceio.domain.entity.Paciente;
import br.com.clinicamaceio.domain.valueobject.Cpf;
import br.com.clinicamaceio.domain.valueobject.Crm;
import br.com.clinicamaceio.domain.valueobject.Endereco;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes TDD das entidades Paciente e Medico.
 * Branch: feat/domain-core | Responsável: Gabriel (GabrielNolasco-hub)
 */
@DisplayName("Paciente e Médico — Entidades do Domínio")
class EntidadesTest {

    private Endereco endMaceio;

    @BeforeEach
    void setUp() {
        endMaceio = new Endereco("Av. Fernandes Lima", "1200",
                                  Endereco.BairroMaceio.POÇO, "57055000");
    }

    // ── Paciente ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar paciente válido com dados de Maceió")
    void deveCriarPacienteValido() {
        Paciente p = pacienteValido();
        assertEquals("Maria das Graças Silva", p.getNome());
        assertTrue(p.isAtivo());
        assertNotNull(p.getId());
    }

    @Test
    @DisplayName("Deve calcular idade corretamente")
    void deveCalcularIdadeCorretamente() {
        Paciente p = new Paciente("Carlos Ferreira", new Cpf("529.982.247-25"),
                LocalDate.now().minusYears(30),
                "carlos@email.com", "82988887777", endMaceio);
        assertEquals(30, p.calcularIdade());
    }

    @Test
    @DisplayName("Deve identificar paciente menor de idade")
    void deveIdentificarMenorDeIdade() {
        Paciente crianca = new Paciente("João Menor", new Cpf("529.982.247-25"),
                LocalDate.now().minusYears(10),
                "joao@email.com", "82977776666", endMaceio);
        assertTrue(crianca.isMenorDeIdade());
    }

    @Test
    @DisplayName("Deve inativar paciente corretamente")
    void deveInativarPaciente() {
        Paciente p = pacienteValido();
        p.inativar();
        assertFalse(p.isAtivo());
    }

    @Test
    @DisplayName("Deve lançar exceção para nome com menos de 3 caracteres")
    void deveLancarExcecaoParaNomeCurto() {
        assertThrows(IllegalArgumentException.class, () ->
            new Paciente("Jo", new Cpf("529.982.247-25"),
                    LocalDate.of(1990, 1, 1), "jo@email.com", "82988887777", endMaceio)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para e-mail inválido")
    void deveLancarExcecaoParaEmailInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
            new Paciente("Ana Lima", new Cpf("529.982.247-25"),
                    LocalDate.of(1990, 1, 1), "emailinvalido", "82988887777", endMaceio)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção para telefone com formato incorreto")
    void deveLancarExcecaoParaTelefoneInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
            new Paciente("Pedro Costa", new Cpf("529.982.247-25"),
                    LocalDate.of(1990, 1, 1), "pedro@email.com", "123", endMaceio)
        );
    }

    @Test
    @DisplayName("Deve validar Cartão SUS com 15 dígitos")
    void deveValidarCartaoSus() {
        Paciente p = pacienteValido();
        assertDoesNotThrow(() -> p.setCartaoSus("123456789012345"));
    }

    @Test
    @DisplayName("Deve lançar exceção para Cartão SUS inválido")
    void deveLancarExcecaoParaCartaoSusInvalido() {
        Paciente p = pacienteValido();
        assertThrows(IllegalArgumentException.class, () -> p.setCartaoSus("123"));
    }

    @Test
    @DisplayName("Dois pacientes com mesmo CPF devem ser equals")
    void doisPacientesComMesmoCpfDevemSerEquals() {
        Cpf cpf = new Cpf("529.982.247-25");
        Paciente p1 = new Paciente("Maria 1", cpf, LocalDate.of(1985, 1, 1),
                "m1@email.com", "82988887777", endMaceio);
        Paciente p2 = new Paciente("Maria 2", cpf, LocalDate.of(1990, 5, 5),
                "m2@email.com", "82988887778", endMaceio);
        assertEquals(p1, p2);
    }

    // ── Médico ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar médico clínico geral da ClinicaMaceió")
    void deveCriarMedicoValido() {
        Medico m = medicoValido();
        assertEquals("Dr. Carlos Alberto Souza", m.getNome());
        assertEquals(Medico.Especialidade.CLINICO_GERAL, m.getEspecialidade());
        assertTrue(m.isDisponivel());
    }

    @Test
    @DisplayName("Deve verificar especialidade corretamente")
    void deveVerificarEspecialidade() {
        Medico m = medicoValido();
        assertTrue(m.atendeEspecialidade(Medico.Especialidade.CLINICO_GERAL));
        assertFalse(m.atendeEspecialidade(Medico.Especialidade.PEDIATRIA));
    }

    @Test
    @DisplayName("Deve tornar médico indisponível e disponível novamente")
    void deveAlterarDisponibilidadeMedico() {
        Medico m = medicoValido();
        m.tornarIndisponivel();
        assertFalse(m.isDisponivel());
        m.tornarDisponivel();
        assertTrue(m.isDisponivel());
    }

    @Test
    @DisplayName("Dois médicos com mesmo CRM devem ser equals")
    void doisMedicosComMesmoCrmDevemSerEquals() {
        Crm crm = Crm.parse("23456/AL");
        Medico m1 = new Medico("Dr. A", crm, Medico.Especialidade.CLINICO_GERAL, "a@email.com");
        Medico m2 = new Medico("Dr. B", crm, Medico.Especialidade.PEDIATRIA, "b@email.com");
        assertEquals(m1, m2);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Paciente pacienteValido() {
        return new Paciente(
            "Maria das Graças Silva",
            new Cpf("529.982.247-25"),
            LocalDate.of(1985, 3, 15),
            "maria.gracas@email.com",
            "82988887777",
            endMaceio
        );
    }

    private Medico medicoValido() {
        return new Medico(
            "Dr. Carlos Alberto Souza",
            Crm.parse("23456/AL"),
            Medico.Especialidade.CLINICO_GERAL,
            "carlos.souza@clinicamaceio.com"
        );
    }
}
