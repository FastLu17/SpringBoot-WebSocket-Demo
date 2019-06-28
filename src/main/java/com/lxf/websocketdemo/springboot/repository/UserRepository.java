package com.lxf.websocketdemo.springboot.repository;

import com.lxf.websocketdemo.springboot.entity.WebUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 小66
 * @create 2019-06-27 15:48
 **/

@Repository
public interface UserRepository extends JpaRepository<WebUser,Long> {

    WebUser findByUserName(String userName);
}
