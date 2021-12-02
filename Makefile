SHELL := /bin/bash
.PHONY: start

start-app:
	-@docker rm -f olympics-api olympics-database
	@mvn clean install -DskipTests
	@docker-compose up -d

stop-app:
	@docker-compose down

start-db:
	sh start-db.sh