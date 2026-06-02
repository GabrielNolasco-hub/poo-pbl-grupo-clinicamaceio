package br.com.clinicamaceio.domain.entity;

import br.com.clinicamaceio.domain.valueobject.Cpf;
import br.com.clinicamaceio.domain.valueobject.Endereco;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

/**
 * Entidade com identidade própria representando um paciente da ClinicaMaceió.
 * Encapsula todas as informações pessoais e clínicas do paciente.
 *
 * Branch: feat/domain-core | Responsável: Gabriel (GabrielNolasco-hub)
 */
public class Paciente {

    private final String id;
    private String nome;
    private final Cpf cpf;
    private LocalDate dataNascimento;
    private String email;
    private String telefone;
    private Endereco endereco;
    private String cartaoSus;
    private boolean ativo;

    public Paciente(String nome, Cpf cpf, LocalDate dataNascimento,
                    String email, String telefone, Endereco endereco) {
        validarNome(nome);
        validarEmail(email);
        validarTelefone(telefone);
        validarDataNascimento(dataNascimento);

        this.id              = UUID.randomUUID().toString();
        this.nome            = nome.trim();
        this.cpf             = cpf;
        this.dataNascimento  = dataNascimento;
        this.email           = email.trim().toLowerCase();
        this.telefone        = telefone.replaceAll("[^0-9]", "");
        this.endereco        = endereco;
        this.ativo           = true;
    }

    // ── Validações privadas ────────────────────────────────────────────────

    private void validarNome(String nome) {
        if (nome == null || nome.trim().length() < 3)
            throw new IllegalArgumentException("Nome do paciente deve ter ao menos 3 caracteres.");
    }

    private void validarEmail(String email) {
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$"))
            throw new IllegalArgumentException("E-mail inválido: " + email);
    }

    private void validarTelefone(String telefone) {
        String limpo = telefone == null ? "" : telefone.replaceAll("[^0-9]", "");
        if (limpo.length() < 10 || limpo.length() > 11)
            throw new IllegalArgumentException("Telefone inválido: informe DDD + número (10 ou 11 dígitos).");
    }

    private void validarDataNascimento(LocalDate data) {
        if (data == null || data.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Data de nascimento inválida.");
    }

    // ── Comportamentos de domínio ──────────────────────────────────────────

    public int calcularIdade() {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public boolean isMenorDeIdade() {
        return calcularIdade() < 18;
    }

    public void inativar() {
        this.ativo = false;
    }

    public void atualizarEndereco(Endereco novoEndereco) {
        if (novoEndereco == null)
            throw new IllegalArgumentException("Endereço não pode ser nulo.");
        this.endereco = novoEndereco;
    }

    public void atualizarEmail(String novoEmail) {
        validarEmail(novoEmail);
        this.email = novoEmail.trim().toLowerCase();
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public String getId()               { return id; }
    public String getNome()             { return nome; }
    public Cpf getCpf()                 { return cpf; }
    public LocalDate getDataNascimento(){ return dataNascimento; }
    public String getEmail()            { return email; }
    public String getTelefone()         { return telefone; }
    public Endereco getEndereco()       { return endereco; }
    public String getCartaoSus()        { return cartaoSus; }
    public boolean isAtivo()            { return ativo; }

    public void setCartaoSus(String cartaoSus) {
        if (cartaoSus != null && !cartaoSus.replaceAll("[^0-9]", "").matches("\\d{15}"))
            throw new IllegalArgumentException("Cartão SUS inválido: deve conter 15 dígitos.");
        this.cartaoSus = cartaoSus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paciente)) return false;
        return cpf.equals(((Paciente) o).cpf);
    }

    @Override
    public int hashCode() {
        return cpf.hashCode();
    }

    @Override
    public String toString() {
        return "Paciente{nome='" + nome + "', cpf=" + cpf + ", idade=" + calcularIdade() + "}";
    }
}
 
