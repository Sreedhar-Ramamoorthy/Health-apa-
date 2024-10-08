package ke.co.apollo.health.policy.mapper.hms;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import ke.co.apollo.health.common.constants.GlobalConstant;
import ke.co.apollo.health.common.utils.HealthDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class DateTypeHandler extends BaseTypeHandler<Date> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setString(i, DateFormatUtils.format(parameter, GlobalConstant.YYYYMMDD_T_HHMMSS));
  }

  @Override
  public Date getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    String data = rs.getString(columnName);
    return StringUtils.isBlank(data) ? null
        : HealthDateUtils.parseDate(data, GlobalConstant.YYYYMMDD_T_HHMMSS);
  }

  @Override
  public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String data = rs.getString(columnIndex);
    return StringUtils.isBlank(data) ? null
        : HealthDateUtils.parseDate(data, GlobalConstant.YYYYMMDD_T_HHMMSS);
  }

  @Override
  public Date getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    String data = cs.getString(columnIndex);
    return StringUtils.isBlank(data) ? null
        : HealthDateUtils.parseDate(data, GlobalConstant.YYYYMMDD_T_HHMMSS);
  }
}
