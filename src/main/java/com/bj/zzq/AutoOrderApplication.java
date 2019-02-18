package com.bj.zzq;


import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/1/11
 * @Description:
 */
@MapperScan(basePackages = "com.bj.zzq.mapper")
@SpringBootApplication
public class AutoOrderApplication {
    private static final Logger logger = LoggerFactory.getLogger(AutoOrderApplication.class);

    public static void main(String[] args) {
        logger.info("\n\n" +
                "----------------------------------------------------------------\n" +
                "  " + " - 开始启动项目 ...\n" +
                "----------------------------------------------------------------\n");

        SpringApplication.run(AutoOrderApplication.class, args);

        logger.info("\n\n" +
                "----------------------------------------------------------------\n" +
                "  " + " - 项目启动成功 ! \n" +
                "----------------------------------------------------------------\n");
    }
}
