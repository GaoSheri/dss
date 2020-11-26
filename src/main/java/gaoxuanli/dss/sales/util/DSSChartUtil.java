package gaoxuanli.dss.sales.util;

import gaoxuanli.dss.sales.entity.SalesElems;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class DSSChartUtil {

    ModelBaseUtil modelBaseUtil = new ModelBaseUtil();

    // 生成关系曲线图对象 JFreeChart 输出为图像并返回
    public File getCurveChart(String elem1, String elem2) {
        XYSeries series = new XYSeries(elem2.concat("-").concat(elem1));
        String sql = "select " + elem1 + "," + elem2 + " from t_sales_elems";

        List<Double> col1 = modelBaseUtil.getColumnData(elem1);
        List<Double> col2 = modelBaseUtil.getColumnData(elem2);
        for (int i = 0; i < col1.size(); i++) {
            series.add(col1.get(i), col2.get(i));
        }

        // title: 企业2001-2010年各年销售因素数据
        JFreeChart chart = ChartFactory.createXYLineChart(
                elem1 + "-" + elem2, elem1, elem2,
                new XYSeriesCollection(series));
        chart.getTitle().setFont(new Font("黑体", Font.BOLD, 18));
        return printPic(chart, "curve_" + elem1 + "_" + elem2);
    }

    // dots 第一维是 X 与 Y， 第二维是第几个点
    public static File getLinearChart(Double a, Double b, Double[][] dots, String x, String y) {
        double[][] innerDots = new double[2][dots[0].length];
        innerDots[0] = Arrays.stream(dots[0]).mapToDouble(d -> d).toArray();
        innerDots[1] = Arrays.stream(dots[1]).mapToDouble(d -> d).toArray();

        // 散点
        DefaultXYDataset dotsDataset = new DefaultXYDataset();
        dotsDataset.addSeries(x + "-" + y, innerDots);
        JFreeChart chart = ChartFactory.createScatterPlot(x + "_" + y, x, y, dotsDataset);

        // 线性方程
        Double lineXMin = Arrays.stream(innerDots[0]).min().getAsDouble();
        Double lineXMax = Arrays.stream(innerDots[0]).max().getAsDouble();
        Double lineYMin = b * lineXMin + a;
        Double lineYMax = b * lineXMax + a;
        DefaultXYDataset lineDataset = new DefaultXYDataset();
        lineDataset.addSeries("LinearFormula",
                new double[][]{{lineXMin, lineYMin}, {lineXMax, lineYMax}});


        XYPlot plot = chart.getXYPlot(); // 已有散点数据集的图本身
        plot.setDataset(1, lineDataset); // 添加折线图数据集
        return printPic(chart, "linear_" + x + "_" + y);
    }


    public static File printPic(JFreeChart chart, String filename) {
        FileOutputStream out = null;
        String path = "D:\\Workspace\\sales\\src\\main\\resources\\static\\pic\\chart_" + filename + ".png";
        File output = new File(path);
        try {
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
        return new File(path);
    }

}
