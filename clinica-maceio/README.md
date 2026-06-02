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

## 🌊 Fluxo de Trabalho Git (IMPORTANTE — leia antes de codar)

### 1. Clone o repositório
```bash
git clone https://github.com/GabrielNolasco-hub/poo-pbl-grupo-clinicamaceio.git
cd poo-pbl-grupo-clinicamaceio
```

### 2. Cada membro cria/troca para sua branch
```bash
# Gabriel
git checkout -b feat/domain-core

# Pedro
git checkout -b feat/value-objects

# Guilherme
git checkout -b feat/aggregates

# Deco
git checkout -b feat/application

# Eric
git checkout -b feat/infrastructure-presentation
```

### 3. Suba suas alterações
```bash
git add .
git commit -m "feat(domain): adiciona entidade Paciente com validações de CPF"
git push origin feat/nome-da-sua-branch
```

### 4. Abra um Pull Request para `main`
- Vá no GitHub → Pull Requests → New Pull Request
- Base: `main` | Compare: sua branch
- Peça review para pelo menos 1 colega antes de mergear

### ⚠️ Regra de Ouro
> Nunca commite direto na `main`. Sempre via Pull Request com aprovação.

---

## 🧪 TDD — Ciclo Red-Green-Refactor

O projeto segue TDD estrito. A ordem de desenvolvimento **de cada branch** deve ser:

1. **🔴 RED** — Escreva o teste em `tests/domain/` → veja o CI falhar
2. **🟢 GREEN** — Implemente o mínimo em `src/domain/` → veja o CI passar
3. **🔵 REFACTOR** — Melhore o código sem quebrar os testes

---

## 📋 Tarefas por Membro

### Gabriel — `feat/domain-core`
- [x] `Paciente.java` — entidade com validações (nome, email, telefone, CPF, data)
- [x] `Medico.java` — entidade com especialidades reais (Maceió)
- [x] `Consulta.java` — entidade com estados e unidades UBS reais
- [x] `EntidadesTest.java` — testes TDD das entidades

### Pedro — `feat/value-objects`
- [x] `Cpf.java` — validação com dígitos verificadores
- [x] `Crm.java` — formato NNNNN/AL
- [x] `Horario.java` — horário comercial ClinicaMaceió (07h–19h, seg–sex)
- [x] `Endereco.java` — bairros reais de Maceió/AL
- [x] `CpfTest.java` e `ValueObjectsTest.java` — testes TDD

### Guilherme — `feat/aggregates`
- [x] `AgendaRoot.java` — aggregate root com controle de conflitos
- [x] `Prontuario.java` — histórico clínico do paciente
- [x] `AgendaTest.java` e parte de `ProntuarioEUseCasesTest.java`

### Deco — `feat/application`
- [x] `AgendarConsultaUseCase.java`
- [x] `CancelarConsultaUseCase.java`
- [x] `RealizarConsultaUseCase.java`
- [x] Testes dos use cases em `ProntuarioEUseCasesTest.java`

### Eric — `feat/infrastructure-presentation`
- [x] `PacienteRepositorio.java` — interface do repositório
- [x] `DatabaseManager.java` — H2 in-memory com schema completo
- [x] `TelaPrincipal.java` — UI Swing integrada ao domínio

---

## 🔄 CI/CD — GitHub Actions

A cada push ou Pull Request para `main`, a pipeline:
1. Faz checkout do código
2. Configura JDK 17
3. Executa `mvn clean compile`
4. Executa `mvn test`
5. Publica relatório dos testes

Se qualquer teste falhar → ❌ o merge é bloqueado.

---

## 🗺️ Contexto Local — Maceió/AL

O sistema foi contextualizado para a realidade de Maceió:

- **Unidades de Atendimento:** UBS Pajuçará, UBS Poço, UBS Serraria, UBS Benedito Bentes, UBS Clima Bom
- **Bairros suportados:** Pajuçará, Ponta Verde, Jatiúca, Farol, Centro, Poço, Serraria, Benedito Bentes, Tabuleiro, Clima Bom, entre outros
- **Horário comercial:** 07h–19h, segunda a sexta (padrão UBS Maceió)
- **CRM regional:** formato com sufixo `/AL`
