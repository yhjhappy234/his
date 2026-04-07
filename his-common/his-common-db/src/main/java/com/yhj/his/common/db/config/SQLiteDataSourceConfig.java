package com.yhj.his.common.db.config;

import org.hibernate.community.dialect.SQLiteDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * SQLite数据源配置
 * <p>
 * 当配置了 sqlite.jdbc.url 时自动启用
 * </p>
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.yhj.his.**.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
@ConditionalOnProperty(name = "sqlite.jdbc.url")
public class SQLiteDataSourceConfig {

    @Value("${sqlite.jdbc.url}")
    private String jdbcUrl;

    /**
     * 配置SQLite数据源
     *
     * @return DataSource实例
     */
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .driverClassName("org.sqlite.JDBC")
                .build();
    }

    /**
     * 配置JPA EntityManagerFactory
     *
     * @param dataSource 数据源
     * @return EntityManagerFactory实例
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.yhj.his.**.entity", "com.yhj.his.**.domain");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", SQLiteDialect.class.getName());
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", false);
        properties.put("hibernate.format_sql", true);
        em.setJpaPropertyMap(properties);

        return em;
    }

    /**
     * 配置事务管理器
     *
     * @param entityManagerFactory EntityManagerFactory实例
     * @return PlatformTransactionManager实例
     */
    @Bean
    public PlatformTransactionManager transactionManager(
            LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    /**
     * 配置JdbcTemplate
     *
     * @param dataSource 数据源
     * @return JdbcTemplate实例
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}