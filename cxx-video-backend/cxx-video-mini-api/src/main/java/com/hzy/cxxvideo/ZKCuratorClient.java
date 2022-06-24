package com.hzy.cxxvideo;

import com.hzy.cxxvideo.entity.Bgm;
import com.hzy.cxxvideo.service.BgmService;
import com.hzy.cxxvideo.utils.JSONUtils;
import org.apache.commons.io.FileUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @Program: cxx-video
 * @Package: com.hzy.cxxvideo
 * @Description:
 * @Author: hzy
 * @Date: 2021/11/28 10:34
 **/
public class ZKCuratorClient {
    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

    public final static String ZK_SERVER = "192.168.72.129";

    @Autowired
    private BgmService bgmService;

    public void init() {
        if(client != null) {
            return;
        }

        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 5);

        client = CuratorFrameworkFactory.builder().connectString(ZK_SERVER).
                sessionTimeoutMs(10000).connectionTimeoutMs(10000).retryPolicy(retry).namespace("admin").build();

        client.start();

        try {
            // 前面定义了admin命名空间，后面getdata的时候不用再加admin
//            byte[] bytes = client.getData().forPath("/bgm/2111287XKX9NYWM8");
//            String res = new String(bytes);
//            System.out.println(res);
            addChildWatcher("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChildWatcher(String node) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(client, node, true);
        // 这里要传参！
        cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
//        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {

            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event)
                    throws Exception {

                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {


                    //
                    System.out.println("监听到节点添加");
                    // 1.  从数据库查询bgm对象，获取路径path
                    String bgmId = event.getData().getPath();
                    String operateJsonStr = new String(event.getData().getData());
                    Map<String,String> map = JSONUtils.jsonToPojo(operateJsonStr, Map.class);
                    String operType = map.get("operType");
                    String path = map.get("operType");

                    String[] split = bgmId.split("/");
                    bgmId = split[split.length - 1];

                    Bgm bgm = bgmService.queryBgmIfo(bgmId);
                    // path saved in database


                    // 2. 定义保存到本地的path
                    String localPath = "D:\\cxx-video" + path;
                    // 3. 定义下载的路径
                    String mvcPath = "http://10.177.19.192:8081" + path;
                    String encode = URLEncoder.encode(mvcPath, "utf-8");

                    URL url = new URL(mvcPath);
                    File file = new File(localPath);
                    FileUtils.copyURLToFile(url,file);

                }
            }
        });

    }

}
