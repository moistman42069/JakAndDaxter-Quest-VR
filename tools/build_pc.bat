@echo off
rem Builds the PC side of OpenGOAL (Precursor fork): gk (the game), goalc (the GOAL compiler), decompiler, extractor.
rem   tools\build_pc.bat [targets...]      (default: gk goalc decompiler extractor)
setlocal
call "C:\Program Files (x86)\Microsoft Visual Studio\2022\BuildTools\VC\Auxiliary\Build\vcvars64.bat" >nul || exit /b 1
set "PATH=C:\Program Files\LLVM\bin;C:\JakQuest\toolchain\nasm-2.16.03;%PATH%"
set "SRC=%~dp0..\third_party\Precursor"
cd /d "%SRC%" || exit /b 1
if not exist out\build\Release\build.ninja (
  rem curl's test for ioctlsocket fails under Clang 23 (int* passed for u_long* is an error now): Windows has it.
  cmake --preset Release-windows-clang -DHAVE_IOCTLSOCKET_FIONBIO=1 || exit /b 1
)
set "TARGETS=%*"
if "%TARGETS%"=="" set "TARGETS=gk goalc decompiler extractor"
cmake --build out\build\Release --target %TARGETS% || exit /b 1
echo BUILD OK
