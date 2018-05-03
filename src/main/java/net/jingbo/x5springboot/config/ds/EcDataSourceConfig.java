package net.jingbo.x5springboot.config.ds;

import com.alibaba.druid.pool.DruidDataSource;
import net.jingbo.x5springboot.interceptor.Wex5ResultSetHandlerInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by Administrator on 2017-3-30.
 */
@Configuration
// 扫描 Mapper 接口并容器管理
@MapperScan(basePackages = EcDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "ecSqlSessionFactory")
public class EcDataSourceConfig {

    // 精确到 ec 目录，以便跟其他数据源隔离
    static final String PACKAGE = "net.jingbo.x5springboot.api.dao";
    static final String MAPPER_LOCATION = "classpath:mapper/ec/*.xml";

    @Value("${mybatis.type-aliases-package}")
    private String typeAliasesPkg;

    @Value("${ec.datasource.url}")
    private String url;

    @Value("${ec.datasource.username}")
    private String user;

    @Value("${ec.datasource.password}")
    private String password;

    @Value("${ec.datasource.driverClassName}")
    private String driverClass;

    @Bean(name = "ecDataSource")
    @Primary
    public DataSource masterDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "ecTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(masterDataSource());
    }

    @Bean(name = "ecSqlSessionFactory")
    /**
     * 此处要配置一个主次，不然注入时spring boot 会报错
     * 真正精准选择使用那个 sqlSessionFactory 在mapper.xml文档里定义
     */
    @Primary
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("ecDataSource") DataSource masterDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setTypeAliasesPackage(typeAliasesPkg);

        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);

        sessionFactory.setConfiguration(configuration);
        sessionFactory.setPlugins(new Interceptor[]{new Wex5ResultSetHandlerInterceptor()});

        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(EcDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }

}
