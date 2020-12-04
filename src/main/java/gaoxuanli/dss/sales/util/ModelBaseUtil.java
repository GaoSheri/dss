package gaoxuanli.dss.sales.util;

import gaoxuanli.dss.sales.entity.SalesElems;
import kmeans.kmeans;
import kmeans.kmeans_data;
import kmeans.kmeans_param;
import org.jfree.data.json.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/*
    模型库
 */
@Service
public class ModelBaseUtil {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;  // 数据库

    // 需求预测模型

    /**
     * oneVarLinearMap 格式如：
     * {
     * "proceeds_ad": { "a": 123.456, "b": 123.456 },
     * "price_proceeds": { "a": 123.456, "b": 123.456 }
     * }
     * formulaMap 格式如：
     * {
     * "proceeds_ad": "y = 12.3456 + 12.3456x",
     * "price_proceeds": "y = 21.3456 + 21.3456x"
     * }
     */
    public Map<String, Map<String, Double>> oneVarLinearMap = new HashMap<>();
    private Map<String, String> formulaMap = new HashMap<>();

    // 存储多元回归预测模型
    private double[] b_value;
    // 存储聚类结果
    private List<List<double[]>> clients;

    public List<List<double[]>> getClients() {
        return clients;
    }

    // 获取公式的参数值（如 a, b）
    public Map<String, Double> getArgs(String formulaKey) {
        if (formulaMap.containsKey(formulaKey)) {
            return oneVarLinearMap.get(formulaKey);
        }
        // 没有计算过该公式
        return null;
    }

    // 获取指定列数据列表
    public List<Double> getColumnData(String column) {
        List<Double> data = new ArrayList<>();
        List<SalesElems> datalist = jdbcTemplate.query("select * from t_sales_elems", new SalesElems());
        datalist.forEach(se -> data.add(se.getColumn(column)));
        return data;
    }


    // 一元线性回归模型
    public String oneVarLinearRegressionModel(List<Double> varX, List<Double> varY, String xName, String yName) {
        String key = xName.concat("_").concat(yName);
        if (oneVarLinearMap.containsKey(key)) {
            // 若曾计算过该公式，则无需重复计算
            return formulaMap.get(key);
        }
        double b = l(varX, varY) / l(varX, varX);
        double a = avr(varY) - (b * avr(varX));
        // 生成直观公式字符串返回
        String formula = "y = " + String.format("%.4f", a) + " + " + String.format("%.4f", b) + "x";
        // 存入公式 map
        formulaMap.put(key, formula);
        Map<String, Double> args = new HashMap<>();
        args.put("a", a);
        args.put("b", b);
        oneVarLinearMap.put(key, args);
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
        // 返回计算所得 yValue
        return a + (b * xValue);
    }


    // 多元线性回归模型
    public double[] multiplyVarsLinearRegression(String[] attrs, String y) {
        int dimension = attrs.length;
        List<SalesElems> datalist = jdbcTemplate.query("select * from t_sales_elems", new SalesElems());
        double[][] x_value = new double[datalist.size()][dimension];
        double[] y_value = new double[datalist.size()];

        for (int i = 0; i < datalist.size(); i++) {
            y_value[i] = datalist.get(i).getColumn(y);
            for (int j = 0; j < dimension; j++) {
                x_value[i][j] = datalist.get(i).getColumn(attrs[j]);
            } // 训练数据赋值
        }
        MultiplyLinearRegression multiplyLinearRegression =
                new MultiplyLinearRegression(x_value, y_value);
        b_value = multiplyLinearRegression.doCalculate();
        return getBValue();
    }

    // 多元线性回归预测
    public double multPredict(double y_inc, String x_change) {
        System.out.println("------- multiply linear predict -------");
        System.out.println("y_increase: " + y_inc);
        System.out.println("predict x: " + x_change);

        Map<String, String> map = new HashMap<>();
        SalesElems ori = jdbcTemplate.query(
                "select * from t_sales_elems where year='2010'",
                new SalesElems()).get(0);
        System.out.println("x[" + x_change + "] original value: " + ori.getColumn(x_change));

        // TODO: 更灵活方式
        List<String> attrs = Arrays.asList("price", "ad", "carOutput");
        int index = attrs.indexOf(x_change) + 1; // 对应 b[]
        Double y = ori.getProceeds() + y_inc;
        Double dv_b = b_value[index]; // 变量系数，除数
        double x_update = y - b_value[0]; // 便于计算
        for (int i = 1; i < b_value.length; i++) {
            if (i == index) continue;
            x_update -= b_value[i] * ori.getColumn(attrs.get(i - 1));
        } // (current) x_update = b[change] * x_change
        x_update /= dv_b;
        return x_update;
    }

    public double[] getBValue() {
        return b_value;
    }


    // 聚类
    public List<List<double[]>> doKmeans(int k) {
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
        }; // 原始数据集
        kmeans_data data = new kmeans_data(points, 20, k);
        kmeans_param param = new kmeans_param();
        param.initCenterMehtod = kmeans_param.CENTER_RANDOM;

        // 划分为 k 类
        kmeans.doKmeans(k, data, param);
        List<List<double[]>> labelList = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            labelList.add(new LinkedList<>());
        }
        // 按划分结果存入列表
        for (int i = 0; i < points.length; i++) {
            int label = data.labels[i];
            System.out.print(label + " ");
            labelList.get(label).add(points[i]);
        }

        System.out.println("\n\n------- cluster result:\n");
        labelList.forEach(label -> {
            label.forEach(point -> {
                Arrays.stream(point).forEach(d -> System.out.print(d + " "));
                System.out.println();
            });
            System.out.println("\n\n");
        });
        clients = labelList;
        return labelList;
    }

    public Integer[] getClusterCounts() {
        List<Integer> count = new ArrayList<>();
        clients.forEach(list -> count.add(list.size()));
        return count.toArray(new Integer[]{});
    }
}
