package gaoxuanli.dss.sales.service;

import gaoxuanli.dss.sales.entity.SalesElems;

import java.io.File;
import java.util.List;

public interface SalesService {

    List<SalesElems> dataList();

    Object oneVarLinear(String x, String y);

    File getCurveChart(String x, String y);

    File getLinearChart(String formulaKey);

    void doKmeans(int k);
}
