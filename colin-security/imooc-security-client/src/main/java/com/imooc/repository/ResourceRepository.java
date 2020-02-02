/**
 * 
 */
package com.imooc.repository;

import com.imooc.entity.Resource;
import org.springframework.stereotype.Repository;

/**
 * @author zhailiang
 *
 */
@Repository
public interface ResourceRepository extends ImoocRepository<Resource> {

	Resource findByName(String name);

}
