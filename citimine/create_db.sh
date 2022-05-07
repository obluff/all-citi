#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME=citimine-mysql
IMAGE_NAME=mysql:5.7.38-debian

function clean() {
    if [ "$(docker ps -q -a -f name=$CONTAINER_NAME)" ]; then
        echo "deleting old docker image"
        docker stop $CONTAINER_NAME
        docker rm $CONTAINER_NAME
    fi
}

function load_dotenv {
    if test -f "../.env"; then
      set -o allexport
      source ../.env
      set +o allexport
    fi
}

function create_mysql_container () {
    echo "pulling docker image"
    docker pull $IMAGE_NAME
    docker run --name $CONTAINER_NAME -e MYSQL_ROOT_PASSWORD=$MYSQL_PW -p 3311:3306 -d $IMAGE_NAME
}

function wait_for_mysql_container() {
    while ! execute_mysql_cmd "SELECT 1" > /dev/null 2>&1; do
        echo "waiting for container to be ready"
        sleep 5
    done
}

function execute_mysql_cmd() {
    local sql_command=$1
    mysql -u$MYSQL_UN -p$MYSQL_PW -h127.0.0.1 -P3311 -e "$sql_command"
}

function execute_mysql_script () {
    local sqlfilepath=$1
    echo "running $sqlfilepath"
    mysql -u$MYSQL_UN -p$MYSQL_PW -h127.0.0.1 -P3311 < $sqlfilepath
}


load_dotenv
clean
create_mysql_container
wait_for_mysql_container
execute_mysql_script sql/citimine_tables.sql
