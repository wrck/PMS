#!/bin/bash
# Wait for MySQL to be ready, then verify Flyway migrations
echo "Waiting for MySQL to be ready..."
until mysqladmin ping -h localhost -P 3306 -uroot -proot --silent; do
  sleep 1
done
echo "MySQL is ready. Checking Flyway migrations..."
mysql -h localhost -P 3306 -uroot -proot -e "USE network_equipment_pms; SHOW TABLES;" 2>/dev/null
echo "Done."
