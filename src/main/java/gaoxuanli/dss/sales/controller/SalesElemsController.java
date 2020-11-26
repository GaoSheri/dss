package gaoxuanli.dss.sales.controller;

import gaoxuanli.dss.sales.entity.SalesElems;
import gaoxuanli.dss.sales.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

//重定向不应用RestController
@Controller
@RequestMapping("/sales")
public class SalesElemsController {

    @Autowired
    private SalesService salesService;

    @RequestMapping("/dataTable")
    public String getOriData(Model model) {
        List<SalesElems> list = salesService.dataList();
        System.out.println(list.size());
        model.addAttribute("list", list);
        // 跳转到templates/dataTable.html
        return "dataTable";
    }
    
    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @ResponseBody
    @GetMapping("/linear/onevar/{x}/{y}")
    public String oneVarLinearRegression(
            @PathVariable String x,
            @PathVariable String y
    ) {
        return (String)salesService.oneVarLinear(x, y);
    }



}
