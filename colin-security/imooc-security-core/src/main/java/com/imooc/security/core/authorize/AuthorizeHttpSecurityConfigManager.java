/**
 *
 */
package com.imooc.security.core.authorize;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * 授权信息管理器
 *
 * 用于收集系统中所有 AuthorizeConfigProvider 并加载其配置
 *
 * @author 蔡勇刚
 *
 */
public interface AuthorizeHttpSecurityConfigManager {

    /**
     * @param http
     */
    void config(HttpSecurity http) throws Exception;

}
