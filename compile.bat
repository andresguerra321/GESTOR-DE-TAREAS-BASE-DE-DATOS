@echo off
echo Compilando TaskFlow...

if not exist "out" mkdir out

javac -encoding UTF-8 -cp "lib/*" -d out src/main/java/com/taskflow/model/*.java src/main/java/com/taskflow/dao/*.java src/main/java/com/taskflow/db/*.java src/main/java/com/taskflow/ui/*.java src/main/java/com/taskflow/*.java

if %ERRORLEVEL% equ 0 (
    echo Compilacion exitosa.
) else (
    echo Error en la compilacion.
)
