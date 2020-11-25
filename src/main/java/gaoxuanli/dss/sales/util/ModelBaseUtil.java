package gaoxuanli.dss.sales.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelBaseUtil {
    // 模型库
    // 需求预测模型

    /**
     * oneVarLinearMap 格式如：
     * {
     *     "proceeds_ad": {
     *         "a": 123.456,
     *         "b": 123.456
     *     },
     *     "price_proceeds": {
     *         "a": 123.456,
     *         "b": 123.456
     *     }
     * }
     */
    public Map<String, Map<String, Double>> oneVarLinearMap = new HashMap<>();
    private Map<String, String> formulaMap = new HashMap<>();

    // 一元线性回归模型
    public String oneVarLinearRegressionModel(List<Double> varX, List<Double> varY, String xName, String yName) {
        String key = xName.concat("_").concat(yName);
        if (oneVarLinearMap.containsKey(key)) {
            return formulaMap.get(key);
        }
        double b = l(varX, varY) / l(varX, varX);
        double a = avr(varY) - (b * avr(varX));
        String formula = "y = " + a + "+" + b + "x";
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
}
