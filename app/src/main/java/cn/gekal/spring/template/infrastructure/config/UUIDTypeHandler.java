package cn.gekal.spring.template.infrastructure.config;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

/** Custom TypeHandler for converting between UUID and Long (for PostgreSQL's BIGSERIAL). */
@MappedTypes(UUID.class)
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType)
      throws SQLException {
    // Convert UUID to Long for database storage
    try {
      Long value = Long.parseLong(parameter.toString().replace("-", "").substring(0, 15), 16);
      ps.setLong(i, value);
    } catch (NumberFormatException e) {
      // If conversion fails, use a default value or handle the error appropriately
      ps.setLong(i, 0L);
    }
  }

  @Override
  public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Long value = rs.getLong(columnName);
    return rs.wasNull() ? null : convertToUUID(value);
  }

  @Override
  public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Long value = rs.getLong(columnIndex);
    return rs.wasNull() ? null : convertToUUID(value);
  }

  @Override
  public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Long value = cs.getLong(columnIndex);
    return cs.wasNull() ? null : convertToUUID(value);
  }

  private UUID convertToUUID(Long value) {
    // Convert Long to UUID
    String hexString = String.format("%015x", value);
    // Pad with zeros to make a valid UUID string
    String uuidString = hexString + "0000000000000000000";
    // Insert hyphens to make a valid UUID string
    uuidString =
        uuidString.replaceFirst(
            "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
            "$1-$2-$3-$4-$5");
    return UUID.fromString(uuidString);
  }
}
