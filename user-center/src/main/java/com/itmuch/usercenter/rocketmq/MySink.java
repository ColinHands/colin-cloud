package com.itmuch.usercenter.rocketmq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MySink {
    @Input("my-input")
    SubscribableChannel input();
}
