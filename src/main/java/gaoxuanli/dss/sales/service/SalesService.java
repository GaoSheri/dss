package gaoxuanli.dss.sales.service;

import gaoxuanli.dss.sales.entity.SalesElems;

import java.util.List;

public interface SalesService {

    List<SalesElems> dataList();

    Object oneVarLinear(String x, String y);
}
