package domain;

import br.com.clinicamaceio.domain.valueobject.Cpf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes TDD do Value Object Cpf.
 * Branch: feat/value-objects | Responsável: Pedro (Pedim00)
 */
@DisplayName("CPF — Value Object")
class CpfTest {

    @Test
    @DisplayName("Deve criar CPF válido formatado")
    void deveCriarCpfValido() {
        Cpf cpf = new Cpf("529.982.247-25");
        assertEquals("529.982.247-25", cpf.getFormatado());
        assertEquals("52998224725", cpf.getNumero());
    }

    @Test
    @DisplayName("Deve aceitar CPF sem formatação")
    void deveAceitarCpfSemFormatacao() {
        assertDoesNotThrow(() -> new Cpf("52998224725"));
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF com menos de 11 dígitos")
    void deveLancarExcecaoParaCpfCurto() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class, () -> new Cpf("123456789")
        );
        assertTrue(ex.getMessage().contains("11 dígitos"));
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF com todos os dígitos iguais")
    void deveLancarExcecaoParaCpfSequencial() {
        assertThrows(IllegalArgumentException.class, () -> new Cpf("111.111.111-11"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("000.000.000-00"));
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF com dígito verificador inválido")
    void deveLancarExcecaoParaDigitoVerificadorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Cpf("529.982.247-99"));
    }

    @Test
    @DisplayName("Dois CPFs iguais devem ser equals")
    void doisCpfsIguaisDevemSerEquals() {
        Cpf cpf1 = new Cpf("529.982.247-25");
        Cpf cpf2 = new Cpf("52998224725");
        assertEquals(cpf1, cpf2);
        assertEquals(cpf1.hashCode(), cpf2.hashCode());
    }

    @Test
    @DisplayName("CPFs diferentes não devem ser equals")
    void cpfsDiferentesNaoDevemSerEquals() {
        Cpf cpf1 = new Cpf("529.982.247-25");
        Cpf cpf2 = new Cpf("311.533.890-00");
        assertNotEquals(cpf1, cpf2);
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF nulo")
    void deveLancarExcecaoParaCpfNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Cpf(null));
    }
}
