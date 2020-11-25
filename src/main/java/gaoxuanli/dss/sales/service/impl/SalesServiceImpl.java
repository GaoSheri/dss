package gaoxuanli.dss.sales.service.impl;

import gaoxuanli.dss.sales.entity.SalesElems;
import gaoxuanli.dss.sales.service.SalesService;
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

@Service
public class SalesServiceImpl implements SalesService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    private ModelBaseUtil modelBaseUtil;
    private List<SalesElems> data;

    public SalesServiceImpl() {
        modelBaseUtil = new ModelBaseUtil();
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

            return null;
    }
}
