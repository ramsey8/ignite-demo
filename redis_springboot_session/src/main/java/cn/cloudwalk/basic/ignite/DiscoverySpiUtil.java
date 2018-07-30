package cn.cloudwalk.basic.ignite;

import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.util.Assert;

import java.util.Arrays;

public class DiscoverySpiUtil {

    /**
     * ignite 集群发现支持方式有
     *      1 组播方式 {@link TcpDiscoveryMulticastIpFinder}，考虑到多数服务器关闭组播功能，不支持该模式
     *      2 静态 IP {@link TcpDiscoveryVmIpFinder}
     *      3 组播 + 静态IP，考虑到多数服务器关闭组播功能，不支持该模式
     * 当前只提供 静态IP 方式发现模式
     * @param ips
     * @return
     */
    public static TcpDiscoverySpi getTcpDiscoverySpi(final String[] ips){
        Assert.isTrue(ips != null && ips.length > 0,"IP's address can not be null");
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryVmIpFinder();
        ipFinder.setAddresses(Arrays.asList(ips));
        spi.setIpFinder(ipFinder);
        return spi;
    }
}
