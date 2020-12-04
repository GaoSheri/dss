package gaoxuanli.dss.sales;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication

@ComponentScan(basePackages = "gaoxuanli.dss.*.service")
@ComponentScan(basePackages = "gaoxuanli.dss.*.controller")
@ComponentScan(basePackages = "gaoxuanli.dss.*.util")
@ComponentScan(basePackages = "gaoxuanli.dss.*.config")
public class SalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesApplication.class, args);
    }

}
