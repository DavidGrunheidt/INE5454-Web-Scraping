# /bin/bash
CONTAINER_NAME="olympics-database"
DB_NAME="olympics"
USER="postgres"
PASSWORD="postgres"
docker rm -f $(docker ps -a | grep $CONTAINER_NAME | awk '{print $1}')
sleep 2
# start mysql
docker run \
  \--name $CONTAINER_NAME -p 5432:5432 -d \
  -e POSTGRES_PASSWORD=$PASSWORD -e POSTGRES_USER=$USER -e POSTGRES_DB=$DB_NAME \
  postgres:13.1-alpine
sleep 10
#cat dump.sql | docker exec -i $CONTAINER_NAME psql -U $USER -d $DB_NAME