@echo off
setlocal
cd /d "%~dp0"

title People API - Inicializador

echo ==========================================
echo          PEOPLE API - INICIALIZACAO
echo ==========================================
echo.

if not exist "pom.xml" (
    echo [ERRO] O arquivo pom.xml nao foi encontrado.
    echo Coloque este arquivo na raiz do projeto.
    pause
    exit /b 1
)

if not exist "frontend\package.json" (
    echo [ERRO] O arquivo frontend\package.json nao foi encontrado.
    echo Confirme se a pasta frontend esta na raiz do projeto.
    pause
    exit /b 1
)

where java >nul 2>nul
if errorlevel 1 (
    echo [ERRO] Java nao foi encontrado no PATH.
    echo Instale o Java 21 e abra novamente o terminal.
    pause
    exit /b 1
)

where npm >nul 2>nul
if errorlevel 1 (
    echo [ERRO] npm nao foi encontrado no PATH.
    echo Instale o Node.js e abra novamente o terminal.
    pause
    exit /b 1
)

if not exist "frontend\node_modules" (
    echo [INFO] Instalando dependencias do frontend...
    pushd "frontend"
    call npm install

    if errorlevel 1 (
        popd
        echo [ERRO] Nao foi possivel instalar as dependencias do frontend.
        pause
        exit /b 1
    )

    popd
    echo.
)

echo [INFO] Iniciando backend na porta 8080...

if exist "mvnw.cmd" (
    start "People API - Backend" cmd /k "cd /d ""%~dp0"" && call mvnw.cmd spring-boot:run"
) else (
    where mvn >nul 2>nul

    if errorlevel 1 (
        echo [ERRO] mvnw.cmd e Maven global nao foram encontrados.
        pause
        exit /b 1
    )

    start "People API - Backend" cmd /k "cd /d ""%~dp0"" && mvn spring-boot:run"
)

echo [INFO] Aguardando o backend iniciar...
timeout /t 5 /nobreak >nul

echo [INFO] Iniciando frontend na porta 4200...
start "People API - Frontend" cmd /k "cd /d ""%~dp0frontend"" && npm start"

echo.
echo ==========================================
echo Aplicacao iniciada em duas janelas.
echo.
echo Frontend: http://localhost:4200
echo Backend : http://localhost:8080
echo H2      : http://localhost:8080/h2-console
echo ==========================================
echo.
echo Para encerrar, feche as janelas do backend e frontend.
pause

endlocal
