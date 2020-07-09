package com.zebrunner.reporting.persistence.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * DataSourceInterceptor - proxies data sources calls for custom interceptors.
 * 
 * @author akhursevich
 */
public abstract class DataSourceInterceptor {

    private final InvocationHandler handler;

    protected DataSourceInterceptor(final ComboPooledDataSource delegate) {
        this.handler = (proxy, method, args) -> (method.getName().equals("getConnection")) ? getConnection(delegate) : method.invoke(delegate, args);
    }

    protected Connection getConnection(final ComboPooledDataSource delegate) throws SQLException {
        return delegate.getConnection();
    }

    public static DataSource wrapInterceptor(DataSourceInterceptor instance) {
        return (DataSource) Proxy.newProxyInstance(instance.getClass().getClassLoader(), new Class[] { DataSource.class }, instance.handler);
    }
}
