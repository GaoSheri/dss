package gaoxuanli.dss.sales;

import gaoxuanli.dss.sales.service.SalesService;
import gaoxuanli.dss.sales.util.DSSChartUtil;
import gaoxuanli.dss.sales.util.ModelBaseUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class SalesApplicationTests {

    @Autowired
    SalesService salesService;
    DSSChartUtil dssChartUtil;

    @Test
    void contextLoads() {
//        salesService.dataList();
//        System.out.println(dssChartUtil.getLineChart("ad", "proceeds"));
        salesService.oneVarLinear("ad", "proceeds");
        salesService.doKmeans(3);

    }

}
