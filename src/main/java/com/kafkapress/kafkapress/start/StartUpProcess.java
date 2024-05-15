package com.kafkapress.kafkapress.start;

import com.kafkapress.kafkapress.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class StartUpProcess implements ApplicationListener<ContextRefreshedEvent> {
    private KafkaConfig kafkaConfig;
    private KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    public StartUpProcess(KafkaConfig kafkaConfig,KafkaTemplate<String,String> kafkaTemplate){
        this.kafkaConfig=kafkaConfig;
        this.kafkaTemplate=kafkaTemplate;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("开始执行压测kafka程序,当前配置的发送主题为:[{}],发送线程数为:[{}],发送数据大小为:[{}],每个线程循环次数为:[{}]",kafkaConfig.getTopic(),kafkaConfig.getThreads(),kafkaConfig.getData().getBytes().length,kafkaConfig.getNums());
        long outStart=System.currentTimeMillis();
        List<CompletableFuture<Double>> list=new ArrayList<>();
        for(int i=0;i<kafkaConfig.getThreads();i++){

            CompletableFuture<Double> doubleCompletableFuture = CompletableFuture.supplyAsync(() -> {
                long start = System.currentTimeMillis();
                for(int j=0;j<kafkaConfig.getNums();j++){
                    kafkaTemplate.send(kafkaConfig.getTopic(), UUID.randomUUID().toString().substring(0,5),kafkaConfig.getData());
                }
                long end = System.currentTimeMillis();

                double speed = (kafkaConfig.getData().getBytes().length*kafkaConfig.getNums()) / ((end - start) / 1000);
                return speed;
            });
            list.add(doubleCompletableFuture);
        }
        AtomicReference<Double> avgdouble= new AtomicReference<>((double) 0);
        list.forEach(doubleCompletableFuture -> {
            try {
                Double aDouble = doubleCompletableFuture.get();
                avgdouble.updateAndGet(v -> new Double((double) (v + aDouble)));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        long outEnd = System.currentTimeMillis();

        log.info("写入完成共耗时:[{}]ms,平均写入速率为:[{}],",outEnd-outStart,avgdouble.get()/kafkaConfig.getThreads());
    }
}
