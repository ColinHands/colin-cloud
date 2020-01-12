package com.itmuch.usercenter.rocketmq;

import com.alibaba.fastjson.JSON;
import com.itmuch.usercenter.domain.dto.messaging.UserAddBonusMsgDTO;
import com.itmuch.usercenter.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MyTestStreamConsumer {
    private final UserService userService;

    @StreamListener("my-input")
    public void receive(String message) {
        log.info("hhhhh{}", JSON.toJSONString(message));
    }
}
