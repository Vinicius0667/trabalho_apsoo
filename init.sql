CREATE TABLE IF NOT EXISTS public.usuario (
    id bigserial NOT NULL,
    nome varchar(60) NOT NULL,
    cpf varchar(14) NULL,
    telefone varchar(15) NOT NULL,
    email varchar(60) NOT NULL,
    senha varchar(255) NOT NULL,
    CONSTRAINT usuario_pkey PRIMARY KEY (id),
    CONSTRAINT usuario_email_key UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS public.administrador (
    id int8 NOT NULL,
    CONSTRAINT administrador_pkey PRIMARY KEY (id),
    CONSTRAINT administrador_id_fkey FOREIGN KEY (id) REFERENCES public.usuario(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.profissional (
    id_profissional int8 NOT NULL,
    percentual_comissao numeric(5,2) DEFAULT 0 NOT NULL,
    status_profissional varchar(30) DEFAULT 'DISPONIVEL' NOT NULL,
    ativo bool DEFAULT true NOT NULL,
    CONSTRAINT profissional_pkey PRIMARY KEY (id_profissional),
    CONSTRAINT profissional_id_fkey FOREIGN KEY (id_profissional) REFERENCES public.usuario(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.especialidade (
    id_especialidade bigserial NOT NULL,
    nome varchar(50) NOT NULL,
    descricao varchar(255) NULL,
    CONSTRAINT especialidade_pkey PRIMARY KEY (id_especialidade)
);

CREATE TABLE IF NOT EXISTS public.profissional_especialidade (
    id_profissional int8 NOT NULL,
    id_especialidade int8 NOT NULL,
    CONSTRAINT profissional_especialidade_pkey PRIMARY KEY (id_profissional, id_especialidade),
    CONSTRAINT pe_profissional_fkey FOREIGN KEY (id_profissional) REFERENCES public.profissional(id_profissional) ON DELETE CASCADE,
    CONSTRAINT pe_especialidade_fkey FOREIGN KEY (id_especialidade) REFERENCES public.especialidade(id_especialidade) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.agenda_profissional (
    id_agenda bigserial NOT NULL,
    id_profissional int8 NOT NULL,
    data date NOT NULL,
    horario_inicio time NOT NULL,
    horario_fim time NOT NULL,
    status varchar(30) DEFAULT 'DISPONIVEL' NOT NULL,
    CONSTRAINT agenda_profissional_pkey PRIMARY KEY (id_agenda),
    CONSTRAINT agenda_profissional_fkey FOREIGN KEY (id_profissional) REFERENCES public.profissional(id_profissional) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.cliente (
    id bigserial NOT NULL,
    nome varchar(60) NOT NULL,
    data_cadastro date DEFAULT CURRENT_DATE NOT NULL,
    observacoes varchar(255) NULL,
    CONSTRAINT cliente_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.servico (
    id bigserial NOT NULL,
    nome varchar(60) NOT NULL,
    descricao varchar(255) NULL,
    duracao_minutos int4 NOT NULL,
    valor_padrao numeric(10,2) NOT NULL,
    CONSTRAINT servico_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.agendamento (
    id bigserial NOT NULL,
    id_cliente int8 NOT NULL,
    id_profissional int8 NOT NULL,
    data_agendada date NOT NULL,
    horario_inicio time NOT NULL,
    horario_fim time NOT NULL,
    status int4 DEFAULT 1 NOT NULL,
    motivo_cancelamento varchar(255) NULL,
    observacoes varchar(255) NULL,
    data_realizacao date NULL,
    horario_inicio_realizacao time NULL,
    horario_fim_realizacao time NULL,
    CONSTRAINT agendamento_pkey PRIMARY KEY (id),
    CONSTRAINT agendamento_cliente_fkey FOREIGN KEY (id_cliente) REFERENCES public.cliente(id),
    CONSTRAINT agendamento_profissional_fkey FOREIGN KEY (id_profissional) REFERENCES public.profissional(id_profissional)
);

CREATE TABLE IF NOT EXISTS public.item_agendamento (
    id bigserial NOT NULL,
    id_agendamento int8 NOT NULL,
    id_servico int8 NOT NULL,
    duracao_realizada int4 NULL,
    valor_cobrado numeric(10,2) NOT NULL,
    CONSTRAINT item_agendamento_pkey PRIMARY KEY (id),
    CONSTRAINT item_agendamento_agendamento_fkey FOREIGN KEY (id_agendamento) REFERENCES public.agendamento(id) ON DELETE CASCADE,
    CONSTRAINT item_agendamento_servico_fkey FOREIGN KEY (id_servico) REFERENCES public.servico(id)
);

CREATE TABLE IF NOT EXISTS public.repasse_comissao (
    id_repasse bigserial NOT NULL,
    id_agendamento int8 NOT NULL,
    valor_pago_cliente numeric(10,2) NOT NULL,
    percentual_aplicado numeric(5,2) NOT NULL,
    valor_comissao numeric(10,2) NOT NULL,
    data_calculo timestamp DEFAULT now() NOT NULL,
    data_repasse timestamp NULL,
    status_pagamento varchar(30) DEFAULT 'PENDENTE' NOT NULL,
    CONSTRAINT repasse_comissao_pkey PRIMARY KEY (id_repasse),
    CONSTRAINT repasse_comissao_agendamento_key UNIQUE (id_agendamento),
    CONSTRAINT repasse_comissao_agendamento_fkey FOREIGN KEY (id_agendamento) REFERENCES public.agendamento(id) ON DELETE CASCADE
);

INSERT INTO public.usuario (id, nome, cpf, telefone, email, senha) VALUES
    (1, 'Admin', '000.000.000-00', '67999990000', 'admin@agenda.com', 'admin'),
    (2, 'Maria Souza', '111.111.111-11', '67999991111', 'maria@agenda.com', '123'),
    (3, 'Joao Lima', '222.222.222-22', '67999992222', 'joao@agenda.com', '123'),
    (4, 'Fernanda Alves', '333.333.333-33', '67999993333', 'fernanda@agenda.com', '123'),
    (5, 'Pedro Santos', '444.444.444-44', '67999994444', 'pedro@agenda.com', '123')
ON CONFLICT DO NOTHING;
SELECT setval('usuario_id_seq', (SELECT MAX(id) FROM public.usuario));

INSERT INTO public.administrador (id) VALUES (1) ON CONFLICT DO NOTHING;

INSERT INTO public.profissional (id_profissional, percentual_comissao) VALUES
    (2, 40.00),
    (3, 35.00),
    (4, 45.00),
    (5, 30.00)
ON CONFLICT DO NOTHING;

INSERT INTO public.especialidade (id_especialidade, nome, descricao) VALUES
    (1, 'Cabelo', 'Cortes e tratamentos capilares'),
    (2, 'Unhas', 'Manicure e pedicure'),
    (3, 'Barba', 'Barba e acabamento')
ON CONFLICT DO NOTHING;
SELECT setval('especialidade_id_especialidade_seq', (SELECT MAX(id_especialidade) FROM public.especialidade));

INSERT INTO public.profissional_especialidade (id_profissional, id_especialidade) VALUES
    (2, 1), (2, 2), (3, 1), (4, 2), (5, 1), (5, 3)
ON CONFLICT DO NOTHING;

INSERT INTO public.cliente (id, nome, observacoes) VALUES
    (1, 'Ana Pereira', NULL),
    (2, 'Carlos Mendes', 'Prefere atendimento pela manha'),
    (3, 'Beatriz Oliveira', NULL),
    (4, 'Lucas Ferreira', 'Alergico a alguns produtos quimicos'),
    (5, 'Juliana Costa', NULL),
    (6, 'Rafael Rodrigues', NULL),
    (7, 'Camila Martins', 'Prefere atendimento a tarde'),
    (8, 'Gabriel Almeida', NULL)
ON CONFLICT DO NOTHING;
SELECT setval('cliente_id_seq', (SELECT MAX(id) FROM public.cliente));

INSERT INTO public.servico (id, nome, descricao, duracao_minutos, valor_padrao) VALUES
    (1, 'Corte', 'Corte de cabelo', 30, 50.00),
    (2, 'Escova', 'Escova simples', 40, 45.00),
    (3, 'Manicure', 'Manicure completa', 45, 35.00),
    (4, 'Pedicure', 'Pedicure completa', 45, 40.00),
    (5, 'Barba', 'Barba com toalha quente', 30, 30.00)
ON CONFLICT DO NOTHING;
SELECT setval('servico_id_seq', (SELECT MAX(id) FROM public.servico));
