# Mapeamento Objeto-Relacional

Mapeamento das classes do pacote `com.agenda.model` para as tabelas do banco PostgreSQL (`init.sql`).

## Legenda

- **PK**: chave primária
- **FK**: chave estrangeira
- **UK**: valor único (UNIQUE)
- Os atributos em camelCase viram colunas em snake_case (ex.: `dataAgendada` → `data_agendada`).

## Resumo em notação textual

```
Usuario (id, nome, cpf, telefone, email, senha)
    PK: id
    UK: email

Administrador (id)
    PK: id
    FK: id → Usuario(id)

Profissional (id_profissional, percentual_comissao, status_profissional, ativo)
    PK: id_profissional
    FK: id_profissional → Usuario(id)

Especialidade (id_especialidade, nome, descricao)
    PK: id_especialidade

Profissional_Especialidade (id_profissional, id_especialidade)
    PK: (id_profissional, id_especialidade)
    FK: id_profissional  → Profissional(id_profissional)
    FK: id_especialidade → Especialidade(id_especialidade)

Agenda_Profissional (id_agenda, id_profissional, data, horario_inicio, horario_fim, status)
    PK: id_agenda
    FK: id_profissional → Profissional(id_profissional)

Cliente (id, nome, data_cadastro, observacoes)
    PK: id

Servico (id, nome, descricao, duracao_minutos, valor_padrao)
    PK: id

Agendamento (id, id_cliente, id_profissional, data_agendada, horario_inicio, horario_fim,
             status, motivo_cancelamento, observacoes, data_realizacao,
             horario_inicio_realizacao, horario_fim_realizacao)
    PK: id
    FK: id_cliente      → Cliente(id)
    FK: id_profissional → Profissional(id_profissional)

Item_Agendamento (id, id_agendamento, id_servico, duracao_realizada, valor_cobrado)
    PK: id
    FK: id_agendamento → Agendamento(id)
    FK: id_servico     → Servico(id)

Repasse_Comissao (id_repasse, id_agendamento, valor_pago_cliente, percentual_aplicado,
                  valor_comissao, data_calculo, data_repasse, status_pagamento)
    PK: id_repasse
    FK: id_agendamento → Agendamento(id)
    UK: id_agendamento
```

## Mapeamento de tipos

| Tipo no diagrama | Tipo em Java | Tipo no PostgreSQL |
|---|---|---|
| long (identificador) | `long` | `bigserial` (gerado automaticamente) |
| long (chave estrangeira) | `long` / objeto associado | `int8` |
| String | `String` | `varchar(n)` |
| int | `int` | `int4` |
| double / float (valores) | `double` / `float` | `numeric(10,2)` |
| double / float (percentuais) | `double` / `float` | `numeric(5,2)` |
| boolean | `boolean` | `bool` |
| Date | `LocalDate` | `date` |
| Time | `LocalTime` | `time` |
| DateTime | `LocalDateTime` | `timestamp` |
| StatusAgendamento (enum) | `StatusAgendamento` | `int4` (código do enum) |

## Mapeamento por classe

### Usuario → tabela `usuario`

| Atributo | Coluna | Tipo | Restrição |
|---|---|---|---|
| id | id | bigserial | PK |
| nome | nome | varchar(60) | NOT NULL |
| cpf | cpf | varchar(14) | NULL |
| telefone | telefone | varchar(15) | NOT NULL |
| email | email | varchar(60) | NOT NULL, UK |
| senha | senha | varchar(255) | NOT NULL |

### Administrador → tabela `administrador`

| Atributo | Coluna | Tipo | Restrição |
|---|---|---|---|
| id (herdado de Usuario) | id | int8 | PK, FK → usuario(id) |

### Profissional → tabela `profissional`

| Atributo | Coluna | Tipo | Restrição |
|---|---|---|---|
| idProfissional | id_profissional | int8 | PK, FK → usuario(id) |
| percentualComissao | percentual_comissao | numeric(5,2) | NOT NULL, padrão 0 |
| statusProfissional | status_profissional | varchar(30) | NOT NULL, padrão 'DISPONIVEL' |
| ativo | ativo | bool | NOT NULL, padrão true |
| especialidades | tabela `profissional_especialidade` | — | associação N:N |
| agendas | tabela `agenda_profissional` (coluna `id_profissional`) | — | associação 1:N |
| agendamentos | tabela `agendamento` (coluna `id_profissional`) | — | associação 1:N |

Os atributos herdados de Usuario (`nome`, `cpf`, `telefone`, `email`, `senha`) ficam na tabela `usuario`. O valor de `id` (herdado) é igual ao de `idProfissional`.

### Especialidade → tabela `especialidade`

| Atributo | Coluna | Tipo | Restrição |
|---|---|---|---|
| idEspecialidade | id_especialidade | bigserial | PK |
| nome | nome | varchar(50) | NOT NULL |
| descricao | descricao | varchar(255) | NULL |

### AgendaProfissional → tabela `agenda_profissional`

| Atributo | Coluna | Tipo | Restrição |
|---|---|---|---|
| idAgenda | id_agenda | bigserial | PK |
| (Profissional dono da agenda) | id_profissional | int8 | FK → profissional(id_profissional) |
| data | data | date | NOT NULL |
| horarioInicio | horario_inicio | time | NOT NULL |
| horarioFim | horario_fim | time | NOT NULL |
| status | status | varchar(30) | NOT NULL, padrão 'DISPONIVEL' |

