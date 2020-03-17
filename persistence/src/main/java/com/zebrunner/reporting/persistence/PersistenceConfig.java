package com.zebrunner.reporting.persistence;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zebrunner.reporting.persistence.utils.TenancyDataSourceWrapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hibernate.cfg.AvailableSettings.NON_CONTEXTUAL_LOB_CREATION;

@Configuration
@EnableJpaRepositories
public class PersistenceConfig {

    private static final String APP_SQL_SESSION_FACTORY_BEAN_NAME = "applicationSqlSessionFactory";
    private static final String APP_MAPPERS_BASE_PACKAGE = "com.zebrunner.reporting.persistence.dao.mysql.application";

    private static final String MNG_SQL_SESSION_FACTORY_BEAN_NAME = "managementSqlSessionFactory";
    private static final String MNG_MAPPERS_BASE_PACKAGE = "com.zebrunner.reporting.persistence.dao.mysql.management";

    @Bean
    public ComboPooledDataSource appDataSource(
            @Value("${datasource.driver-class}") String driverClass,
            @Value("${datasource.url}") String jdbcUrl,
            @Value("${datasource.username}") String dbUsername,
            @Value("${datasource.password}") String dbPassword,
            @Value("${datasource.pool-size}") int maxPoolSize,
            @Value("${datasource.idle-connection-test-period}") int idleConnectionTestPeriod
    ) throws PropertyVetoException {
        return buildDataSource(driverClass, jdbcUrl, dbUsername, dbPassword, maxPoolSize, idleConnectionTestPeriod);
    }

    @Bean
    public ComboPooledDataSource managementDataSource(
            @Value("${datasource.driver-class}") String driverClass,
            @Value("${datasource.url}") String jdbcUrl,
            @Value("${datasource.username}") String dbUsername,
            @Value("${datasource.password}") String dbPassword,
            @Value("${datasource.pool-size}") int maxPoolSize,
            @Value("${datasource.idle-connection-test-period}") int idleConnectionTestPeriod
    ) throws PropertyVetoException {
        ComboPooledDataSource dataSource = buildDataSource(driverClass, jdbcUrl, dbUsername, dbPassword, maxPoolSize, idleConnectionTestPeriod);
        dataSource.setIdentityToken("management");
        return dataSource;
    }

    /*@Bean
    @Primary
    public DataSourceTransactionManager transactionManager(TenancyDataSourceWrapper tenancyAppDSWrapper) {
        return new DataSourceTransactionManager(tenancyAppDSWrapper.getDataSource());
    }*/

    @Bean
    public DataSourceTransactionManager managementTransactionManager(TenancyDataSourceWrapper tenancyMngDSWrapper) {
        return new DataSourceTransactionManager(tenancyMngDSWrapper.getDataSource());
    }

    @Bean
    public SqlSessionFactoryBean applicationSqlSessionFactory(
            TenancyDataSourceWrapper tenancyAppDSWrapper,
            @Value("classpath*:/com/zebrunner/reporting/persistence/dao/mappers/application/**/*.xml") Resource[] appMapperResources
    ) {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(tenancyAppDSWrapper.getDataSource());
        sessionFactoryBean.setMapperLocations(appMapperResources);
        return sessionFactoryBean;
    }

    @Bean
    public SqlSessionFactoryBean managementSqlSessionFactory(
            TenancyDataSourceWrapper tenancyMngDSWrapper,
            @Value("classpath*:/com/zebrunner/reporting/persistence/dao/mappers/management/**/*.xml") Resource[] managementMapperResources
    ) {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(tenancyMngDSWrapper.getDataSource());
        sessionFactoryBean.setMapperLocations(managementMapperResources);
        return sessionFactoryBean;
    }

    @Bean
    public static MapperScannerConfigurer appMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(APP_MAPPERS_BASE_PACKAGE);
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(APP_SQL_SESSION_FACTORY_BEAN_NAME);
        return mapperScannerConfigurer;
    }

    @Bean
    public static MapperScannerConfigurer managementMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(MNG_MAPPERS_BASE_PACKAGE);
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(MNG_SQL_SESSION_FACTORY_BEAN_NAME);
        return mapperScannerConfigurer;
    }

    @Bean
    public TenancyDataSourceWrapper tenancyAppDSWrapper(ComboPooledDataSource appDataSource) {
        return new TenancyDataSourceWrapper(appDataSource);
    }

    @Bean
    public TenancyDataSourceWrapper tenancyMngDSWrapper(ComboPooledDataSource managementDataSource) {
        return new TenancyDataSourceWrapper(managementDataSource);
    }

    private ComboPooledDataSource buildDataSource(String driverClass, String jdbcUrl, String dbUsername,
                                                  String dbPassword, int maxPoolSize, int idleConnectionTestPeriod)
            throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(TenancyDataSourceWrapper tenancyAppDSWrapper) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(tenancyAppDSWrapper.getDataSource());
        entityManagerFactoryBean.setPackagesToScan("com.zebrunner.reporting.domain.entity");
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.getJpaPropertyMap().put(NON_CONTEXTUAL_LOB_CREATION, true);
        entityManagerFactoryBean.setJpaProperties(jpaProperties());

        return entityManagerFactoryBean;
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory, TenancyDataSourceWrapper tenancyAppDSWrapper) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(tenancyAppDSWrapper.getDataSource());
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    private Properties jpaProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());

        Properties properties = new Properties();
        properties.putAll(props);

        return properties;
    }

}
