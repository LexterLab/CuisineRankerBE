@echo off

rem Bring down any existing containers and networks
docker compose down

rem Start the containers in background mode
docker compose up -d --build

