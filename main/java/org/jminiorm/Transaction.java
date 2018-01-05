package org.jminiorm;

import org.jminiorm.dialect.ISQLDialect;
import org.jminiorm.exception.DBException;
import org.jminiorm.executor.IStatementExecutor;
import org.jminiorm.mapping.provider.IORMappingProvider;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction extends AbstractQueryTarget implements ITransaction {

    private IDatabase database;
    private Connection connection;

    public Transaction(IDatabase database) throws DBException {
        this.database = database;
        connection = database.getConnection();
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public void commit() throws DBException {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public void rollback() throws DBException {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @Override
    public void close() throws DBException {
        database.releaseConnection(connection);
    }

    @Override
    public Connection getConnection() throws DBException {
        return connection;
    }

    @Override
    public void releaseConnection(Connection con) throws DBException {
    }

    @Override
    public ISQLDialect getDialect() {
        return database.getDialect();
    }

    @Override
    public IORMappingProvider getORMappingProvider() {
        return database.getORMappingProvider();
    }

    @Override
    public IStatementExecutor getStatementExecutor() {
        return database.getStatementExecutor();
    }

}
