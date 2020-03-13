package com.itmuch.contentcenter;

import com.itmuch.contentcenter.rocketmq.MySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.alibaba.sentinel.annotation.SentinelRestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.Collections;

// 扫描mybatis哪些包里面的接口
@MapperScan("com.itmuch.contentcenter.dao")
@SpringBootApplication
@EnableFeignClients// (defaultConfiguration = GlobalFeignConfiguration.class)
// 整合SpringCloudStream
@EnableBinding({Source.class, MySource.class})
@EnableResourceServer
// 启动@PreAuthorize("#oauth2.hasScope('fly')")功能 可以在方法执行之前或者之后可以用注解插入一些安全的表达式
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }

    // 在spring容器中，创建一个对象，类型RestTemplate；名称/ID是：restTemplate
    // <bean id="restTemplate" class="xxx.RestTemplate"/>
    @Bean
    @LoadBalanced
    @SentinelRestTemplate
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        template.setInterceptors(
            Collections.singletonList(
                new TestRestTemplateTokenRelayInterceptor()
            )
        );
        return template;
    }
}
