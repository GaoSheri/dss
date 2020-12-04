package gaoxuanli.dss.sales.controller;

import gaoxuanli.dss.sales.entity.SalesElems;
import gaoxuanli.dss.sales.service.SalesService;
import org.ejml.alg.dense.misc.ImplCommonOps_DenseMatrix64F;
import org.jfree.data.json.impl.JSONArray;
import org.jfree.data.json.impl.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//重定向不应用RestController
@Controller
public class SalesElemsController {

    @Autowired
    private SalesService salesService;

    @RequestMapping("/sales/dataTable")
    // 销售主页面
    public String getOriData(Model model) {
        List<SalesElems> list = salesService.dataList();
        System.out.println(list.size());
        model.addAttribute("list", list);
        // 跳转到templates/dataTable.html
        return "test";
    }

    @ResponseBody
    @GetMapping("/sales/linear/onevar/{x}/{y}")
    // 一元线性回归预测模型
    public String oneVarLinearRegression(
            @PathVariable String x,
            @PathVariable String y
    ) {
        return (String) salesService.oneVarLinear(x, y);
    }

    @ResponseBody
    @GetMapping(value = "/sales/linear/programming/ad")
    // 广告决策 - 线性规划模型
    public JSONObject adDecision(@RequestParam List<Double> weight,
                                 @RequestParam Map<String, Double> limit,
                                 @RequestParam List<Double> params,
                                 @RequestParam String signal) {
        List<Map<List<Double>, String>> conditions = new ArrayList<>();
        Map<List<Double>, String> cond = new HashMap<>();
        cond.put(params, signal);
        conditions.add(cond);
        return salesService.getPossibleSolution(weight, limit, conditions);
    }

    @RequestMapping(value = "/client")
    public String clientPage(Model model) {
        double[][] points = {
                // 客户号| 2008| 2009| 2010| 将来业务量（万元）
                {18.27, 50.48, 9.99, 78.74},
                {22.22, 60.77, 11.70, 94.70},
                {17.16, 45.09, 10.32, 72.56},
                {17.38, 48.45, 8.95, 74.78},
                {20.41, 56.86, 11.40, 88.67},
                {15.80, 41.34, 10.18, 67.32},
                {18.65, 52.80, 9.44, 80.89},
                {20.84, 59.99, 11.61, 92.44},
                {16.17, 49.18, 8.27, 73.62},
                {15.47, 42.56, 9.17, 67.20},
                {16.41, 50.33, 9.22, 75.96},
                {17.68, 41.51, 8.87, 68.06},
                {17.86, 52.02, 10.10, 79.98},
                {12.06, 26.66, 8.00, 46.72},
                {21.93, 61.04, 11.81, 94.78},
                {14.31, 39.54, 8.39, 62.24},
                {16.33, 36.94, 7.40, 60.67},
                {19.75, 52.16, 9.02, 80.93},
                {12.80, 34.92, 9.24, 56.96},
                {22.51, 59.31, 10.65, 92.47}
        };
        List<double[]> clientList = new ArrayList<>(Arrays.asList(points));
        model.addAttribute("clientList", clientList);
        return "client";
    }

    @ResponseBody
    @GetMapping("/multiplyLinear")
    public String multiplyLinear() {
        double[] b = salesService.multiplyVarsLinear(new String[]{"price", "ad", "carOutput"}, "proceeds");
        return "y = " + String.format("%.5f", b[0]) + " " + String.format("%.5f", b[1]) + "x1 +"
                + String.format("%.5f", b[2]) + "x2 +" + String.format("%.5f", b[3]) + "x3"
                + "<br>R^2 = 0.972";
    }

    @ResponseBody
    @GetMapping("/multiplyPredict")
    public String predict() {
        double x_ad = salesService.multiplyLinearPredict(26.53213, "ad");
        double x_price = salesService.multiplyLinearPredict(26.53213, "price");
        return "广告支出（ad）需达到 " + String.format("%.5f", x_ad) + "万元<br>"
                + "价格（price）需降至 " + String.format("%.5f", x_price) + "元";
    }

    @ResponseBody
    @GetMapping("/kmeans")
    public String kmeans(Model model) {
        List<List<double[]>> list = salesService.kmeans(4);
        model.addAttribute("clientCate", list);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append("【" + (i + 1) + "】" + list.get(i).size() + "<br>");
            for (int j = 0; j < list.get(i).size(); j++) {
                double[] d = list.get(i).get(j);
                Arrays.stream(d).forEach(dv -> sb.append(dv + " "));
                sb.append("<br>");
            }
            sb.append("<br>");
        }
        return sb.toString();
    }

    @GetMapping("sales/ad")
    public String ad(Model model) {
        Map<String, Double> limit = new HashMap<>();
        limit.put("1ge", 5.0);
        limit.put("2ge", 3.0);
        limit.put("3le", 2.0);
        Map<List<Double>, String> cond = new HashMap<>();
        cond.put(Arrays.asList(15.0, 2.4, 12.0, 100.0), "le");
        JSONObject json = salesService.getPossibleSolution(
                Arrays.asList(192.0, 36.0, 12.0), limit, Collections.singletonList(cond)
        );

        StringBuilder sb = new StringBuilder();
        List<Double> goals = (List<Double>)json.get("goals");
        List<List<Integer>> vars = (List<List<Integer>>)json.get("vars");
        model.addAttribute("best", json.get("best"));
        model.addAttribute("vars", vars);
        model.addAttribute("goals", goals);
        return "adDecision";
    }
}
