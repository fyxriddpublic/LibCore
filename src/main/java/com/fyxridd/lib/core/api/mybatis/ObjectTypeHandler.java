package com.fyxridd.lib.core.api.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.*;
import java.sql.*;

/**
 * Mybatis的对象解析器
 */
public class ObjectTypeHandler extends BaseTypeHandler<Object> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object obj, JdbcType jdbcType) throws SQLException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            try {
                oos.writeObject(obj);

                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                try {
                    preparedStatement.setBinaryStream(i, bis);
                } finally {
                    bis.close();
                }
            } finally {
                oos.close();
            }
        } catch (IOException e) {
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return get(resultSet.getBlob(columnName));
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return get(resultSet.getBlob(columnIndex));
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return get(callableStatement.getBlob(columnIndex));
    }

    private Object get(Blob blob) throws SQLException {
        if (blob == null) return null;

        byte[] bytes = blob.getBytes(1, (int) blob.length());
        if (bytes != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            try {
                ObjectInputStream ois = new ObjectInputStream(bis);
                try {
                    return ois.readObject();
                } catch (Exception e) {
                } finally {
                    try {
                        ois.close();
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            } finally {
                try {
                    bis.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }
}
