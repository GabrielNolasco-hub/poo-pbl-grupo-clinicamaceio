package br.com.clinicamaceio.domain.entity;

import br.com.clinicamaceio.domain.valueobject.Crm;

import java.util.UUID;

/**
 * Entidade com identidade própria representando um médico da ClinicaMaceió.
 * As especialidades refletem as mais comuns em clínicas populares de Maceió/AL.
 *
 * Branch: feat/domain-core | Responsável: Gabriel (GabrielNolasco-hub)
 */
public class Medico {

    public enum Especialidade {
        CLINICO_GERAL,
        PEDIATRIA,
        GINECOLOGIA,
        CARDIOLOGIA,
        DERMATOLOGIA,
        ORTOPEDIA,
        PSIQUIATRIA,
        NUTRICAO,
        TELEMEDICINA_GERAL
    }

    private final String id;
    private String nome;
    private final Crm crm;
    private Especialidade especialidade;
    private String email;
    private boolean disponivel;

    public Medico(String nome, Crm crm, Especialidade especialidade, String email) {
        validarNome(nome);
        validarEmail(email);
        if (especialidade == null)
            throw new IllegalArgumentException("Especialidade não pode ser nula.");

        this.id            = UUID.randomUUID().toString();
        this.nome          = nome.trim();
        this.crm           = crm;
        this.especialidade = especialidade;
        this.email         = email.trim().toLowerCase();
        this.disponivel    = true;
    }

    // ── Validações privadas ────────────────────────────────────────────────

    private void validarNome(String nome) {
        if (nome == null || nome.trim().length() < 3)
            throw new IllegalArgumentException("Nome do médico deve ter ao menos 3 caracteres.");
    }

    private void validarEmail(String email) {
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$"))
            throw new IllegalArgumentException("E-mail do médico inválido: " + email);
    }

    // ── Comportamentos de domínio ──────────────────────────────────────────

    public boolean atendeEspecialidade(Especialidade especialidade) {
        return this.especialidade == especialidade;
    }

    public void tornarIndisponivel() {
        this.disponivel = false;
    }

    public void tornarDisponivel() {
        this.disponivel = true;
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public String getId()                   { return id; }
    public String getNome()                 { return nome; }
    public Crm getCrm()                     { return crm; }
    public Especialidade getEspecialidade() { return especialidade; }
    public String getEmail()                { return email; }
    public boolean isDisponivel()           { return disponivel; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medico)) return false;
        return crm.equals(((Medico) o).crm);
    }

    @Override
    public int hashCode() {
        return crm.hashCode();
    }

    @Override
    public String toString() {
        return "Dr(a). " + nome + " — " + especialidade + " | CRM: " + crm;
    }
}
