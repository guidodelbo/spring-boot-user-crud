package com.guidodelbo.usercrud.service.impl;

import com.guidodelbo.usercrud.service.SchedulingService;
import com.guidodelbo.usercrud.shared.EmailScheduler;
import com.guidodelbo.usercrud.shared.dto.UserDto;
import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RScheduledFuture;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
public class SchedulingServiceImpl implements SchedulingService {

    private RedissonClient redisson;

    public SchedulingServiceImpl() {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        this.redisson = Redisson.create(config);

        RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
        nodeConfig.setExecutorServiceWorkers(Collections.singletonMap("myExecutor", 5));
        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();
    }

    @Override
    public void scheduleEmail(UserDto userDto) {

        RScheduledExecutorService executorService = redisson.getExecutorService("myExecutor");
        executorService.schedule(new EmailScheduler(userDto), 1, TimeUnit.MINUTES);
    }


/*
    // 1. Create config object
    Config config = new Config();
    config.useClusterServers()
        // use "rediss://" for SSL connection
        .addNodeAddress("redis://127.0.0.1:7181");

    // or read config from file
    config = Config.fromYAML(new File("config-file.yaml"));
    // 2. Create Redisson instance

    // Sync and Async API
    RedissonClient redisson = Redisson.create(config);

    // Reactive API
    RedissonReactiveClient redissonReactive = Redisson.createReactive(config);

    // RxJava2 API
    RedissonRxClient redissonRx = Redisson.createRx(config);
    // 3. Get Redis based Map
    RMap<MyKey, MyValue> map = redisson.getMap("myMap");

    RMapReactive<MyKey, MyValue> mapReactive = redissonReactive.getMap("myMap");

    RMapRx<MyKey, MyValue> mapRx = redissonRx.getMap("myMap");
    // 4. Get Redis based Lock
    RLock lock = redisson.getLock("myLock");

    RLockReactive lockReactive = redissonReactive.getLock("myLock");

    RLockRx lockRx = redissonRx.getLock("myLock");
    // 4. Get Redis based ExecutorService
    RExecutorService executor = redisson.getExecutorService("myExecutorService");

    // over 50 Redis based Java objects and services ...
*/

}


