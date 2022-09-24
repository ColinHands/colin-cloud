package com.springboot.cloud.common.core.util;

import com.springboot.cloud.common.core.exception.ServiceException;

public class Limiter {
    public static <T> T limit(BizProperty bizProperty, BizHandler<T> bizHandler) {

        String verifyRedisKey = bizProperty.key();
        String expireTimeStr = bizProperty.expireTime();
        // 判断是否超过最大允许次数
        if (true) {
            throw  new ServiceException();
        }
        T t = bizHandler.handle();
        // redis 次数加1
        return t;
    }

    public interface BizHandler<T> {
        T handle();
    }

    public interface BizProperty {
        default String expireTime() {
            return "";
        }

        String key();

        Integer limitTime();

        default String errorMessage() {
            return "";
        }
    }
}
