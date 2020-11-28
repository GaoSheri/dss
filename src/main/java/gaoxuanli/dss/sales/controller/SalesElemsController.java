package gaoxuanli.dss.sales.controller;

import gaoxuanli.dss.sales.entity.SalesElems;
import gaoxuanli.dss.sales.service.SalesService;
import org.jfree.data.json.impl.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//重定向不应用RestController
@Controller
@RequestMapping("/sales")
public class SalesElemsController {

    @Autowired
    private SalesService salesService;

    @RequestMapping("/dataTable")
    // 销售数据表
    public String getOriData(Model model) {
        List<SalesElems> list = salesService.dataList();
        System.out.println(list.size());
        model.addAttribute("list", list);
        // 跳转到templates/dataTable.html
        return "dataTable";
    }

    @ResponseBody
    @GetMapping("/linear/onevar/{x}/{y}")
    // 一元线性回归预测模型
    public String oneVarLinearRegression(
            @PathVariable String x,
            @PathVariable String y
    ) {
        return (String) salesService.oneVarLinear(x, y);
    }

    @ResponseBody
    @GetMapping(value = "/linear/programming/ad")
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

}
