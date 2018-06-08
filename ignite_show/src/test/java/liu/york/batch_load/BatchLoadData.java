package liu.york.batch_load;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class BatchLoadData {
    private static final int Num = 10_000_000;
    private static final String VALUE = "VALUE_FIX_SIZE";

    /**
     * origin : 46082 ms
     * stream : 13022 ms
     * @throws InterruptedException
     */
    @Test
    public void batchLoad() throws InterruptedException {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setLocalHost("127.0.0.1");

        Ignite ignite = Ignition.start(cfg);

        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("cache");
        // 预热 - 缓冲两秒
        warmUp(cache);

        IgniteDataStreamer<Object, Object> dataStreamer = ignite.dataStreamer("cache");
        dataStreamer.autoFlushFrequency(10_000);
        System.out.println("Add start ....");
        long startTime = System.currentTimeMillis();
        for (int i=0;i<Num;i++)
        {
//            cache.put(i,VALUE);
            dataStreamer.addData(i,VALUE);
        }
        System.out.println("Add Done.   spends ===> " + (System.currentTimeMillis() - startTime));

        TimeUnit.SECONDS.sleep(5);
        ignite.close();
    }

    private void warmUp(IgniteCache<Integer, String> cache) throws InterruptedException {
        for(int i=0;i<10_000;i++)
        {
            cache.put(i,"value");
        }
        cache.removeAll();
        TimeUnit.SECONDS.sleep(2);
    }
}