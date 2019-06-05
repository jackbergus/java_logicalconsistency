package org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms;


import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms.DBMSInterface;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.SQLType;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.UniqueIndex;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Public interface among different possible databases
 */
public class Database implements AutoCloseable {

    private final String name;
    private final Connection connection;
    private final DBMSInterface dialect;

    /**
     * Creates a database connection
     * @param name          Database name
     * @param connection    Java connection
     * @param dialect       Dialect associated to the RDBMS
     */
    private Database(String name, Connection connection, DBMSInterface dialect) {
        this.name = name;
        this.connection = connection;
        this.dialect = dialect;
    }

    /**
     * Returns the jOOQ query engine in java. It automatically generates the best query generator for the
     * current RDBMS dialect
     *
     * @return
     */
    public DSLContext jooq() {
        return DSL.using(connection, dialect.currentDialect());
    }

    /**
     * Returns all the tables contained within the current database
     * @return
     */
    public List<Table<?>> getTables() {
        return jooq().meta().getTables();
    }

    /**
     * Given a Class, it returns the table associated using the annotated information
     * @param clazz
     * @return
     */
    public Optional<Table<?>> getTable(Class<?> clazz) {
        String tableName = clazz.getDeclaredAnnotation(org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.Table.class).sqlTable();
        for (Table<?> table : jooq().meta().getTables()) {
            if (table.getName().equals(tableName)) {
                return Optional.of(table);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks whether the table annotated within the class exists
     * @param clazz
     * @return
     */
    public boolean tableExists(Class<?> clazz) {
        String tableName = clazz.getDeclaredAnnotation(org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.Table.class).sqlTable();
        return jooq().meta().getTables().stream().anyMatch(x -> x.getName().equals(tableName));
    }

    /**
     * This method inserts one single row
     * @param record
     * @param <T>       Object containing the table's information
     * @return          If the table is not present, returns null, and any other object otherwise.
     */
    public <T> InsertValuesStepN<?> insertInto(T record) {
        Optional<Table<?>> table = getTable(record.getClass());
        if (table.isPresent()) {
            // For each field ff = f[i] within the record
            Field[] f = record.getClass().getDeclaredFields();
            ArrayList<Object> values = new ArrayList<>();
            CreateTableColumnStep step = null;
            for (int i = 0; i<f.length; i++) {
                Field ff = f[i];
                ff.setAccessible(true);
                // if the field is annotated to be serialized into the relational database
                if (ff.isAnnotationPresent(SQLType.class)) {
                    try {
                        // add its value to the collection of elements
                        values.add(ff.get(record));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            // INSERT INTO record.class() ( .. fields ...) ( .. values ... )
            return jooq().insertInto(table.get(), table.get().fields()).values(values);
        }
        return null;
    }

    /**
     * Performs a batch insertion over a multitude of elements
     * @param recordIterator    Iterator over the records
     * @param limit             Batch insertion limit
     * @param <T>               Record's type
     * @return                  Number of insertions with success.
     */
    public <T> List<int[]> batchInsertion(Iterator<T> recordIterator, int limit) {
        if (limit <= 0)
            return Collections.emptyList();
        ArrayList<InsertValuesStepN<?>> batchInsertion = new ArrayList<>(limit);
        ArrayList<int[]> results = new ArrayList<>();

        while (recordIterator.hasNext()) {
            int count = batchInsertion.size();
            if (count >= limit) {
                // Performs the batch insertion to the database
                results.add(jooq().batch(batchInsertion).execute());
                // Remove all the previous insertions
                batchInsertion.clear();
            } else {
                // adding the number of insertions for the current command
                T element = recordIterator.next();
                if (element != null) batchInsertion.add(insertInto(element));
            }
        }
        if (!batchInsertion.isEmpty()) {                // Finalize the last insertion
            results.add(jooq().batch(batchInsertion).execute());
        }
        return results;
    }

    /**
     * Tries to create a table using the schema information that can be inferred from the class specification
     * @param clazz
     * @return
     */
    public boolean createTableFromClass(Class<?> clazz) {
        // CREATE TABLE clazz (
        CreateTableAsStep<Record> statement = jooq().createTableIfNotExists(clazz.getDeclaredAnnotation(org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.Table.class).sqlTable());
        Field[] f = clazz.getDeclaredFields();
        CreateTableColumnStep step = null;
        for (int i = 0; i<f.length; i++) {
            Field ff = f[i];
            ff.setAccessible(true);
            if (ff.isAnnotationPresent(SQLType.class)) {
                // fieldName fieldType
                if (step == null) {
                    step = statement.column(ff.getName(), dialect.getClassAssociatedToSQLType(ff.getAnnotation(SQLType.class).type()));
                } else {
                    step = step.column(ff.getName(), dialect.getClassAssociatedToSQLType(ff.getAnnotation(SQLType.class).type()));
                }
            }
        }
        // )
        try {
            step.execute();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates an unique index using the class annotations
     * @param clazz         Class through which obtain the annotations
     * @param indexName     Name of the index
     * @return
     */
    public boolean createUniqueIndex(Class<?> clazz, String indexName) {
        Field[] f = clazz.getDeclaredFields();
        // Collect all the elements that are annotated to belong to the unique index
        ArrayList<String> uniqueAnnotated = new ArrayList<>();
        for (int i = 0; i<f.length; i++) {
            Field ff = f[i];
            ff.setAccessible(true);
            if (ff.isAnnotationPresent(UniqueIndex.class)) {
                uniqueAnnotated.add(ff.getName());
            }
        }
        if (!uniqueAnnotated.isEmpty()) {
            String arrays[] = uniqueAnnotated.toArray(new String[uniqueAnnotated.size()]);
            // CREATE UNIQUE INDEX indexname ON clazz ( ... array ... )
            jooq().createUniqueIndex(indexName).on(clazz.getDeclaredAnnotation(org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.Table.class).sqlTable(), arrays).execute();
            return true;
        }
        return false;
    }

    /**
     * Opens an existing database
     * @param iface         Database interface
     * @param dbname        Database name
     * @param username      Database username
     * @param password      Database password
     * @return  If the connection was not established, it returns an empty object, and an instance of the Database otherwise
     */
    public static Optional<Database> open(DBMSInterface iface, String dbname, String username, String password) {
        if (dbname == null)
            dbname = iface.defaultDatabaseName();
        Optional<Connection> functConnection = ConnectionBridge.connect(iface, dbname, username, password);
        if (functConnection.isPresent())
            return Optional.of(new Database(dbname, functConnection.get(), iface));
        else
            return Optional.empty();
    }


    /**
     * Opens an existing database. If such database does not exist, it tries to create one.
     * @param iface         Database interface
     * @param dbname        Database name
     * @param username      Database username
     * @param password      Database password
     * @return  If the connection was not established or/and the database cannot be created, it returns an empty object,
     *          and an instance of the Database otherwise
     */
    public static Optional<Database> openOrCreate(DBMSInterface iface, String dbname, String username, String password) {
        Optional<Database> functConnection = open(iface, dbname, username, password);
        if (functConnection.isPresent())
            return functConnection;
        else {
            // Trying to create the new database
            Optional<Database> defaultDb = open(iface, null, username, password);
            if (!defaultDb.isPresent()) {
                return Optional.empty();
            } else {
                try {
                    Statement s = defaultDb.get().connection.createStatement();
                    s.executeUpdate("CREATE DATABASE " + dbname);
                    try {
                        defaultDb.get().close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return open(iface, dbname, username, password);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return Optional.empty();
                }
            }
        }
    }

    /**
     * Closes the database connection
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        connection.close();
    }

    /**
     * Executes a sql query from string directly using the connection (no jooq)
     * @param query
     */
    public ResultSet rawSqlQuery(String query) {
        try (Statement s = connection.createStatement()) {
            return s.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Runs a sql statement from string directly using the connection (no jooq)
     * @param query
     */
    public void rawSqlStatement(String query) {
            try (Statement s = connection.createStatement()) {
                s.execute(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * Runs a sql statement from file directly using the connection (no jooq)
     * @param file
     */
    public void rawSqlStatement(File file) {
        try {
            String query = new String(Files.readAllBytes(file.toPath()),UTF_8);
            rawSqlStatement(query);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
