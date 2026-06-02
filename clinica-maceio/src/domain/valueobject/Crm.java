package br.com.clinicamaceio.domain.valueobject;

/**
 * Value Object imutável representando o CRM de um médico.
 * Formato aceito: número/UF — ex: 12345/AL
 *
 * Branch: feat/value-objects | Responsável: Pedro (Pedim00)
 */
public final class Crm {

    private final String numero;
    private final String uf;

    public Crm(String numero, String uf) {
        validarNumero(numero);
        validarUf(uf);
        this.numero = numero.trim();
        this.uf     = uf.trim().toUpperCase();
    }

    /** Construtor de conveniência aceitando formato "12345/AL" */
    public static Crm parse(String crm) {
        if (crm == null || !crm.contains("/")) {
            throw new IllegalArgumentException("CRM inválido: formato esperado NNNNN/UF — ex: 12345/AL");
        }
        String[] partes = crm.split("/");
        return new Crm(partes[0].trim(), partes[1].trim());
    }

    private void validarNumero(String numero) {
        if (numero == null || !numero.trim().matches("\\d{4,6}")) {
            throw new IllegalArgumentException("Número CRM inválido: deve conter entre 4 e 6 dígitos.");
        }
    }

    private void validarUf(String uf) {
        if (uf == null || !uf.trim().matches("[A-Za-z]{2}")) {
            throw new IllegalArgumentException("UF do CRM inválida: deve ser 2 letras — ex: AL");
        }
    }

    public String getNumero() { return numero; }
    public String getUf()     { return uf; }

    @Override
    public String toString() {
        return numero + "/" + uf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Crm)) return false;
        Crm outro = (Crm) o;
        return numero.equals(outro.numero) && uf.equals(outro.uf);
    }

    @Override
    public int hashCode() {
        return (numero + uf).hashCode();
    }
}
