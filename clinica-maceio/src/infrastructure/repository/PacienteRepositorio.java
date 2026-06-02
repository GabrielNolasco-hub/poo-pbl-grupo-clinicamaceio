package br.com.clinicamaceio.infrastructure.repository;

import br.com.clinicamaceio.domain.entity.Paciente;
import br.com.clinicamaceio.domain.valueobject.Cpf;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório para Paciente.
 * A implementação real com H2 fica em PacienteRepositorioH2.
 *
 * Branch: feat/infrastructure-presentation | Responsável: Eric (EricPessoa-git)
 */
public interface PacienteRepositorio {
    void salvar(Paciente paciente);
    Optional<Paciente> buscarPorId(String id);
    Optional<Paciente> buscarPorCpf(Cpf cpf);
    List<Paciente> listarTodos();
    void atualizar(Paciente paciente);
    void remover(String id);
}
