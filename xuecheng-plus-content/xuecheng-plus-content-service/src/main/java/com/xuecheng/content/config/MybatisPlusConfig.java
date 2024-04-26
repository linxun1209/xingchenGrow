package com.xuecheng.content.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @author Xingchen
 * @version 1.0
 * @description TODO
 * @date 2023/2/12 9:23
 */
@Configuration
@MapperScan("com.xuecheng.content.mapper")
public class MybatisPlusConfig implements MetaObjectHandler {
 /**
  * 定义分页拦截器
  */
 @Bean
 public MybatisPlusInterceptor mybatisPlusInterceptor() {
  MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
  interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
  return interceptor;
 }


 @Override
 public void insertFill(MetaObject metaObject) {
  setFieldValByName("createDate", LocalDateTime.now(), metaObject);
  setFieldValByName("createTime", LocalDateTime.now(), metaObject);
  setFieldValByName("dealTime", LocalDateTime.now(), metaObject);
 }

 @Override
 public void updateFill(MetaObject metaObject) {
  setFieldValByName("changeDate", LocalDateTime.now(), metaObject);
 }

}
