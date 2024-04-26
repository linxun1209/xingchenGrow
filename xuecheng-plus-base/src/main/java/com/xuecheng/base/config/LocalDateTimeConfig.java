//package com.xuecheng.base.config;
//
//import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
//import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Date;
//import java.time.format.DateTimeFormatter;
//
//@Configuration
//public class LocalDateTimeConfig {
//
//    /*
//     * 序列化内容
//     *   Date -> String
//     * 服务端返回给客户端内容
//     * */
//    @Bean
//    public LocalDateTimeSerializer localDateTimeSerializer() {
//        return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//    }
//
//    /*
//     * 反序列化内容
//     *   String -> Date
//     * 客户端传入服务端数据
//     * */
//    @Bean
//    public LocalDateTimeDeserializer localDateTimeDeserializer() {
//        return new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//    }
//
//
//    // 配置
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//        return builder -> {
//            builder.serializerByType(Date.class, localDateTimeSerializer());
//            builder.deserializerByType(Date.class, localDateTimeDeserializer());
//        };
//    }
//
//}