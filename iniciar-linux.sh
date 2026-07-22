#!/usr/bin/env bash

set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$ROOT_DIR/frontend"

cd "$ROOT_DIR"

echo "=========================================="
echo "         PEOPLE API - INICIALIZAÇÃO"
echo "=========================================="
echo

if [[ ! -f "$ROOT_DIR/pom.xml" ]]; then
  echo "[ERRO] O arquivo pom.xml não foi encontrado."
  echo "Coloque este script na raiz do projeto."
  exit 1
fi

if [[ ! -f "$FRONTEND_DIR/package.json" ]]; then
  echo "[ERRO] O arquivo frontend/package.json não foi encontrado."
  echo "Confirme se a pasta frontend está na raiz do projeto."
  exit 1
fi

if ! command -v java >/dev/null 2>&1; then
  echo "[ERRO] Java não foi encontrado."
  echo "Instale o Java 21 e tente novamente."
  exit 1
fi

if ! command -v npm >/dev/null 2>&1; then
  echo "[ERRO] npm não foi encontrado."
  echo "Instale o Node.js e tente novamente."
  exit 1
fi

if [[ ! -d "$FRONTEND_DIR/node_modules" ]]; then
  echo "[INFO] Instalando dependências do frontend..."
  (
    cd "$FRONTEND_DIR"
    npm install
  )
  echo
fi

if [[ -f "$ROOT_DIR/mvnw" ]]; then
  chmod +x "$ROOT_DIR/mvnw"
  BACKEND_COMMAND=("$ROOT_DIR/mvnw" "spring-boot:run")
elif command -v mvn >/dev/null 2>&1; then
  BACKEND_COMMAND=("mvn" "spring-boot:run")
else
  echo "[ERRO] O Maven Wrapper e o Maven global não foram encontrados."
  exit 1
fi

BACKEND_PID=""
FRONTEND_PID=""

cleanup() {
  echo
  echo "[INFO] Encerrando aplicação..."

  if [[ -n "$FRONTEND_PID" ]] && kill -0 "$FRONTEND_PID" 2>/dev/null; then
    kill "$FRONTEND_PID" 2>/dev/null || true
  fi

  if [[ -n "$BACKEND_PID" ]] && kill -0 "$BACKEND_PID" 2>/dev/null; then
    kill "$BACKEND_PID" 2>/dev/null || true
  fi

  wait "$FRONTEND_PID" 2>/dev/null || true
  wait "$BACKEND_PID" 2>/dev/null || true
}

trap cleanup EXIT INT TERM

echo "[INFO] Iniciando backend na porta 8080..."
"${BACKEND_COMMAND[@]}" &
BACKEND_PID=$!

echo "[INFO] Aguardando o backend iniciar..."
sleep 5

echo "[INFO] Iniciando frontend na porta 4200..."
(
  cd "$FRONTEND_DIR"
  npm start
) &
FRONTEND_PID=$!

echo
echo "=========================================="
echo "Aplicação iniciada."
echo
echo "Frontend: http://localhost:4200"
echo "Backend : http://localhost:8080"
echo "H2      : http://localhost:8080/h2-console"
echo
echo "Pressione Ctrl + C para encerrar."
echo "=========================================="
echo

wait -n "$BACKEND_PID" "$FRONTEND_PID"
