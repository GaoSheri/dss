package gaoxuanli.dss.sales.service;

import gaoxuanli.dss.sales.entity.SalesElems;
import org.jfree.data.json.impl.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface SalesService {

    List<SalesElems> dataList();

    Object oneVarLinear(String x, String y);

    File getCurveChart(String x, String y);

    Double[][] getDots(String x, String y);

    List<Double> getColumnData(String c);

    File getLinearChart(String x, String y);

    JSONObject getPossibleSolution(
        List<Double> weight,
        Map<String, Double> limit,
        List<Map<List<Double>, String>> conditions);

    void doKmeans(int k);
}
