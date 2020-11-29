package gaoxuanli.dss.sales.service.impl;

import gaoxuanli.dss.sales.entity.SalesElems;
import gaoxuanli.dss.sales.service.SalesService;
import gaoxuanli.dss.sales.util.DSSChartUtil;
import gaoxuanli.dss.sales.util.Equations;
import gaoxuanli.dss.sales.util.ModelBaseUtil;
import org.jfree.data.json.impl.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public String oneVarLinear(String x, String y) {
        Double[][] dots = getDots(x, y);
        String formula = modelBaseUtil.oneVarLinearRegressionModel(
                Arrays.asList(dots[0]), Arrays.asList(dots[1]), x, y);
        System.out.println("formula: " + formula);
        return formula;
    }

    // 获取由指定两列字段组成的点集
    private Double[][] getDots(String x, String y) {
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
    public JSONObject getPossibleSolution(
            List<Double> weight, Map<String, Double> limit,
            List<Map<List<Double>, String>> conditions) {
        Equations equations = new Equations(weight, limit, conditions);
        return equations.getResult();
    }

    @Override
    public double[] multiplyVarsLinear(String[] attrs, String y) {
        return modelBaseUtil.multiplyVarsLinearRegression(attrs, y);
    }

    @Override
    public double multiplyLinearPredict(double y_inc, String x_change) {
        return modelBaseUtil.multPredict(y_inc, x_change);
    }

    @Override
    public List<List<double[]>> kmeans(int k) {
        modelBaseUtil.doKmeans(k);
        // TODO: 返回JSONObject便于处理
        return null;
    }



    /*      生成图表功能      */

    @Override
    public File getPieChart() {
        // TODO: 【可选】将点集分类情况返回界面
        return DSSChartUtil.getPieChart(modelBaseUtil.getClusterCounts());
    }

    @Override
    public File getCurveChart(String x, String y) {
        return dssChartUtil.getCurveChart(x, y);
    }

    @Override
    public File getLinearChart(String x, String y) {
        String formulaKey = x + "_" + y;
        Double[][] dots = getDots(x, y);
        Map<String, Double> argsMap = modelBaseUtil.getArgs(formulaKey);
        System.out.println("getLinearChart: " + formulaKey);
        System.out.println("a: " + argsMap.get("a") + ", b: " + argsMap.get("b"));
        return DSSChartUtil.getLinearChart(argsMap.get("a"), argsMap.get("b"), dots, x, y);
    }
}
