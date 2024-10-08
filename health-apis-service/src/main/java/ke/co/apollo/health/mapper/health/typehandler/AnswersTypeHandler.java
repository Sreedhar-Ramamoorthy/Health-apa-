package ke.co.apollo.health.mapper.health.typehandler;

import com.google.common.reflect.TypeToken;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import ke.co.apollo.health.common.domain.model.Question.Answers;
import ke.co.apollo.health.common.utils.GsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class AnswersTypeHandler extends BaseTypeHandler<List<Answers>> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, List<Answers> parameter,
      JdbcType jdbcType) throws SQLException {
    ps.setString(i, GsonUtils.createDefaultGson().toJson(parameter));
  }

  @Override
  public List<Answers> getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    String data = rs.getString(columnName);
    return StringUtils.isBlank(data) ? null
        : GsonUtils.createGson().fromJson(data, new TypeToken<List<Answers>>() {
        }.getType());
  }

  @Override
  public List<Answers> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String data = rs.getString(columnIndex);
    return StringUtils.isBlank(data) ? null
        : GsonUtils.createGson().fromJson(data, new TypeToken<List<Answers>>() {
        }.getType());
  }

  @Override
  public List<Answers> getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    String data = cs.getString(columnIndex);
    return StringUtils.isBlank(data) ? null
        : GsonUtils.createGson().fromJson(data, new TypeToken<List<Answers>>() {
        }.getType());
  }
}
