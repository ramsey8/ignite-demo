package liu.york.calculator;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class RemoteCalculator {
    private static final String IP = "192.168.159.135";

    /**
     * 两个 server 节点，计算字符个数
     * Note：本地节点如果是 client 模式，那么也会视为一个计算节点
     */
    @Test
    public void calculator(){
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi(IP);
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        cfg.setClientMode(true);
        // 必须开启等类加载
        cfg.setPeerClassLoadingEnabled(true);
        String str = "Count characters using callable"; // 28
        Collection<IgniteCallable<Integer>> calls = new ArrayList<>();

        String[] split = str.split(" ");
        for(String s : split)
        {
            calls.add(() ->
            {
                System.out.println("the string ===> " + s);
                int length = s.length();
                return length;
            });
        }

        Ignite ignite = Ignition.start(cfg);
        // ignite.cluster().forRemotes 集群中的远端节点
        Collection<Integer> call = ignite.compute(ignite.cluster().forRemotes()).call(calls);
        int sum = call.stream().mapToInt(Integer::intValue).sum();

        System.out.println("result ===================> " + sum);
        ignite.close();
    }

    @Test
    public void server() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi(IP);
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);
        // 必须开启等类加载
        cfg.setPeerClassLoadingEnabled(true);

        Ignite ignite = Ignition.start(cfg);

        TimeUnit.DAYS.sleep(1);
    }
}