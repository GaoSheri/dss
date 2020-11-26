package gaoxuanli.dss.sales;

import gaoxuanli.dss.sales.service.SalesService;
import gaoxuanli.dss.sales.util.DSSChartUtil;
import gaoxuanli.dss.sales.util.ModelBaseUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;

@SpringBootTest
class SalesApplicationTests {

    @Autowired
    SalesService salesService;
    @Autowired
    DSSChartUtil dssChartUtil;

    @Test
    void contextLoads() {
//        salesService.dataList();
//        System.out.println(dssChartUtil.getLineChart("ad", "proceeds"));
        salesService.dataList().forEach(System.out::println);
        salesService.oneVarLinear("ad", "proceeds");
        salesService.doKmeans(4);
        salesService.getColumnData("proceeds").forEach(System.out::println);
        Arrays.stream(salesService.getDots("price", "proceeds"))
                .forEach(doubles -> System.out.println("x: " + doubles[0] + ", y: " + doubles[1]));

    }

}
