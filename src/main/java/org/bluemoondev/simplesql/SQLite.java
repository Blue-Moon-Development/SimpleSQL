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

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> SQLite.java<br>
 * <p>
 * Represents an SQLite database. This is a locally stored database file rather
 * than a server based one.
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class SQLite extends Database {

	private final File file;

	/**
	 * Creates an SQLite database with the given file
	 *
	 * @param file The desired database file to create
	 */
	public SQLite(File file) {
		super("org.sqlite.JDBC");
		file.getParentFile().mkdirs();
		this.file = file;
	}

	@Override
	public void reactivateConnection() throws SQLException {
		SimpleSQL.getLogger().info("Connecting to local database file " + file.getAbsolutePath());
		setConnection(DriverManager.getConnection("jdbc:sqlite://" + file.getAbsolutePath()));
	}

}
