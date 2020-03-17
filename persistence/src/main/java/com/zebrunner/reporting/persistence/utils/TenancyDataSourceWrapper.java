package com.zebrunner.reporting.persistence.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zebrunner.reporting.domain.db.Tenancy;

/**
 * TenancyDataSourceWrapper - initializes schema according to current tenant.
 * 
 * @author akhursevich
 */
public class TenancyDataSourceWrapper {

    private static final String SET_SEARCH_PATH_SQL = "SET search_path TO '%s';";

    private final DataSource dataSource;

    public TenancyDataSourceWrapper(ComboPooledDataSource ds) {
        this.dataSource = DataSourceInterceptor.wrapInterceptor(new DataSourceInterceptor(ds) {
            @Override
            protected Connection getConnection(ComboPooledDataSource delegate) throws SQLException {
                Connection connection = delegate.getConnection();
                String schema = getSchema(delegate);
                connection.prepareStatement(String.format(SET_SEARCH_PATH_SQL, schema)).execute();
                return connection;
            }
        });
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }

    private static String getSchema(ComboPooledDataSource delegate) {
        return delegate.getIdentityToken().equals(Tenancy
                .getManagementSchema()) ? Tenancy.getManagementSchema() : TenancyContext.getTenantName();
    }
}
