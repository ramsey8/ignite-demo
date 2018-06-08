package liu.york.cluster;

import liu.york.BaseConfigUtil;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GroupTest {
    @Test
    public void localServer() throws InterruptedException {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setDiscoverySpi(BaseConfigUtil.getTcpDiscoverySpi("127.0.0.1"));
        Map<String,String> userAttributes = new HashMap<>();
        userAttributes.put("ROLE","group1");
        cfg.setUserAttributes(userAttributes);

        Ignite ignite = Ignition.start(cfg);
        ClusterGroup group = ignite.cluster().forAttribute("ROLE","group1");
        Collection<ClusterNode> nodes = group.nodes();
        nodes.forEach(clusterNode -> System.out.println("group id =============> " + clusterNode.id()));

        Collection<ClusterNode> allGroup = ignite.cluster().nodes();
        allGroup.forEach(clusterNode -> System.out.println("all id ===============> " + clusterNode.id()));

        TimeUnit.SECONDS.sleep(3);
        ignite.close();
    }
}