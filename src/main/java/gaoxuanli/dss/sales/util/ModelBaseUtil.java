package gaoxuanli.dss.sales.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kmeans.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class ModelBaseUtil {
    // 模型库
    // 需求预测模型

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * oneVarLinearMap 格式如：
     * {
     * "proceeds_ad": {
     * "a": 123.456,
     * "b": 123.456
     * },
     * "price_proceeds": {
     * "a": 123.456,
     * "b": 123.456
     * }
     * }
     */
    public Map<String, Map<String, Double>> oneVarLinearMap = new HashMap<>();
    private Map<String, String> formulaMap = new HashMap<>();

    public Map<String, Double> getArgs(String formulaKey) {
        if (formulaMap.containsKey(formulaKey)) {
            return oneVarLinearMap.get(formulaKey);
        }
        // 没有计算过该公式
        return null;
    }

    public List<Double> getColumnData(String column) {
        List<Double> list = new ArrayList<>();
        jdbcTemplate.query("select " + column + " from t_sales_elems",
                rs -> {
                    list.add(rs.getDouble(column));
                });
        return list;
    }

    // 一元线性回归模型
    public String oneVarLinearRegressionModel(List<Double> varX, List<Double> varY, String xName, String yName) {
        String key = xName.concat("_").concat(yName);
        if (oneVarLinearMap.containsKey(key)) {
            return formulaMap.get(key);
        }
        double b = l(varX, varY) / l(varX, varX);
        double a = avr(varY) - (b * avr(varX));
        String formula = "y = " + String.format("%.4f", a) + " + " + String.format("%.4f", b) + "x";
        formulaMap.put(key, formula);
        return formula;
    }

    // l_xx, l_xy, l_yy
    private Double l(List<Double> first, List<Double> second) {
        int n = first.size();
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            sum += first.get(i) * second.get(i);
        }
        return sum - (n * avr(first) * avr(second));
    }

    public Double avr(List<Double> var) {
        double sum = var.stream().mapToDouble(d -> d).sum();
        return sum / var.size();
    }

    // 预测
    // xValue 为代入公式的值
    public Double predict(String formulaKey, Double xValue) {
        if (!oneVarLinearMap.containsKey(formulaKey)) {
            // 需要先生成公式才可进行预测
            return -1.0;
        }
        double a = oneVarLinearMap.get(formulaKey).get("a");
        double b = oneVarLinearMap.get(formulaKey).get("b");
        return a + (b * xValue);
    }

    // 聚类
    /*
    客户号	2008年（万元）	2009年（万元）	2010年（万元）	将来业务量（万元）
    （序号）
     */
    public Map<String, List<Double>> doKmeans(int k) {
        double[][] points = {
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
        }; // 原始数据集
        kmeans_data data = new kmeans_data(points, 20, k);
        kmeans_param param = new kmeans_param();
        param.initCenterMehtod = kmeans_param.CENTER_RANDOM;

        // 划分为4类
        kmeans.doKmeans(k, data, param);

        // 输出结果
        System.out.println("The labels of points is:");
        for (int label : data.labels) {
            System.out.println(label + " ");
        }
        return null;
    }


}
