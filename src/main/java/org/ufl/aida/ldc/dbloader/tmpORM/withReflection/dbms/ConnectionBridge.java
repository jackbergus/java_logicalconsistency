package org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms;

import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms.DBMSInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class ConnectionBridge {

    /**
     * Connects to a relational databaase
     * @param iface         Interface, providing an external configuration for a database access
     * @param database      Database name to which we want to access
     * @param username      Name of the user which is allowed to access the database
     * @param password      Password for the database
     * @return              If either the database exists or the datbase connection is up, or if the username/pw exists, then the connection is extablished and everything is returned
     */
    public static Optional<Connection> connect(DBMSInterface iface, String database, String username, String password) {
        try {
            return Optional.of(DriverManager.getConnection(iface.connectToDatabaseOnLocalhost(database),username,password));
        } catch (SQLException e) {
            //e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Connects to the default relational databaase
     * @param iface         Interface, providing an external configuration for a database access
     * @param username      Name of the user which is allowed to access the database
     * @param password      Password for the database
     * @return              If either the database exists or the datbase connection is up, or if the username/pw exists, then the connection is extablished and everything is returned
     */
    public static Optional<Connection> connect(DBMSInterface iface,  String username, String password) {
        return connect(iface, iface.defaultDatabaseName(), username, password);
    }

    /**
     * Loads the database driver from the DBMSInterface
     * @param iface
     * @return
     */
    public static Optional<Class> loadDatabaseClass(DBMSInterface iface) {
        try {
           return Optional.of(Class.forName(iface.driverClass()));
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            return Optional.empty();
        }
    }

}
