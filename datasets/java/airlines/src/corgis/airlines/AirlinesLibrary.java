package corgis.airlines;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import corgis.airlines.domain.*;

import java.sql.*;

/**
 * This dataset is all about flights in the united states, including information about the number, length, and type of delays. The data is reported for individual months at every major airport for every carrier.
Additional information is available: <a href="http://www.rita.dot.gov/bts/help/aviation/html/understanding.html">http://www.rita.dot.gov/bts/help/aviation/html/understanding.html</a>

 */
public class AirlinesLibrary {
    private String databasePath;
	private Connection connection;
    private JSONParser parser;
    private final int HARDWARE = 100;
    
    public static void main(String[] args) {
        System.out.println("Testing Airlines");
        AirlinesLibrary airlinesLibrary = new AirlinesLibrary();
        
        
        System.out.println("Testing production GetReports");
        ArrayList<Airline> list_of_airline_1_production = airlinesLibrary.getReports(false);
        
        
        System.out.println("Testing test GetReports");
        ArrayList<Airline> list_of_airline_1_test = airlinesLibrary.getReports(true);
        
        
    }
    
    private void connectToDatabase(String databasePath) {
        this.connection = null;
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+databasePath);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        this.parser =  new JSONParser();
    }
	
    /**
     * Create a connection to the database file in its standard position.
     */
	public AirlinesLibrary() {
        this.databasePath = "airlines.db";
        this.connectToDatabase(this.databasePath);
	}
	
    /**
     * Create a connection to the database file, stored explicitly somewhere.
     * @param databasePath The filename of the database file.
     */
	public AirlinesLibrary(String databasePath) {
        this.databasePath = databasePath;
        this.connectToDatabase(this.databasePath);
	}
    
    
    
    /**
     * Returns a list of airline reports in the database.
    
     * @return a list[airline]
     */
	public ArrayList<Airline> getReports() {
        return this.getReports(true);
    }
    
    
    /**
     * Returns a list of airline reports in the database.
    
     * @return a list[airline]
     */
	public ArrayList<Airline> getReports(boolean test) {
        String query;
        if (test) {
            query = String.format("SELECT data FROM airlines LIMIT %d", this.HARDWARE);
        } else {
            query = "SELECT data FROM airlines";
        }
        PreparedStatement preparedQuery = null;
        ResultSet rs = null;
        try {
            preparedQuery = this.connection.prepareStatement(query);
        } catch (SQLException e) {
            System.err.println("Could not build SQL query for local database.");
    		e.printStackTrace();
        }
        try {
            rs = preparedQuery.executeQuery();
        } catch (SQLException e) {
            System.err.println("Could not execute query.");
    		e.printStackTrace();
        }
        
        ArrayList<Airline> result = new ArrayList<Airline>();
        try {
            while (rs.next()) {
                String raw_result = rs.getString(1);
                Airline parsed = null;
                if (test) {
                    parsed = new Airline(((JSONObject)this.parser.parse(raw_result)));
                    
                } else {
                    parsed = new Airline(((JSONObject)this.parser.parse(raw_result)));
                    
                }
                
                result.add(parsed);
                
            }
        } catch (SQLException e) {
            System.err.println("Could not iterate through query.");
    		e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("Could not convert the response from getReports; a parser error occurred.");
    		e.printStackTrace();
        }
        return result;
	}
    
}