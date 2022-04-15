#!/usr/bin/env bash
set -euo pipefail

TMP_SQL_PATH=/tmp/tmp_scraped_citimine$(date +%s)
function load_dotenv {
    if test -f "../.env"; then
      set -o allexport
      source ../.env
      set +o allexport
    fi
}

function write_mysql_updates() {
    station_status=$(curl https://gbfs.citibikenyc.com/gbfs/en/station_status.json)
    station_status_cols="num_docks_available, num_bikes_available, num_ebikes_available, station_id"

    station=$(curl https://gbfs.citibikenyc.com/gbfs/en/station_information.json)
    station_cols="station_id, name, lon, lat"

    echo """
    use citimine;
    start transaction;

    $(insert_statement "$station" "stations" "$station_cols");
    $(insert_statement "$station_status" "station_status" "$station_status_cols");

    commit;
    """ > $TMP_SQL_PATH
}

function execute_mysql_file() {
    local sqlfilepath=$1
    mysql -u$MYSQL_UN -p$MYSQL_PW -h127.0.0.1 -P3311 < $sqlfilepath
}

function insert_statement() {
    local data=$1
    local table_name=$2
    local cols=$3

    last_updated=$(echo $data | jq '.last_updated')
    last_updated_sql=$(echo "FROM_UNIXTIME($last_updated)")
    col_selector=$(echo $cols | sed 's/\</\./g')
    jq_query=".data.stations | .[] | [${col_selector}] | @csv"
    insert_data=$(echo $data | jq -r "$jq_query" | awk -v lu=$last_updated_sql '{print "(" $0 "," lu"),"}' | sed '$ s/,$//g')
    on_duplicate_key=$(echo $cols | tr "," "\n" | awk '{print $1 "=" $1","}' | sed '$ s/,$//g')

    echo "INSERT INTO $table_name
          ($cols, last_updated)
          VALUES
          $insert_data
          ON DUPLICATE KEY UPDATE
          $on_duplicate_key"
}

load_dotenv
write_mysql_updates
execute_mysql_file $TMP_SQL_PATH
echo "updated successfully"
