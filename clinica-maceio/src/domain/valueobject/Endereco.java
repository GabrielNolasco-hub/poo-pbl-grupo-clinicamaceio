package br.com.clinicamaceio.domain.valueobject;

/**
 * Value Object imutável representando um endereço em Maceió/AL.
 * Contém bairros reais de Maceió para contextualização local.
 *
 * Branch: feat/value-objects | Responsável: Pedro (Pedim00)
 */
public final class Endereco {

    /** Bairros reais de Maceió utilizados pelas clínicas populares */
    public enum BairroMaceio {
        PAJUCARA, PONTA_VERDE, JATIUCA, FAROL, CENTRO,
        POÇO, SERRARIA, BENEDITO_BENTES, TABULEIRO, CLIMA_BOM,
        GRUTA_DE_LOURDES, FERNAO_VELHO, SANTOS_DUMONT, TRAPICHE_DA_BARRA
    }

    private final String logradouro;
    private final String numero;
    private final BairroMaceio bairro;
    private final String cidade;
    private final String uf;
    private final String cep;

    public Endereco(String logradouro, String numero, BairroMaceio bairro, String cep) {
        if (logradouro == null || logradouro.isBlank())
            throw new IllegalArgumentException("Logradouro não pode ser vazio.");
        if (bairro == null)
            throw new IllegalArgumentException("Bairro não pode ser nulo.");
        if (cep == null || !cep.replaceAll("[^0-9]", "").matches("\\d{8}"))
            throw new IllegalArgumentException("CEP inválido. Formato esperado: 00000-000");

        this.logradouro = logradouro.trim();
        this.numero     = (numero == null || numero.isBlank()) ? "S/N" : numero.trim();
        this.bairro     = bairro;
        this.cidade     = "Maceió";
        this.uf         = "AL";
        this.cep        = cep.replaceAll("[^0-9]", "")
                             .replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }

    public String getLogradouro() { return logradouro; }
    public String getNumero()     { return numero; }
    public BairroMaceio getBairro() { return bairro; }
    public String getCidade()     { return cidade; }
    public String getUf()         { return uf; }
    public String getCep()        { return cep; }

    @Override
    public String toString() {
        return logradouro + ", " + numero + " — " +
               bairro.name().replace("_", " ") + ", " +
               cidade + "/" + uf + " — CEP: " + cep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endereco)) return false;
        Endereco e = (Endereco) o;
        return logradouro.equals(e.logradouro) &&
               numero.equals(e.numero) &&
               bairro == e.bairro &&
               cep.equals(e.cep);
    }

    @Override
    public int hashCode() {
        return (logradouro + numero + bairro + cep).hashCode();
    }
}
