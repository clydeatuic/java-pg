package com.jdbcpg.todos;

public class PG {
    //Static Variables
    static java.sql.Connection conn  = null;
    static java.sql.Statement stmt = null;
    static java.sql.ResultSet rs = null;
    static java.sql.PreparedStatement pstmt = null;
    static String url = "";
    static String username = "";
    static String password = "";
    static String className = "";
    static String error = "";
    
    //Open DB Session Method
    public static boolean openDB(){
        boolean result = false;
        try{
            Class.forName(className);
            conn = java.sql.DriverManager.getConnection(url,username,password);
            
            //System.out.println("Open DB Success: DB session connected.");
            result = true;
        }
        catch(Exception e){
            error = e.getMessage();
            System.out.println("Open DB Failed: " + e.getMessage());
        } 
        return result;
    }

    //Close DB Session Method
    public static boolean closeDB(){
        boolean result = false;
        try{
            conn.close();

            System.out.println("Session has been executed successfully!");
            result = true;
        }
        catch(Exception e){
            error = e.getMessage();
            System.out.println("Close DB Failed: " + e.getMessage());
        }
        return result;
    }
    
    //Method for INSERT, UPDATE and DELETE statements
    public static boolean executeDML(String query){
        boolean result = false;
        try{
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
            result = true;
        }
        catch(Exception e){
            error = e.getMessage();
            System.out.println("Execute DML Error: " + e.getMessage());
            System.out.println("Query: " + query);
        }
        return result;
    }
    
    //Overloaded Method for INSERT, UPDATE and DELETE statments
    public static boolean executeDML(String query, Object[] values){
        boolean result = false;
        if(openDB()){
            try{
                pstmt = null;
                pstmt = conn.prepareStatement(query);
                for(int i=1,j=0;i<=values.length;i++,j++){
                    pstmt.setObject(i, values[j]);
                }
                pstmt.executeUpdate();
                result = true;
            }
            catch(Exception e){
                error = e.getMessage();
                System.out.println("Execute DML Error: " + e.getMessage());
                System.out.println("Query: " + query);
            }
            closeDB();
        }
        return result;
    }    

    // Method for SELECT statements
    public static String[][] executeDQL(String table, String[] columns, String where){
        String[][] records = null;
        if(openDB()){
            try{
                String[] w = where.split(";");
                
                stmt = conn.createStatement();
                
                //Count total rows
                rs = stmt.executeQuery("SELECT count(*) FROM " + table + " WHERE " + w[0]);
                int totalRows = 0;
                if(rs.next())totalRows = rs.getInt(1);
                
                //Format columns
                String cols = "";
                for (int i=0;i<columns.length;i++) {
                    cols += columns[i];
                    if((i+1)!=columns.length)cols+=", ";
                }
                
                //Execute DML Query
                rs = stmt.executeQuery("SELECT "+ cols +" FROM " + table + " WHERE " + w[0]);
                java.sql.ResultSetMetaData rsmd = rs.getMetaData();
                
                //Count total columns
                int totalColumns = rsmd.getColumnCount();
                
                //Initialize 2D Array "records" with totalRows by totalColumns
                records = new String[totalRows][totalColumns];
                
                //Retrieve the record and store it to 2D Array "records"
                int row=0;
                while(rs.next()){                
                    for(int col=0,index=1;col<totalColumns;col++,index++){
                        records[row][col] = rs.getObject(index).toString();
                    }
                    row++;
                }
            }
            catch(Exception e){
                error = e.getMessage();
                System.out.println("Execute DQL Error: " + e.getMessage());
            }
            closeDB();
        }
        return records;
    } 
    
    //Demo Method
    public static void main(String [] args){
        /*
        //Sample code snippet for INSERT DML Statement
        Object[] tasks = {"Task D", "NO"};
        String query = "INSERT INTO task (name, isdone) VALUES (?,?)";
        if(PG.executeDML(query, tasks)) System.out.println("Query Ok!");
        else System.out.println("Query Not Ok!");
        */
        
        /*
        //Sample code snippet for UPDATE DML Statement
        Object[] values = {"Task B", "YES", 13};
        String query = "UPDATE task SET name=? ,isdone=? WHERE id=?";
        if(PG.executeDML(query, values)) System.out.println("Query Ok!");
        else System.out.println("Query Not Ok!");        
        */
        
        /*
        //Sample code snippet for DELETE DML Statement
        Object[] values = {13};
        String query = "DELETE FROM task WHERE id=?";
        if(PG.executeDML(query, values)) System.out.println("Query Ok!");
        else System.out.println("Query Not Ok!");        
        */
        
        /*
        //Sample code snippet for SELECT DQL Statement
        String[] columns = {"name","isdone"};
        String whereClause = "1=1";
        String[][] records = PG.executeDQL("task", columns, whereClause);
        if(records==null || records.length==0)System.out.println("No record found!");
        else System.out.println(records.length + " record(s) found!");
        */
    }
}
