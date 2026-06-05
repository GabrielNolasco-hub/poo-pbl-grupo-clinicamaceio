package br.com.clinicamaceio.infrastructure.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gerenciador de conexão com banco H2 em memória.
 * Inicializa o schema automaticamente ao ser criado.
 *
 * Branch: feat/infrastructure-presentation | Responsável: Eric (EricPessoa-git)
 */
public class DatabaseManager {

    private static final String URL      = "jdbc:h2:mem:clinicamaceio;DB_CLOSE_DELAY=-1";
    private static final String USUARIO  = "sa";
    private static final String SENHA    = "";

    private static DatabaseManager instancia;
    private final Connection conexao;

    private DatabaseManager() throws SQLException {
        this.conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
        inicializarSchema();
    }

    public static DatabaseManager getInstance() throws SQLException {
        if (instancia == null || instancia.conexao.isClosed()) {
            instancia = new DatabaseManager();
        }
        return instancia;
    }

    public Connection getConexao() {
        return conexao;
    }

    private void inicializarSchema() throws SQLException {
        try (Statement stmt = conexao.createStatement()) {
            // Tabela de Pacientes
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS pacientes (
                    id              VARCHAR(36)  PRIMARY KEY,
                    nome            VARCHAR(120) NOT NULL,
                    cpf             VARCHAR(11)  NOT NULL UNIQUE,
                    data_nascimento DATE         NOT NULL,
                    email           VARCHAR(120) NOT NULL,
                    telefone        VARCHAR(11)  NOT NULL,
                    cartao_sus      VARCHAR(15),
                    ativo           BOOLEAN      NOT NULL DEFAULT TRUE
                )
            """);

            // Tabela de Médicos
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS medicos (
                    id            VARCHAR(36)  PRIMARY KEY,
                    nome          VARCHAR(120) NOT NULL,
                    crm           VARCHAR(10)  NOT NULL UNIQUE,
                    especialidade VARCHAR(50)  NOT NULL,
                    email         VARCHAR(120) NOT NULL,
                    disponivel    BOOLEAN      NOT NULL DEFAULT TRUE
                )
            """);

            // Tabela de Consultas
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS consultas (
                    id                   VARCHAR(36)  PRIMARY KEY,
                    paciente_id          VARCHAR(36)  NOT NULL,
                    medico_id            VARCHAR(36)  NOT NULL,
                    data_hora            TIMESTAMP    NOT NULL,
                    tipo                 VARCHAR(20)  NOT NULL,
                    unidade              VARCHAR(50)  NOT NULL,
                    status               VARCHAR(20)  NOT NULL DEFAULT 'AGENDADA',
                    link_telemedicina    VARCHAR(255),
                    motivo_cancelamento  VARCHAR(255),
                    observacoes          TEXT,
                    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
                    FOREIGN KEY (medico_id)   REFERENCES medicos(id)
                )
            """);

            // Tabela de Prontuários
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS prontuarios (
                    id                  VARCHAR(36)  PRIMARY KEY,
                    paciente_id         VARCHAR(36)  NOT NULL UNIQUE,
                    alergias            TEXT,
                    historico_familiar  TEXT,
                    tipo_sanguineo      VARCHAR(3),
                    FOREIGN KEY (paciente_id) REFERENCES pacientes(id)
                )
            """);

            // Tabela de Registros Clínicos
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS registros_clinicos (
                    id             VARCHAR(36)  PRIMARY KEY,
                    prontuario_id  VARCHAR(36)  NOT NULL,
                    data_registro  TIMESTAMP    NOT NULL,
                    medico_nome    VARCHAR(120) NOT NULL,
                    especialidade  VARCHAR(50)  NOT NULL,
                    descricao      TEXT         NOT NULL,
                    prescricao     TEXT,
                    diagnostico    TEXT,
                    FOREIGN KEY (prontuario_id) REFERENCES prontuarios(id)
                )
            """);
        }
    }

    public void fecharConexao() throws SQLException {
        if (conexao != null && !conexao.isClosed()) {
            conexao.close();
        }
    }
}
 
