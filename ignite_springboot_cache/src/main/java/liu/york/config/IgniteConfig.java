package liu.york.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.Arrays;

@Configuration
@ImportResource("classpath:ignite-spring.xml")
public class IgniteConfig {

//    private static final String IP = "127.0.0.1";
//    @Bean
//    public IgniteConfiguration igniteConfiguration(){
//        TcpDiscoverySpi spi = new TcpDiscoverySpi();
//        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
//        ipFinder.setAddresses(Arrays.asList(IP));
//        spi.setIpFinder(ipFinder);
//
//        IgniteConfiguration cfg = new IgniteConfiguration();
//        cfg.setDiscoverySpi(spi);
//        return cfg;
//    }
//
//
//    @Bean
//    public SpringCacheManager springCacheManager(IgniteConfiguration igniteConfiguration){
//        SpringCacheManager springCacheManager = new SpringCacheManager();
//        springCacheManager.setConfiguration(igniteConfiguration);
//        return springCacheManager;
//    }

}