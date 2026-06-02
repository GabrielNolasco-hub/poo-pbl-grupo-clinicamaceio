package br.com.clinicamaceio.domain.valueobject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Value Object imutável representando um horário de consulta.
 * Garante que o agendamento seja sempre no futuro e em horário comercial.
 * Horário comercial ClinicaMaceió: 07h–19h de segunda a sexta.
 *
 * Branch: feat/value-objects | Responsável: Pedro (Pedim00)
 */
public final class Horario {

    private static final int HORA_ABERTURA  = 7;
    private static final int HORA_FECHAMENTO = 19;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final LocalDateTime dataHora;

    public Horario(LocalDateTime dataHora) {
        validar(dataHora);
        this.dataHora = dataHora;
    }

    private void validar(LocalDateTime dt) {
        if (dt == null) {
            throw new IllegalArgumentException("Horário não pode ser nulo.");
        }
        if (dt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Horário inválido: não é possível agendar no passado.");
        }
        int diaSemana = dt.getDayOfWeek().getValue(); // 1=seg … 7=dom
        if (diaSemana == 6 || diaSemana == 7) {
            throw new IllegalArgumentException(
                "Horário inválido: ClinicaMaceió não atende aos finais de semana.");
        }
        int hora = dt.getHour();
        if (hora < HORA_ABERTURA || hora >= HORA_FECHAMENTO) {
            throw new IllegalArgumentException(
                "Horário inválido: atendimento apenas entre " +
                HORA_ABERTURA + "h e " + HORA_FECHAMENTO + "h.");
        }
    }

    public LocalDateTime getDataHora() { return dataHora; }

    public boolean conflitaCom(Horario outro, int duracaoMinutos) {
        LocalDateTime fim = this.dataHora.plusMinutes(duracaoMinutos);
        return outro.dataHora.isBefore(fim) && outro.dataHora.isAfter(this.dataHora.minusMinutes(1));
    }

    @Override
    public String toString() {
        return dataHora.format(FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Horario)) return false;
        return dataHora.equals(((Horario) o).dataHora);
    }

    @Override
    public int hashCode() {
        return dataHora.hashCode();
    }
}
