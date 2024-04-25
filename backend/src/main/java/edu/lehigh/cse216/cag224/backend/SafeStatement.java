package edu.lehigh.cse216.cag224.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SafeStatement {
    // Enum for the data types that can be passed into the sql safe statement
    public static enum Classes { Str, Int, Bool }

    private static class Param {
        Integer pos;
        Classes type;
        /**
         * Create a param instance
         * @param pos the position (1-n) in the prepared statement
         * @param type the datatype for the data meant for that spot
         */
        Param(Integer pos, Classes type){this.pos = pos; this.type = type;};
    }

    public static class TableMapping {
        String tableID;
        String tableName;
        /**
         * Creating a table mapping in case we need to change the tblNames (for testing for example)
         * @param tableID the table text to replace
         * @param tableName the new table text
         */
        TableMapping(String tableID, String tableName){
            this.tableID = tableID;
            this.tableName = tableName;
        }
    }

    private PreparedStatement statement;
    private String statementString;
    private HashMap<String, Param> params;
    private boolean hasResult;
    public static Connection conn;
    
    public static ArrayList<TableMapping> tableNameMapping = null;

    /**
     * Set the table mappings
     * @param mapping the table mapping to use
     */
    public static void setTableMapping(ArrayList<TableMapping> mapping){
        tableNameMapping = mapping;
    }

    /**
     * Create a safe table statement. It is recommended to look in SQL___.java at examples of use. Or contact ethan! He is too lazy to write his whole explanation here...
     * @param sql the SQL statement to pass in.
     * @param hasResult if the sql statement returns data
     * @throws SQLException if we have an error parsing the statement
     */
    public SafeStatement(String sql, boolean hasResult) throws SQLException {
        // Check we aren't using prepared statement syntax
        if (sql.contains("?")) throw new SQLException("Prefer use of {type,name} over ? in SafeStatement");
        // A hashmap of params based on the identifier
        this.params = new HashMap<String, Param>();
        // The hasResult data member
        this.hasResult = hasResult;
        // Start parsing at the first {
        int cursor = sql.indexOf("{", 0);
        int pos = 1;
        while (cursor != -1){
            // Parse from '{' to the '}'
            int end = sql.indexOf("}", cursor);
            if (end == -1) throw new SQLException("Can't parse SQL SafeStatement");
            // Get the data and split on ,
            String data = sql.substring(cursor+1, end).replace(" ", "");
            String[] pair = data.split(",");
            // Check only one comma
            if (pair.length != 2) throw new SQLException("Invalid number of arguments provided.");
            // Make sure we haven't used that identifier yet
            if (params.containsKey(pair[1].toLowerCase())) throw new SQLException("Cannot create a statement with multiple of the same identifiers.");
            // Get the chosen class based on what the user writes (either str, int, or bool)
            Classes chosenClass = Classes.valueOf(pair[0].substring(0, 1).toUpperCase() + pair[0].substring(1).toLowerCase());
            // Put the datatype into the hashmap
            params.put(pair[1].toLowerCase(), new Param(pos, chosenClass));
            // Increment position
            pos++;
            // Update cursor
            cursor = sql.indexOf("{", cursor+1);
        }
        
        // Regex to remove {} and replace with ?
        String sqlStatementFixed = sql.replaceAll("\\{[^,]*,[^}]*\\}", "?");
        // replace all instances of the tableid with the tablename. We want to make sure we are using test tables where applicable
        boolean found = false;
        for (TableMapping tableMapping : tableNameMapping) {
            if (!found) found = sqlStatementFixed.contains(tableMapping.tableID);
            sqlStatementFixed = sqlStatementFixed.replace(tableMapping.tableID, tableMapping.tableName);
        }
        // Make sure we made replacements
        if (!found) throw new SQLException("Expected to find and replace a table name");
        // Save the statement string for testing
        this.statementString = sqlStatementFixed;
        // Prepare the statement
        this.statement = conn.prepareStatement(sqlStatementFixed); 
    }

    /**
     * Execute the query with the prepared values
     * @return the result set if the statement has a result. Otherwise will return null.
     * @throws SQLException
     */
    public ResultSet executeQuery() throws SQLException {
        if (hasResult){
            return statement.executeQuery();
        } else {
            statement.executeUpdate();
            return null;
        }
    }

    /**
     * Set string data in the statement
     * @param paramName the identifier for the param
     * @param paramData the data to replace
     * @return an instance of itself, for chaining purposes! (I personally value this ability because it makes it WAY easier to write the code)
     * @throws SQLException if there are errors, will give a descript error message
     */
    public SafeStatement setString(String paramName, String paramData) throws SQLException {
        if (!params.containsKey(paramName.toLowerCase())) throw new SQLException(String.format("Param name %s not in the list of arguments", paramName));
        Param p = params.get(paramName.toLowerCase());
        if (p.type != Classes.Str) throw new SQLException(String.format("Cannot set String param name %s, we expect a %s", paramName, p.type.toString()));
        statement.setString(p.pos, paramData);
        return this;
    }

    /**
     * Set int data in the statement
     * @param paramName the identifier for the param
     * @param paramData the data to replace
     * @return an instance of itself, for chaining purposes! (I personally value this ability because it makes it WAY easier to write the code)
     * @throws SQLException if there are errors, will give a descript error message
     */
    public SafeStatement setInt(String paramName, Integer paramData) throws SQLException {
        if (!params.containsKey(paramName.toLowerCase())) throw new SQLException(String.format("Param name %s not in the list of arguments", paramName));
        Param p = params.get(paramName.toLowerCase());
        if (p.type != Classes.Int) throw new SQLException(String.format("Cannot set Int param name %s, we expect a %s", paramName, p.type.toString()));
        statement.setInt(p.pos, paramData);
        return this;
    }

    /**
     * Set boolean data in the statement
     * @param paramName the identifier for the param
     * @param paramData the data to replace
     * @return an instance of itself, for chaining purposes! (I personally value this ability because it makes it WAY easier to write the code)
     * @throws SQLException if there are errors, will give a descript error message
     */
    public SafeStatement setBool(String paramName, Boolean paramData) throws SQLException {
        if (!params.containsKey(paramName.toLowerCase())) throw new SQLException(String.format("Param name %s not in the list of arguments", paramName));
        Param p = params.get(paramName.toLowerCase());
        if (p.type != Classes.Bool) throw new SQLException(String.format("Cannot set Bool param name %s, we expect a %s", paramName, p.type.toString()));
        statement.setBoolean(p.pos, paramData);
        return this;
    }

    /**
     * Will return the statement string.
     * @return The statement currently constructed (without replaced values cuz again I'm lazy and not an essential refactor)
     */
    @Override
    public String toString(){
        return statementString;
    }
}