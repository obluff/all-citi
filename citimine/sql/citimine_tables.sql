
create database citimine;
use citimine;

CREATE TABLE stations (
  station_id varchar(255) NOT NULL,
  name varchar(255),
  lat decimal not null,
  lon decimal not null,
  last_updated timestamp,
  PRIMARY KEY (station_id)
);

CREATE TABLE station_status (
  station_id varchar(255) NOT NULL,
  num_docks_available int NOT NULL,
  num_bikes_available int NOT NULL,
  num_ebikes_available int NOT NULL,
  last_updated timestamp,
  UNIQUE KEY (station_id, last_updated),
  CONSTRAINT citimine_ibfk_1 FOREIGN KEY (station_id) references stations(station_id)
);
