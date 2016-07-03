package corgis.publishers;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import corgis.publishers.domain.*;

import java.sql.*;

/**
 * {'overview': "From a newspaper article about analyzing amazon book sales by genre and publisher. Unfortunately, they do not have information on the book's title or author.\n", 'citation': 'http://authorearnings.com/report/september-2015-author-earnings-report/', 'short': "From a newspaper article about analyzing amazon book sales by genre and publisher. Unfortunately, they do not have information on the book's title or author.\n"}
 */
public class PublishersLibrary {
    private String databasePath;
	private Connection connection;
    private JSONParser parser;
    private final int HARDWARE = 1000;
    
    public static void main(String[] args) {
        System.out.println("Testing Publishers");
        PublishersLibrary publishersLibrary = new PublishersLibrary();
        
        
        System.out.println("Testing production GetBooks");
        ArrayList<Book> list_of_book_1_production = publishersLibrary.getBooks(false);
        
        
        System.out.println("Testing test GetBooks");
        ArrayList<Book> list_of_book_1_test = publishersLibrary.getBooks(true);
        
        
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
	public PublishersLibrary() {
        this.databasePath = "publishers.db";
        this.connectToDatabase(this.databasePath);
	}
	
    /**
     * Create a connection to the database file, stored explicitly somewhere.
     * @param databasePath The filename of the database file.
     */
	public PublishersLibrary(String databasePath) {
        this.databasePath = databasePath;
        this.connectToDatabase(this.databasePath);
	}
    
    
    
    /**
     * Returns a list of the books in the database.
    
     * @return a list[book]
     */
	public ArrayList<Book> getBooks() {
        return this.getBooks(true);
    }
    
    
    /**
     * Returns a list of the books in the database.
    
     * @return a list[book]
     */
	public ArrayList<Book> getBooks(boolean test) {
        String query;
        if (test) {
            query = String.format("SELECT data FROM publishers LIMIT %d", this.HARDWARE);
        } else {
            query = "SELECT data FROM publishers";
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
        
        ArrayList<Book> result = new ArrayList<Book>();
        try {
            while (rs.next()) {
                String raw_result = rs.getString(1);
                Book parsed = null;
                if (test) {
                    parsed = new Book((JSONObject)this.parser.parse(raw_result));
                    
                } else {
                    parsed = new Book((JSONObject)this.parser.parse(raw_result));
                    
                }
                
                result.add(parsed);
                
            }
        } catch (SQLException e) {
            System.err.println("Could not iterate through query.");
    		e.printStackTrace();
        } catch (ParseException e) {
            System.err.println("Could not convert the response from getBooks; a parser error occurred.");
    		e.printStackTrace();
        }
        return result;
	}
    
}