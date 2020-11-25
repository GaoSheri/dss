package gaoxuanli.dss.sales;

import gaoxuanli.dss.sales.service.SalesService;
import gaoxuanli.dss.sales.util.DSSChartUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SalesApplicationTests {

    @Autowired
    SalesService salesService;
    DSSChartUtil dssChartUtil;

    @Test
    void contextLoads() {
//        salesService.dataList();
        System.out.println(dssChartUtil.getChart("ad", "proceeds"));
    }

}
