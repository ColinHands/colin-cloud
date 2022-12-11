/**
 * 
 */
package com.imooc.service;

import com.imooc.dto.AdminCondition;
import com.imooc.dto.AdminInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 管理员服务
 * 
 * @author zhailiang
 *
 */
public interface AdminService {

	/**
	 * 创建管理员
	 * @param adminInfo
	 * @return
	 */
	AdminInfo create(AdminInfo adminInfo);
	/**
	 * 修改管理员
	 * @param adminInfo
	 * @return
	 */
	AdminInfo update(AdminInfo adminInfo);
	/**
	 * 删除管理员
	 * @param id
	 */
	void delete(Long id);
	/**
	 * 获取管理员详细信息
	 * @param id
	 * @return
	 */
	AdminInfo getInfo(Long id);

	AdminInfo getInfo(String userName);
	/**
	 * 分页查询管理员
	 * @param condition
	 * @return
	 */
	Page<AdminInfo> 	query(AdminCondition condition, Pageable pageable);

}
