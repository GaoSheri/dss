package gaoxuanli.dss.sales.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

public class DSSChartUtil {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Object getChart(String elem1, String elem2) {

        XYSeries series = new XYSeries(elem2.concat("-").concat(elem1));
        String sql = "select " + elem1 + "," + elem2 + " from t_sales_elems";

        jdbcTemplate.query(sql, rs -> {
            series.add(rs.getDouble(elem1), rs.getDouble(elem2));
        });

        JFreeChart chart = ChartFactory.createXYLineChart(
                "企业2001-2010年各年销售因素数据: " + elem1 + "-" + elem2,
                elem1, elem2, new XYSeriesCollection(series));
        chart.getTitle().setFont(new Font("黑体", Font.BOLD, 18));

        // 输出为文件
        FileOutputStream out = null;
        String path = "D:\\jfreetest\\output\\chart" + elem1 + "-" + elem2 + ".png";
        try {
            File output = new File(path);
            if (!output.getParentFile().exists()) {
                output.getParentFile().mkdirs();
            }
            out = new FileOutputStream(path);
            ChartUtils.writeChartAsPNG(out, chart, 500, 500);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
        return chart;
    }
}
