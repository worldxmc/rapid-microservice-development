package cloud.framework.cache;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis配置类
 * @Configuration 定义配置类
 * @EnableCaching 开启缓存
 * @ConditionalOnProperty(name={"spring.redis"}) 在yaml文件检测到指定参数配置才加载当前配置类
 * @EnableConfigurationProperties({RedisProperties.class}) 在加载当前配置类前先将其依赖的属性类注册到容器中
 *
 * @author xmc
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name={"spring.redis.host"})
@EnableConfigurationProperties({RedisProperties.class})
public class RedisConfiguration extends CachingConfigurerSupport {

    @Autowired
    private RedisProperties redisProperties;

    public RedisConfiguration() {
    }

    /**
     * 注册缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        FastJsonRedisSerializer serializer = new FastJsonRedisSerializer(Object.class);
        SerializationPair<Object> pair = SerializationPair.fromSerializer(serializer);
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
        return new RedisCacheManager(redisCacheWriter, config);
    }

    /**
     * 注册redisTemplate
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer(Object.class);
        template.setConnectionFactory(redisConnectionFactory);
        template.setDefaultSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 注册redis连接池lettuce（lettuce基于netty实现与redis的同步及异步通信）
     */
    @Bean(destroyMethod = "destroy")
    @ConditionalOnProperty(name={"spring.redis.iscluster"}, havingValue="true")
    public RedisConnectionFactory newLettuceConnectionFactory() {
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enableAllAdaptiveRefreshTriggers()
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(25L))
                .enablePeriodicRefresh(Duration.ofSeconds(20L))
                .build();
        ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .autoReconnect(true)
                .socketOptions(SocketOptions.builder().keepAlive(true).build())
                .validateClusterNodeMembership(false)
                .build();
        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                .clientOptions(clusterClientOptions)
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .build();
        return new LettuceConnectionFactory(this.getClusterConfiguration(), lettuceClientConfiguration);
    }

    /**
     * 获取redis集群配置
     */
    private RedisClusterConfiguration getClusterConfiguration(){
        RedisProperties.Cluster cluster = this.redisProperties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(cluster.getNodes());
        if(cluster.getMaxRedirects() != null){
            config.setMaxRedirects(cluster.getMaxRedirects());
        }
        if(this.redisProperties.getPassword() != null){
            config.setPassword(RedisPassword.of(this.redisProperties.getPassword()));
        }
        return config;
    }

    /**
     * 提供自定义hash key生成规则（redis集群模式下缓存key基于方法参数计算生成，但由于Arrays.deepHashCode方法在不同机器下会生成不同的值，这样会导致在机器2上取不到同一接口在机器1上生成的缓存）
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder builder = new StringBuilder();
            builder.append(target.getClass().getName());
            builder.append(method.getName());
            Object[] paramsArray = params;
            for(int i = 0; i < params.length; i++) {
                Object obj = paramsArray[i];
                builder.append(obj.toString());
            }
            return builder.toString();
        };
    }

    /**
     * 注册redisUtils工具类
     */
    @Bean
    public RedisUtils redisUtils() {
        return new RedisUtils();
    }

}