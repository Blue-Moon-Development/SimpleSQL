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

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> MySql.java<br>
 * <p>
 * Represents a MySQL database. This will connect to a server based database. If
 * you need a locally stored file instead,
 * use {@link org.bluemoondev.simplesql.SQLite SQLite} instead
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class MySql extends Database {

	/** Static reference to the EST server timezone code */
	public static final String	EST	= "EST5EDT";
	/** Static reference to the CST server timezone code */
	public static final String	CST	= "CST6CDT";
	/** Static reference to the MST server timezone code */
	public static final String	MST	= "MST7MDT";
	/** Static reference to the PST server timezone code */
	public static final String	PST	= "PST8PDT";

	private final String	host, user, password, timezone;
	private final int		port;

	/**
	 * Creates a MySql database with the given information
	 *
	 * @param host           The host address of the server
	 * @param user           The username to connect to the database
	 * @param password       The password to connect to the database
	 * @param dbName         The name of the database. <strong>This must be an
	 *                       existing database</strong>
	 * @param port           The port of the server. Typically the default is 3306
	 * @param serverTimezone The server timezone
	 */
	public MySql(String host, String user, String password, String dbName, int port, String serverTimezone) {
		super("com.mysql.cj.jdbc.Driver");
		this.host = host;
		this.user = user;
		this.password = password;
		this.name = dbName;
		this.port = port;
		this.timezone = serverTimezone;
	}

	/**
	 * Creates a MySql database with the given information
	 *
	 * @param host           The host address of the server
	 * @param user           The username to connect to the database
	 * @param password       The password to connect to the database
	 * @param dbName         The name of the database. <strong>This must be an
	 *                       existing database</strong>
	 * @param serverTimezone The server timezone
	 */
	public MySql(String host, String user, String password, String dbName, String serverTimezone) {
		this(host, user, password, dbName, 3306, serverTimezone);
	}

	@Override
	public void reactivateConnection() throws SQLException {
		String dsn = "jdbc:mysql://"	+ host + ":" + port + "/" + name
						+ "?serverTimezone=" + timezone;
		setConnection(DriverManager.getConnection(dsn, user, password));
	}

}
