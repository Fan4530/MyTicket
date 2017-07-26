
package db;

import db.mysql.MySQLConnection;

public class DBConnectionFactory {
	private static final String DEFAULT_DB = "mysql";

	// Create a DBConnection based on given db type.
	public static DBConnection getDBConnection(String db) {
		switch (db) {
		// You may try other dbs and add them here.
		case "mysql":
			return MySQLConnection.getInstance();
		default:
			throw new IllegalArgumentException("Invalid db " + db);
		}
	}

	// This is overloading not overriding.
	public static DBConnection getDBConnection() {
		return getDBConnection(DEFAULT_DB);
	}
}
