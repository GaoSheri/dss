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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SalesServiceImpl implements SalesService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private ModelBaseUtil modelBaseUtil;
    private DSSChartUtil dssChartUtil;
    private List<SalesElems> data;

    public SalesServiceImpl() {
        modelBaseUtil = new ModelBaseUtil();
        dssChartUtil = new DSSChartUtil();
    }

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
        List<Double> xVar = new ArrayList<>();
        List<Double> yVar = new ArrayList<>();
        // 若为空则初始化操作
        if (data == null) dataList();
        // 初始化 x 和 y 的数据集
        data.forEach(se -> {
            xVar.add(se.getColumn(x));
            yVar.add(se.getColumn(y));
        });

        String formula = modelBaseUtil.oneVarLinearRegressionModel(xVar, yVar, x, y);
        System.out.println("formula: " + formula);
        return formula;
    }

    @Override
    public File getCurveChart(String x, String y) {
        return dssChartUtil.getCurveChart(x, y);
    }

    @Override
    public File getLinearChart(String formulaKey) {
        Matcher m = Pattern.compile("(.*)_(.*)").matcher(formulaKey);
        String x = m.group(1), y = m.group(2);
        Double[][] dots = new Double[][]{
                modelBaseUtil.getColumnData(x).toArray(new Double[]{}),
                modelBaseUtil.getColumnData(y).toArray(new Double[]{})
        };
        Map<String, Double> argsMap = modelBaseUtil.getArgs(formulaKey);
        return DSSChartUtil.getLinearChart(argsMap.get("a"), argsMap.get("b"), dots, x, y);
    }

    @Override
    public void doKmeans(int k) {
        modelBaseUtil.doKmeans(k);
    }
}
