package cn.cloudwalk.basic.ignite.config;

import cn.cloudwalk.basic.ignite.DiscoverySpiUtil;
import cn.cloudwalk.basic.ignite.model.IgniteCacheMapView;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;

import javax.cache.configuration.Factory;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.expiry.TouchedExpiryPolicy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 使用 ignite 保存 Web Session 方式有两种
 *      1 ignite 自带 Web Session Clustering，在容错的分布式缓存中缓存所有 web 会话
 *          但是截止目前最新版本 V2.4.0 支持的容器只有
 *          a：Apache Tomcat 6/7
 *          b：Eclipse Jetty 9
 *          c：Oracle WebLogic >= 10.3.4
 *          详见 <p>https://apacheignite-mix.readme.io/docs/web-session-clustering</p>
 *      2 ignite 和 spring 结合，替换方式很松弛，只需要替换 {@link SessionRepository} 即可
 * 综合两优缺点，选择第二种方式
 */
//@Configuration
//@Import(SpringHttpSessionConfiguration.class)
public class IgniteSessionConfiguration {

    /**
     * 默认：30 分钟
     * 单位：{@link TimeUnit#SECONDS}
     */
    private int sessionTimeOut = 1800;

    /**
     * 如果 true，则当前节点加入集群模式为 client 模式，反之为 server 模式
     * 在不使用集群的 计算、部署服务、分布式存储 等功能条件下，两者可视为无区别
     * 注意： client 模式条件是 集群已 存在 并且有 server 节点
     *       因为 client 模式本身不会存储数据
     */
    private boolean clientMode = false;

    /**
     * 如果没有配置 ip 地址，默认该服务为 ignite 集群节点
     * 如果当前节点需要加入集群，则必须配置集群中节点 IP 地址
     * 可以配置集群中的一个或者多个 IP
     */
    private String[] addresses = {"127.0.0.1:47500..47509"};

    /**
     * Number of backups for cache.
     * 如果选择 {@link CacheMode#REPLICATED} 模式，则不需要配置这个选项
     * 后期可能会做变动，暂时暴露该变量
     */
    private int backups = CacheConfiguration.DFLT_BACKUPS;

    /**
     * 缓存模式一共三种 {@link CacheMode}
     * 考虑 session 的访问频率 和 容量小特点，采用 {@link CacheMode#REPLICATED} 模式
     * 不建议修改默认配置
     */
    private CacheMode cacheMode = CacheConfiguration.DFLT_CACHE_MODE;

    /**
     * 防止集群多节点的 session 缓存名称重复
     * 该名称不允许设置，一个集群一个 {@link IgniteSessionConfiguration#SESSION_CACHE_NAME}
     * fixed global session name in all of ignite nodes
     */
    private static final String SESSION_CACHE_NAME = "ignite_session_cache";

    /**
     * 采用 {@link TouchedExpiryPolicy} 策略
     * 对 创建时间、最后访问时间、最后更新时间 都有效
     * @return
     */
    private Factory<ExpiryPolicy> getExpiryPolicyFactory(){
        return TouchedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS,this.sessionTimeOut));
    }

    /**
     * 配置 CacheConfiguration
     * @return
     */
    private CacheConfiguration<String, ExpiringSession> cacheConfiguration(){
        CacheConfiguration<String, ExpiringSession> cfg = new CacheConfiguration<String, ExpiringSession>();
        cfg.setName(SESSION_CACHE_NAME);
        cfg.setBackups(this.backups);
        cfg.setCacheMode(this.cacheMode);
        cfg.setExpiryPolicyFactory(getExpiryPolicyFactory());
        return cfg;
    }

    /**
     * 配置 IgniteConfiguration
     * @return
     */
    private IgniteConfiguration igniteConfiguration(){
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setClientMode(this.clientMode);
        cfg.setCacheConfiguration(cacheConfiguration());
        cfg.setDiscoverySpi(DiscoverySpiUtil.getTcpDiscoverySpi(this.addresses));
        return cfg;
    }

    /**
     * 启动 ignite 节点
     * @return
     */
    private Ignite ignite;
    public Ignite getIgnite() {
		return ignite;
	}

	public void setIgnite(Ignite ignite) {
		this.ignite = ignite;
	}

	public Ignite ignite(){
    	this.ignite=Ignition.start(igniteConfiguration());
    	return ignite;
    }

    /**
     * 配置 SessionRepository
     * 由于结合 spring-session 机制，所以在应用程序中需要开启注解 {@link EnableSpringHttpSession}
     * @return
     */
    @Bean
    @Order(0)
    public SessionRepository<ExpiringSession> sessionRepository() {
        IgniteCache<String, ExpiringSession> igCache = ignite().cache(SESSION_CACHE_NAME);
        Map<String, ExpiringSession> igcMapView = new IgniteCacheMapView<String, ExpiringSession>(igCache);
        return new MapSessionRepository(igcMapView);
    }


    public int getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(int sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public boolean isClientMode() {
        return clientMode;
    }

    public void setClientMode(boolean clientMode) {
        this.clientMode = clientMode;
    }

    public String[] getAddresses() {
        return addresses;
    }

    public void setAddresses(String[] addresses) {
        this.addresses = addresses;
    }

    public int getBackups() {
        return backups;
    }

    public void setBackups(int backups) {
        this.backups = backups;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }
}
