-- Dados iniciais para o sistema de moeda estudantil

-- Inserir instituições (sem especificar ID para usar auto-increment)
INSERT INTO instituicao (nome) VALUES ('Universidade X');
INSERT INTO instituicao (nome) VALUES ('Faculdade Y');

-- Inserir empresas parceiras (sem especificar ID para usar auto-increment)
INSERT INTO empresa_parceira (nome, email, login, senha) VALUES ('Cantina Universitária', 'cantina@uni.br', 'cantina', 'senha123');
INSERT INTO empresa_parceira (nome, email, login, senha) VALUES ('Livraria Campus', 'livraria@campus.br', 'livraria', 'senha123');

-- Inserir um aluno exemplo (usando ID 1 da instituição)
INSERT INTO aluno (nome, email, cpf, rg, endereco, curso, login, senha, saldo_moedas, instituicao_id)
VALUES ('João da Silva', 'joao@exemplo.com', '11122233344', 'MG1234567', 'Rua A, 123', 'Sistemas', 'joao', 'senha', 0, 1);
