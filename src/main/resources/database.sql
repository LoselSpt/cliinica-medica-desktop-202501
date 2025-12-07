DROP DATABASE IF EXISTS clinica;
CREATE DATABASE clinica
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE clinica;

CREATE TABLE especialidade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255)
);

CREATE TABLE pessoas (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  telefone VARCHAR(20),
  email VARCHAR(100)
);

CREATE TABLE medico (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  crm VARCHAR(20) NOT NULL,
  id_pessoa BIGINT NOT NULL,
  id_especialidade BIGINT NOT NULL,
  FOREIGN KEY (id_pessoa) REFERENCES pessoas(id),
  FOREIGN KEY (id_especialidade) REFERENCES especialidade(id)
);

CREATE TABLE secretaria (
    id_pessoa BIGINT PRIMARY KEY,
    pis VARCHAR(20),
    FOREIGN KEY (id_pessoa) REFERENCES pessoas(id)
);

CREATE TABLE perfis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

CREATE TABLE perfil_funcionalidades (
    id_perfil BIGINT NOT NULL,
    funcionalidade VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_perfil, funcionalidade),
    FOREIGN KEY (id_perfil) REFERENCES perfis(id)
);

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    id_perfil BIGINT NOT NULL,
    id_funcionario BIGINT NOT NULL,
    FOREIGN KEY (id_perfil) REFERENCES perfis(id),
    FOREIGN KEY (id_funcionario) REFERENCES pessoas(id)
);

CREATE TABLE convenios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cnpj VARCHAR(20)
);

CREATE TABLE consultas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_medico BIGINT NOT NULL,
    id_paciente BIGINT NOT NULL,
    id_convenio BIGINT,
    id_secretaria BIGINT,
    data_hora DATETIME NOT NULL,
    is_retorno BOOLEAN DEFAULT FALSE,
    status VARCHAR(20),
    motivo VARCHAR(255),
    FOREIGN KEY (id_medico) REFERENCES medico(id),
    FOREIGN KEY (id_paciente) REFERENCES pessoas(id),
    FOREIGN KEY (id_convenio) REFERENCES convenios(id),
    FOREIGN KEY (id_secretaria) REFERENCES secretaria(id_pessoa)
);

CREATE TABLE prontuarios (
    id_consulta BIGINT PRIMARY KEY,
    historico TEXT,
    receituario TEXT,
    exames TEXT,
    FOREIGN KEY (id_consulta) REFERENCES consultas(id) ON DELETE CASCADE
);

-- Inserir Perfil
INSERT INTO perfis (nome) VALUES ('Médico'), ('Secretária');
-- Inserir Funcionalidades
INSERT INTO perfil_funcionalidades VALUES 
(1, 'REALIZAR_CONSULTA'), 
(2, 'AGENDAR_CONSULTA'), (2, 'CADASTRAR_PACIENTE');
-- Inserir Pessoas
INSERT INTO pessoas (nome, email) VALUES ('Guilherme', 'guilherme@clinic.com'), ('Losel', 'losel@clinic.com');
-- Inserir Médico/Secretária
INSERT INTO especialidade (nome) VALUES ('Clínico Geral');
INSERT INTO medico (crm, id_pessoa, id_especialidade) VALUES ('12345', 1, 1);
INSERT INTO secretaria (id_pessoa, pis) VALUES (2, '123');
-- Inserir Usuários
INSERT INTO usuarios (login, senha, status, id_perfil, id_funcionario) VALUES 
('house', '123', 'ATIVO', 1, 1),
('losel', '123', 'ATIVO', 2, 2);