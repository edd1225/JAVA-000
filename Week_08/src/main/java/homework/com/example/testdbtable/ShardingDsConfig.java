package homework.com.example.testdbtable;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA
 * User: yyzz
 * Date: 2020/12/1
 * Time: 16:26
 */
@Configuration
public class ShardingDsConfig {
    @Bean
    public DataSource shardingDataSource() throws IOException, SQLException {
        return YamlShardingSphereDataSourceFactory.createDataSource(new File(ShardingDsConfig.class.getResource("/META-INF/sharding-databases-tables.yaml").getFile()));
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("shardingDataSource") DataSource shardingDataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(shardingDataSource);
        return bean.getObject();
    }

}
