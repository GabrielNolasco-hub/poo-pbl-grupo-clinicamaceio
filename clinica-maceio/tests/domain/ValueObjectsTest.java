package domain;

import br.com.clinicamaceio.domain.valueobject.Crm;
import br.com.clinicamaceio.domain.valueobject.Endereco;
import br.com.clinicamaceio.domain.valueobject.Horario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes TDD dos Value Objects CRM, Horario e Endereco.
 * Branch: feat/value-objects | Responsável: Pedro (Pedim00)
 */
@DisplayName("CRM, Horário e Endereço — Value Objects")
class ValueObjectsTest {

    // ── CRM ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar CRM válido com UF AL")
    void deveCriarCrmValidoAL() {
        Crm crm = Crm.parse("23456/AL");
        assertEquals("23456", crm.getNumero());
        assertEquals("AL", crm.getUf());
        assertEquals("23456/AL", crm.toString());
    }

    @Test
    @DisplayName("Deve lançar exceção para CRM sem barra")
    void deveLancarExcecaoParaCrmSemBarra() {
        assertThrows(IllegalArgumentException.class, () -> Crm.parse("23456AL"));
    }

    @Test
    @DisplayName("Deve lançar exceção para UF com mais de 2 letras")
    void deveLancarExcecaoParaUfInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Crm("23456", "ALA"));
    }

    @Test
    @DisplayName("Dois CRMs iguais devem ser equals")
    void doisCrmsIguaisDevemSerEquals() {
        assertEquals(Crm.parse("12345/AL"), Crm.parse("12345/AL"));
    }

    // ── Horário ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar horário válido no futuro em dia útil")
    void deveCriarHorarioValido() {
        LocalDateTime futuro = proximoDiaUtilAs(10);
        assertDoesNotThrow(() -> new Horario(futuro));
    }

    @Test
    @DisplayName("Deve lançar exceção para horário no passado")
    void deveLancarExcecaoParaHorarioPassado() {
        LocalDateTime passado = LocalDateTime.now().minusDays(1);
        assertThrows(IllegalArgumentException.class, () -> new Horario(passado));
    }

    @Test
    @DisplayName("Deve lançar exceção para horário antes das 7h (ClinicaMaceió)")
    void deveLancarExcecaoParaHorarioAntesDaAbertura() {
        LocalDateTime cedo = proximoDiaUtilAs(6);
        assertThrows(IllegalArgumentException.class, () -> new Horario(cedo));
    }

    @Test
    @DisplayName("Deve lançar exceção para horário após as 19h (ClinicaMaceió)")
    void deveLancarExcecaoParaHorarioAposEncerramento() {
        LocalDateTime tarde = proximoDiaUtilAs(20);
        assertThrows(IllegalArgumentException.class, () -> new Horario(tarde));
    }

    @Test
    @DisplayName("Deve detectar conflito de horário")
    void deveDetectarConflitoDeHorario() {
        LocalDateTime base = proximoDiaUtilAs(9);
        Horario h1 = new Horario(base);
        Horario h2 = new Horario(base.plusMinutes(15));
        assertTrue(h1.conflitaCom(h2, 30)); // 30 min de duração → conflito
    }

    @Test
    @DisplayName("Não deve detectar conflito quando horários são distantes")
    void naoDeveDetectarConflitoParaHorariosDistantes() {
        LocalDateTime base = proximoDiaUtilAs(9);
        Horario h1 = new Horario(base);
        Horario h2 = new Horario(base.plusMinutes(60));
        assertFalse(h1.conflitaCom(h2, 30));
    }

    // ── Endereço ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar endereço em Pajuçará, Maceió")
    void deveCriarEnderecoEmPajucara() {
        Endereco e = new Endereco("Rua Jangadeiros Alagoanos", "500",
                                  Endereco.BairroMaceio.PAJUCARA, "57030030");
        assertEquals("Maceió", e.getCidade());
        assertEquals("AL", e.getUf());
        assertEquals(Endereco.BairroMaceio.PAJUCARA, e.getBairro());
    }

    @Test
    @DisplayName("Deve lançar exceção para CEP inválido")
    void deveLancarExcecaoParaCepInvalido() {
        assertThrows(IllegalArgumentException.class,
            () -> new Endereco("Rua A", "1", Endereco.BairroMaceio.CENTRO, "1234"));
    }

    @Test
    @DisplayName("Deve lançar exceção para logradouro vazio")
    void deveLancarExcecaoParaLogradouroVazio() {
        assertThrows(IllegalArgumentException.class,
            () -> new Endereco("", "1", Endereco.BairroMaceio.CENTRO, "57000000"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private LocalDateTime proximoDiaUtilAs(int hora) {
        LocalDateTime dt = LocalDateTime.now().plusDays(1).withHour(hora).withMinute(0).withSecond(0).withNano(0);
        while (dt.getDayOfWeek().getValue() >= 6) dt = dt.plusDays(1);
        return dt;
    }
}
