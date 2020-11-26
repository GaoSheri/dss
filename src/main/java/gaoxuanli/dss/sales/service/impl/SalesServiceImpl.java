package gaoxuanli.dss.sales.service.impl;

import gaoxuanli.dss.sales.entity.SalesElems;
import gaoxuanli.dss.sales.service.SalesService;
import gaoxuanli.dss.sales.util.DSSChartUtil;
import gaoxuanli.dss.sales.util.ModelBaseUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SalesServiceImpl implements SalesService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private ModelBaseUtil modelBaseUtil;
    @Autowired
    private DSSChartUtil dssChartUtil;
    private static List<SalesElems> data;

    public List<SalesElems> getData() {
        return data;
    }
//
//    public SalesServiceImpl() {
//        modelBaseUtil = new ModelBaseUtil();
//        dssChartUtil = new DSSChartUtil();
//    }

    @Override
    public List<SalesElems> dataList() {
        if (data != null) {
            return data;
        }
        String sql = "select * from t_sales_elems";
        List<SalesElems> datalist = jdbcTemplate.query(sql, new SalesElems());
//        datalist.forEach(System.out::println);
        data = datalist;
        return datalist;
    }


    @Override
    public Object oneVarLinear(String x, String y) {
        Double[][] dots = getDots(x, y);
        String formula = modelBaseUtil.oneVarLinearRegressionModel(
                Arrays.asList(dots[0]), Arrays.asList(dots[1]), x, y);
        System.out.println("formula: " + formula);
        return formula;
    }

    // private
    @Override
    public Double[][] getDots(String x, String y) {
        // 若为空则初始化操作
        if (data == null) dataList();
        Double[][] dots = new Double[2][data.size()];
        // 输入（x, y）形成点集
        for (int i = 0; i < data.size(); i++) {
            dots[0][i] = data.get(i).getColumn(x);
            dots[1][i] = data.get(i).getColumn(y);
        }
        return dots;
    }

    @Override
    public List<Double> getColumnData(String c) {
        return modelBaseUtil.getColumnData(c);
    }

    @Override
    public File getCurveChart(String x, String y) {
        return dssChartUtil.getCurveChart(x, y);
    }

    @Override
    public File getLinearChart(String x, String y) {
        String formulaKey = x + "_" + y;
        System.out.println("getLinearChart: " + formulaKey);
        Double[][] dots = getDots(x, y);
        Map<String, Double> argsMap = modelBaseUtil.getArgs(formulaKey);
        System.out.println("a: " + argsMap.get("a") + ", b: " + argsMap.get("b"));
        return DSSChartUtil.getLinearChart(argsMap.get("a"), argsMap.get("b"), dots, x, y);
    }

    @Override
    public void doKmeans(int k) {
        modelBaseUtil.doKmeans(k);
    }
}
