/*
 * Copyright (C) 2019 Matt
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.bluemoondev.simplesql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> Database.java<br>
 * <p>
 * Represents a database to be implemented
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 * @see MySql
 * @see SQLite
 */
public abstract class Database {

	private Connection	conn;
	protected String	name;

	/**
	 * Creates a database using the specified driver
	 *
	 * @param driver The class path/name for the driver
	 */
	public Database(String driver) {
		try {
			Class<?> driverClass = Class.forName(driver);
			driverClass.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
			SimpleSQL.getLogger().error(ex.getMessage(), ex);
		}
	}

	/**
	 * Sets the connection
	 * 
	 * @param conn The connection to set
	 */
	public final void setConnection(Connection conn) { this.conn = conn; }

	/**
	 * Retrieves the connection and reconnects if needed
	 * 
	 * @return The connection
	 * @throws SQLException If the connection was failed to be created or retrieved
	 */
	public final Connection getConnection() throws SQLException {
		if (conn == null || conn.isClosed())
			reactivateConnection();
		return conn;
	}

	/**
	 * Shuts down the connection to the database
	 * 
	 * @throws SQLException If there was an error while closing the connection
	 */
	public final void closeConnection() throws SQLException {
		if (conn != null && !conn.isClosed())
			conn.close();
	}

	/**
	 * Reconnects to the database
	 * 
	 * @throws SQLException If the connection was unable to be established
	 */
	public abstract void reactivateConnection() throws SQLException;

	/**
	 * Closes a Result Set
	 * 
	 * @param rs The result set to close
	 * @throws SQLException If the result set could not be closed
	 */
	public final void closeResultSet(ResultSet rs) throws SQLException {
		if (rs != null)
			rs.close();
	}

	/**
	 * Creates a prepared statement with the given query
	 * 
	 * @param query The SQL query to create a statement from
	 * @return The created prepared statement
	 * @throws SQLException If the prepared statement could not be created
	 */
	public final PreparedStatement getPreparedStatement(String query) throws SQLException {
		return getConnection().prepareStatement(query);
	}

	/**
	 * Closes a statement
	 * 
	 * @param s The statement to close
	 * @throws SQLException If the statement could not be closed
	 */
	public final void closeStatement(Statement s) throws SQLException {
		if (s != null)
			s.close();

	}

}
