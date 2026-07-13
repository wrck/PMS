SELECT TABLE_NAME, TABLE_COLLATION FROM information_schema.TABLES WHERE TABLE_SCHEMA='dpspms' AND TABLE_NAME='pms_lowcode_connector';
SELECT COLUMN_NAME, COLUMN_TYPE, CHARACTER_SET_NAME, COLLATION_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='dpspms' AND TABLE_NAME='pms_lowcode_connector';
SELECT variable_name, variable_value FROM information_schema.global_variables WHERE variable_name IN ('character_set_server','character_set_database','collation_server','collation_database');
