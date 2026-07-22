export interface PersonRequest {
  documento: string;
  nome: string;
  sobrenome: string;
  email: string;
}

export interface Person {
  id: number;
  documento: string;
  nome: string;
  sobrenome: string;
  email: string;
}

export interface NationalityResponse {
  pessoaId: number;
  nome: string;
  codigoPais: string;
  nacionalidade: string;
  probabilidade: number;
  quantidadeAmostras?: number;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string>;
}