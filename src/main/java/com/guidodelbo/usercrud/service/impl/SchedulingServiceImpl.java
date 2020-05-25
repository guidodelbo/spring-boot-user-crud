package com.guidodelbo.usercrud.service.impl;

import com.guidodelbo.usercrud.SpringApplicationContext;
import com.guidodelbo.usercrud.service.SchedulingService;
import com.guidodelbo.usercrud.shared.EmailScheduler;
import com.guidodelbo.usercrud.shared.dto.UserDto;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SchedulingServiceImpl implements SchedulingService {

    private RedissonClient redisson;
    private RScheduledExecutorService executorService;

    public SchedulingServiceImpl(SpringApplicationContext applicationContext) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        this.redisson = Redisson.create(config);
        
        ConfigurableListableBeanFactory beanFactory = ((AnnotationConfigServletWebServerApplicationContext) applicationContext.getContext()).getBeanFactory();

        WorkerOptions options = WorkerOptions.defaults()
                .workers(5)
                .beanFactory(beanFactory);

        this.executorService = redisson.getExecutorService("myExecutor");
        this.executorService.registerWorkers(options);
    }

    @Override
    public void scheduleEmail(UserDto userDto) {
        try {

            executorService.schedule(new EmailScheduler(userDto), 1, TimeUnit.MINUTES);

        } catch(Exception e) {
            //TODO: loggear exception
            System.out.println(e.getMessage());
        }
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


