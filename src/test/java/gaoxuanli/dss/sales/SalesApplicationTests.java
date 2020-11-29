package gaoxuanli.dss.sales;

import gaoxuanli.dss.sales.service.SalesService;
import gaoxuanli.dss.sales.util.DSSChartUtil;
import gaoxuanli.dss.sales.util.ModelBaseUtil;
import gaoxuanli.dss.sales.util.MultiplyLinearRegression;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Array;
import java.util.*;

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
//        salesService.dataList().forEach(System.out::println);
//        salesService.oneVarLinear("ad", "proceeds");
//        salesService.getColumnData("proceeds").forEach(System.out::println);

        // cluster【done】
//        salesService.kmeans(4);

        // multiply linear regression calculate【done】
//        String[] attrs = {"price", "ad", "carOutput"};
//        String y = "proceeds";
//        System.out.println(Arrays.toString(salesService.multiplyVarsLinear(attrs, y)));

        // multiply linear predict【done】
        // 前提: 已经将 B 计算完毕
//        double y_inc = 265.32130 * 0.1; // 销量增长10%
//        double x_result = salesService.multiplyLinearPredict(y_inc, "ad");
//        System.out.println("x_result: " + x_result);
//        // x_result: 135.20802946584135
//        x_result = salesService.multiplyLinearPredict(y_inc, "price");
//        System.out.println("x_result: " + x_result);
//        // x_result: -45.61978086830522

        // ad decision test【done】
//        Map<String, Double> limit = new HashMap<>();
//        limit.put("1ge", 5.0);
//        limit.put("2ge", 3.0);
//        limit.put("3le", 2.0);
//        Map<List<Double>, String> cond = new HashMap<>();
//        cond.put(Arrays.asList(15.0, 2.4, 12.0, 100.0), "le");
//
//        System.out.println(salesService.getPossibleSolution(
//                Arrays.asList(192.0, 36.0, 12.0),
//                limit,
//                Collections.singletonList(cond))
//        );
    }

}
