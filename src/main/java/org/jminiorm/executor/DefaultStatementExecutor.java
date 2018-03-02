package org.jminiorm.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jminiorm.IQueryTarget;
import org.jminiorm.exception.DBException;

/**
 * The default implementation of IStatementExecutor.
 */
public class DefaultStatementExecutor extends AbstractStatementExecutor {

    @Override
    public List<Long> executeUpdate(IQueryTarget target, String sql, List<List<Object>> params, String generatedColumn)
            throws DBException {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            List<Long> generatedKeys = new ArrayList<>();
            con = target.getConnection();
            if (generatedColumn != null)
                stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            else
                stmt = con.prepareStatement(sql);
            for (List<Object> curParams : params) {
                setParameters(stmt, curParams);
                stmt.executeUpdate();
                if (generatedColumn != null) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        rs.next();
                        generatedKeys.add(rs.getLong(1));
                    }
                }
            }
            return generatedKeys;
        } catch (SQLException e) {
            throw new DBException(e);
        } finally {
            try {
                if (con != null) target.releaseConnection(con);
            } catch (Exception e) {
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
            }
        }
    }

}
