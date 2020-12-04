package gaoxuanli.dss.sales.controller;

import gaoxuanli.dss.sales.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/image")
public class ImageController {

    @Autowired
    SalesService salesService;

    @ResponseBody
    @GetMapping(value = "/linear/{x}/{y}", produces = MediaType.IMAGE_PNG_VALUE)
    // 一元线性回归 生成图
    public BufferedImage getLinearChartPic(
            @PathVariable String x,
            @PathVariable String y
    ) throws IOException {
        System.out.println("x: " + x + ", y: " + y);
        try(InputStream is = new FileInputStream(
                salesService.getLinearChart(x, y))) {
            return ImageIO.read(is);
        }
    }

    @ResponseBody
    @GetMapping(value = "/curve/{x}/{y}", produces = MediaType.IMAGE_PNG_VALUE)
    // 二元关系曲线 生成图
    public BufferedImage getCurveChartPic(
            @PathVariable String x,
            @PathVariable String y
    ) throws IOException {
        return ImageIO.read(salesService.getCurveChart(x, y));
    }
//
    @ResponseBody
    @GetMapping(value = "/pie", produces = MediaType.IMAGE_PNG_VALUE)
    public BufferedImage getPirChartPic() throws IOException {
        return ImageIO.read(salesService.getPieChart());
    }
}
