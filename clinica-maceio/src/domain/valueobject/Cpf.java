package br.com.clinicamaceio.domain.valueobject;

/**
 * Value Object imutável representando um CPF válido.
 * Aplica validação de formato e dígitos verificadores.
 *
 * Branch: feat/value-objects | Responsável: Pedro (Pedim00)
 */
public final class Cpf {

    private final String numero;

    public Cpf(String numero) {
        String limpo = sanitizar(numero);
        validar(limpo);
        this.numero = limpo;
    }

    private String sanitizar(String valor) {
        if (valor == null) return "";
        return valor.replaceAll("[^0-9]", "");
    }

    private void validar(String cpf) {
        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF inválido: deve conter 11 dígitos.");
        }
        if (cpf.chars().distinct().count() == 1) {
            throw new IllegalArgumentException("CPF inválido: todos os dígitos são iguais.");
        }
        if (!verificarDigitos(cpf)) {
            throw new IllegalArgumentException("CPF inválido: dígitos verificadores incorretos.");
        }
    }

    private boolean verificarDigitos(String cpf) {
        int soma = 0;
        for (int i = 0; i < 9; i++) soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        int primeiro = (soma * 10) % 11;
        if (primeiro == 10) primeiro = 0;
        if (primeiro != Character.getNumericValue(cpf.charAt(9))) return false;

        soma = 0;
        for (int i = 0; i < 10; i++) soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        int segundo = (soma * 10) % 11;
        if (segundo == 10) segundo = 0;
        return segundo == Character.getNumericValue(cpf.charAt(10));
    }

    public String getNumero() {
        return numero;
    }

    /** Retorna formatado: 000.000.000-00 */
    public String getFormatado() {
        return numero.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cpf)) return false;
        return numero.equals(((Cpf) o).numero);
    }

    @Override
    public int hashCode() {
        return numero.hashCode();
    }

    @Override
    public String toString() {
        return getFormatado();
    }
}