### Cliente → tabela `cliente`

| Atributo | Coluna | Tipo | Restrição |
|---|---|---|---|
| id | id | bigserial | PK |
| nome | nome | varchar(60) | NOT NULL |
| dataCadastro | data_cadastro | date | NOT NULL, padrão data atual |
| observacoes | observacoes | varchar(255) | NULL |

### Servico → tabela `servico`

| Atributo | Coluna | Tipo | Restrição |
|---|---|---|---|
| id | id | bigserial | PK |
| nome | nome | varchar(60) | NOT NULL |
| descricao | descricao | varchar(255) | NULL |
| duracaoMinutos | duracao_minutos | int4 | NOT NULL |
| valorPadrao | valor_padrao | numeric(10,2) | NOT NULL |

### Agendamento → tabela `agendamento`

| Atributo | Coluna | Tipo | Restrição |
|---|---|---|---|
| id | id | bigserial | PK |
| cliente | id_cliente | int8 | NOT NULL, FK → cliente(id) |
| profissional | id_profissional | int8 | NOT NULL, FK → profissional(id_profissional) |
| dataAgendada | data_agendada | date | NOT NULL |
| horarioInicio | horario_inicio | time | NOT NULL |
| horarioFim | horario_fim | time | NOT NULL |
| status | status | int4 | NOT NULL, padrão 1 (AGENDADO) |
| motivoCancelamento | motivo_cancelamento | varchar(255) | NULL |
| observacoes | observacoes | varchar(255) | NULL |
| dataRealizacao | data_realizacao | date | NULL |
| horarioInicioRealizacao | horario_inicio_realizacao | time | NULL |
| horarioFimRealizacao | horario_fim_realizacao | time | NULL |
| itens | tabela `item_agendamento` (coluna `id_agendamento`) | — | composição 1:N |
| repasseComissao | tabela `repasse_comissao` (coluna `id_agendamento`) | — | associação 1:0..1 |

### ItemAgendamento → tabela `item_agendamento`

| Atributo | Coluna | Tipo | Restrição |
|---|---|---|---|
| id | id | bigserial | PK |
| (Agendamento dono do item) | id_agendamento | int8 | NOT NULL, FK → agendamento(id) |
| servico | id_servico | int8 | NOT NULL, FK → servico(id) |
| duracaoRealizada | duracao_realizada | int4 | NULL |
| valorCobrado | valor_cobrado | numeric(10,2) | NOT NULL |

### RepasseComissao → tabela `repasse_comissao`

| Atributo | Coluna | Tipo | Restrição |
|---|---|---|---|
| idRepasse | id_repasse | bigserial | PK |
| (Agendamento que gerou o repasse) | id_agendamento | int8 | NOT NULL, UK, FK → agendamento(id) |
| valorPagoCliente | valor_pago_cliente | numeric(10,2) | NOT NULL |
| percentualAplicado | percentual_aplicado | numeric(5,2) | NOT NULL |
| valorComissao | valor_comissao | numeric(10,2) | NOT NULL |
| dataCalculo | data_calculo | timestamp | NOT NULL, padrão now() |
| dataRepasse | data_repasse | timestamp | NULL |
| statusPagamento | status_pagamento | varchar(30) | NOT NULL, padrão 'PENDENTE' |

### StatusAgendamento (enum) → coluna `agendamento.status`

O enum não tem tabela própria. O valor é gravado como inteiro (`codigo`) na coluna `status` da tabela `agendamento`:

| Constante | Código |
|---|---|
| AGENDADO | 1 |
| BLOQUEADO | 2 |
| CANCELADO | 3 |
| PENDENTE | 4 |
| CONCLUIDO | 5 |

## Herança

**Usuario ← Administrador / Profissional**: mapeada com **uma tabela por classe** (estratégia *joined*).

- `usuario` guarda os atributos comuns.
- `administrador` e `profissional` guardam só os atributos próprios. A chave primária de cada uma também é chave estrangeira para `usuario(id)`.
- Com `ON DELETE CASCADE`, excluir um usuário exclui também o registro de administrador ou de profissional.

## Associações

| Associação no diagrama | Multiplicidade | Como ficou no banco |
|---|---|---|
| Profissional **possui** Especialidade | 1..* — 1..* | Tabela associativa `profissional_especialidade`, com PK composta (id_profissional, id_especialidade) |
| Profissional **possui** AgendaProfissional | 1 — 0..* | FK `id_profissional` em `agenda_profissional` |
| Profissional **atende** Agendamento | 1 — 0..* | FK `id_profissional` em `agendamento` |
| Cliente **solicita** Agendamento | 1 — 0..* | FK `id_cliente` em `agendamento` |
| Agendamento ◆ ItemAgendamento (composição) | 1 — 1..* | FK `id_agendamento` em `item_agendamento` com `ON DELETE CASCADE`: os itens são excluídos junto com o agendamento |
| ItemAgendamento **contém** Servico | 0..* — 1 | FK `id_servico` em `item_agendamento` |
| Agendamento **gera** RepasseComissao | 1 — 0..1 | FK `id_agendamento` em `repasse_comissao` com UNIQUE, o que garante no máximo um repasse por agendamento |

Regra geral: em associações 1:N, a FK fica na tabela do lado "muitos". Em associações N:N, cria-se uma tabela associativa. Na associação 1:0..1, a FK fica no lado opcional e recebe UNIQUE.
