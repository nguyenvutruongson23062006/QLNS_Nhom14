@echo off
cd /d "%~dp0"
call mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=sqlite" "-Dspring-boot.run.arguments=--server.port=8095"
