package Modulo_3.IntegratorPackage;

import Modulo_3.ConectionDB.DbConnection;
import Modulo_3.ConectionDB.DbResultSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IntegratorService {
    private static IntegratorService integratorDAO = null;
    private DbConnection conn;

    IntegratorService() {
        this.conn = conn;
    }

    public static IntegratorService getInstance() {
        if (integratorDAO == null)
            integratorDAO = new IntegratorService();
        return integratorDAO;
    }

    public List<Integrator> listIntegrator() {
        conn = new DbConnection();
        conn.connect();
        List<Integrator> integrators = new ArrayList<>();
        DbResultSet result = new DbResultSet();
        try {
            ResultSet rs = result.getResultSet(conn, "SELECT in_id, in_name, in_messageCost, in_threadCapacity, in_tokenApi" +
                    " FROM integrator");
            while (rs.next()) {
                IntegratorFactory factory = new IntegratorFactory();
                String integratorType = rs.getString("in_name");
                Integrator i = factory.getIntegrator(integratorType, rs.getInt("in_id"), rs.getString("in_name"), rs.getFloat("in_messageCost"),
                        rs.getInt("in_threadCapacity"), rs.getString("in_tokenApi"));
                integrators.add(i);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getStackTrace());
        }
        return integrators;
    }
}
