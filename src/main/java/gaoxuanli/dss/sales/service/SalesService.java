package gaoxuanli.dss.sales.service;

import gaoxuanli.dss.sales.entity.SalesElems;
import org.jfree.data.json.impl.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface SalesService {

    // 完整数据集：查询自数据库
    List<SalesElems> dataList();

    // 获取某字段的取值列表
    List<Double> getColumnData(String c);

    // 计算一元线性回归并返回 [方程式] 字符串
    String oneVarLinear(String x, String y);

    // 计算多元线性回归并返回 [参数取值] 数组
    double[] multiplyVarsLinear(String[] attrs, String y);

    // 多元线性回归预测计算并返回 [指定变量值]
    double multiplyLinearPredict(double y_inc, String x_change);

    // 计算多元方程组的可能取值并返回 [可能取值] 集合 与 [最佳取值] 下标
    // 【注】 目前仅支持最大值
    // TODO: 支持最小值功能
    JSONObject getPossibleSolution(
        List<Double> weight,
        Map<String, Double> limit,
        List<Map<List<Double>, String>> conditions);

    // 进行 k-means 聚类算法并返回按聚类结果划分的 [分类点集] 列表
    List<List<double[]>> kmeans(int k);


    /*      生成图表功能      */

    // 生成指定的两字段的关系曲线图像
    File getCurveChart(String x, String y);

    // 生成指定的两字段的线性回归图像
    File getLinearChart(String x, String y);

    // 生成聚类形成的饼图
    File getPieChart();
}
