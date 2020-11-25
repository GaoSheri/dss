package gaoxuanli.dss.sales.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Entity
@Table("t_sales_elems")
public class SalesElems implements RowMapper<SalesElems>, Serializable {

    @Id
    @GeneratedValue
    Integer id;

    Double proceeds;

    Double price;

    Double ad;

    Double carOutput;

    String year;

    @Override
    public SalesElems mapRow(ResultSet rs, int rowNum) throws SQLException {
        SalesElems salesElems = new SalesElems();
        salesElems.setId(rs.getInt("id"));
        salesElems.setAd(rs.getDouble("ad"));
        salesElems.setPrice(rs.getDouble("price"));
        salesElems.setProceeds(rs.getDouble("proceeds"));
        salesElems.setCarOutput(rs.getDouble("car_output"));
        salesElems.setYear(rs.getString("year"));
        return salesElems;
    }

    public Double getColumn(String column) {
        switch (column) {
            case "proceeds": return getProceeds();
            case "price": return getPrice();
            case "ad": return getAd();
            case "carOutput": return getCarOutput();
            default: return 0.0;
        }
    }
}
