package liu.york.cluster_metrics;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterMetrics;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.junit.Test;
import sun.plugin2.gluegen.runtime.CPU;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ClusterMetricsTest {
    @Test
    public void metrics() throws InterruptedException {
        TcpDiscoverySpi tcpDiscoverySpi = BaseConfigUtil.getTcpDiscoverySpi("192.168.159.135");

        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(tcpDiscoverySpi);

        Ignite ignite = Ignition.start(cfg);

        Collection<ClusterNode> nodes = ignite.cluster().nodes();

        nodes.forEach(node -> {

            long heapTotal = node.metrics().getHeapMemoryTotal();
            long heapUsed = node.metrics().getHeapMemoryUsed();
            System.out.println("node:" + node.id() + "--------->" + node.metrics().getCurrentCpuLoad());
            System.out.println("node:" + node.id() + "--------->" + (heapTotal-heapUsed));

        });

        ClusterMetrics metrics = ignite.cluster().metrics();

        double currentCpuLoad = metrics.getCurrentCpuLoad();
        long heapMemoryUsed = metrics.getHeapMemoryUsed();

        System.out.println("cpu ===> " + currentCpuLoad);
        System.out.println("heap ===> " + heapMemoryUsed);

        TimeUnit.SECONDS.sleep(5);
    }
}