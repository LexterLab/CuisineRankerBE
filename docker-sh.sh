#!/bin/bash

# Bring down any existing containers and networks
docker-compose down

# Start the containers in background mode
docker-compose up -d --build