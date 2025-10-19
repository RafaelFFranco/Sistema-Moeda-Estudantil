-- Dados iniciais para o sistema de moeda estudantil

-- Inserir instituições
INSERT INTO instituicao (id, nome) VALUES (1, 'Universidade X');
INSERT INTO instituicao (id, nome) VALUES (2, 'Faculdade Y');

-- Inserir empresas parceiras
INSERT INTO empresa_parceira (id, nome, email, login, senha) VALUES (1, 'Cantina Universitária', 'cantina@uni.br', 'cantina', 'senha123');
INSERT INTO empresa_parceira (id, nome, email, login, senha) VALUES (2, 'Livraria Campus', 'livraria@campus.br', 'livraria', 'senha123');

-- Inserir um aluno exemplo
INSERT INTO aluno (id, nome, email, cpf, rg, endereco, curso, login, senha, saldo_moedas, instituicao_id)
VALUES (1, 'João da Silva', 'joao@exemplo.com', '11122233344', 'MG1234567', 'Rua A, 123', 'Sistemas', 'joao', 'senha', 0, 1);
