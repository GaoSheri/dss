package gaoxuanli.dss.sales;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication

@MapperScan("gaoxuanli.dss.*.mapper")
@ComponentScan(basePackages = "gaoxuanli.dss.*.service")
@ComponentScan(basePackages = "gaoxuanli.dss.*.controller")
public class SalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesApplication.class, args);
    }

}
