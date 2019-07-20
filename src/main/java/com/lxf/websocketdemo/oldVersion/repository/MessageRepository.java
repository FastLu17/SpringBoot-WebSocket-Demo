package com.lxf.websocketdemo.oldVersion.repository;

import com.lxf.websocketdemo.oldVersion.entity.WebMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 小66
 * @create 2019-06-27 15:48
 **/

@Repository
public interface MessageRepository extends JpaRepository<WebMessage,Long> {

}
