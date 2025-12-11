/opt/mssql/bin/sqlservr &

echo "Waiting for SQL Server to start..."
sleep 30

echo "Creating BidVerse database and tables..."
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P $SA_PASSWORD -i /usr/src/app/init.sql

echo "Database initialization completed!"

wait