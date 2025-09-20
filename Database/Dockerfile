FROM mcr.microsoft.com/mssql/server:2022-latest

ENV ACCEPT_EULA=Y
ENV SA_PASSWORD=n5m_35z3m_A_73137
ENV MSSQL_PID=Express

EXPOSE 1433

USER root
RUN mkdir -p /var/opt/mssql/data && \
    mkdir -p /usr/src/app && \
    chown -R mssql:mssql /var/opt/mssql && \
    chmod -R 755 /var/opt/mssql

COPY init.sql /usr/src/app/
COPY setup.sh /usr/src/app/
RUN chmod +x /usr/src/app/setup.sh

USER mssql

CMD ["/usr/src/app/setup.sh"]