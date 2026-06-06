# 🏥 ClinicaMaceió — Sistema de Agendamento e Telemedicina

> **Projeto Integrador — Orientação a Objetos**  
> Tema 5: Sistema de Agendamento e Telemedicina para Clínicas Populares  
> Contextualizado para **Maceió/AL**

---

## 👥 Integrantes e Branches

| Integrante | GitHub | Branch | Responsabilidade |
|---|---|---|---|
| Gabriel Nolasco | [@GabrielNolasco-hub](https://github.com/GabrielNolasco-hub) | `feat/domain-core` | Entidades: Paciente, Médico, Consulta |
| Pedro | [@Pedim00](https://github.com/Pedim00) | `feat/value-objects` | Value Objects: CPF, CRM, Horario, Endereço |
| Guilherme Pereira | [@gpereirazm](https://github.com/gpereirazm) | `feat/aggregates` | Aggregates: AgendaRoot, Prontuário |
| Alexandre (Deco) | [@AlexandreAlbuquerque-hub](https://github.com/AlexandreAlbuquerque-hub) | `feat/application` | Use Cases: Agendar, Cancelar, Realizar Consulta |
| Eric Pessoa | [@EricPessoa-git](https://github.com/EricPessoa-git) | `feat/infrastructure-presentation` | DB H2 + Interface Swing |

---

## 🏗️ Arquitetura e Estrutura

```
clinica-maceio/
├── .github/
│   └── workflows/
│       └── ci.yml                    ← Pipeline CI/CD GitHub Actions
├── src/
│   ├── domain/                       ← Núcleo DDD (sem dependências externas)
│   │   ├── entity/
│   │   │   ├── Paciente.java
│   │   │   ├── Medico.java
│   │   │   └── Consulta.java
│   │   ├── valueobject/
│   │   │   ├── Cpf.java
│   │   │   ├── Crm.java
│   │   │   ├── Horario.java
│   │   │   └── Endereco.java
│   │   └── aggregate/
│   │       ├── AgendaRoot.java       ← Aggregate Root principal
│   │       └── Prontuario.java
│   ├── application/
│   │   └── usecase/
│   │       ├── AgendarConsultaUseCase.java
│   │       ├── CancelarConsultaUseCase.java
│   │       └── RealizarConsultaUseCase.java
│   ├── infrastructure/
│   │   ├── repository/
│   │   │   └── PacienteRepositorio.java
│   │   └── persistence/
│   │       └── DatabaseManager.java  ← H2 in-memory (bônus)
│   └── presentation/
│       └── view/
│           └── TelaPrincipal.java    ← UI Swing (bônus)
├── tests/
│   └── domain/
│       ├── CpfTest.java
│       ├── ValueObjectsTest.java
│       ├── EntidadesTest.java
│       ├── AgendaTest.java
│       └── ProntuarioEUseCasesTest.java
├── project-meta.json
└── README.md
```

---

## ⚙️ Como Rodar

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Compilar e testar
```bash
mvn clean test
```

### Rodar a interface gráfica (bônus)
```bash
mvn compile
mvn exec:java -Dexec.mainClass="br.com.clinicamaceio.presentation.view.TelaPrincipal"
```

---


## 🗺️ Contexto Local — Maceió/AL

O sistema foi contextualizado para a realidade de Maceió:

- **Unidades de Atendimento:** UBS Pajuçará, UBS Poço, UBS Serraria, UBS Benedito Bentes, UBS Clima Bom
- **Bairros suportados:** Pajuçará, Ponta Verde, Jatiúca, Farol, Centro, Poço, Serraria, Benedito Bentes, Tabuleiro, Clima Bom, entre outros
- **Horário comercial:** 07h–19h, segunda a sexta (padrão UBS Maceió)
- **CRM regional:** formato com sufixo `/AL`
