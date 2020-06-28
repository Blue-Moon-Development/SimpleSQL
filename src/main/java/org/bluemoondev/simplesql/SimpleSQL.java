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

import java.util.logging.Logger;

import org.bluemoondev.simplesql.exceptions.SSQLException;
import org.bluemoondev.simplesql.utils.TableManager;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> SimpleSQL.java<br>
 * <p>
 * Initializes the API and provides access to the database
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class SimpleSQL {

	private static Logger logger = Logger.getLogger(SimpleSQL.class.getName());

	private static Database database;

	/**
	 * Initializes the SimpleSQL API
	 *
	 * @param db The type of database to use. Can by MySql or SQLite
	 * @see SQLite
	 * @see MySql
	 */
	public static void init(Database db) {
		database = db;
		TableManager.createTables();
	}

	/**
	 * Gets the instance of the database
	 *
	 * @return The database in use
	 * @throws SSQLException 
	 */
	public static Database getDatabase() throws SSQLException {
		if (database == null) throw new SSQLException("Database has not been created. Please call SimpleSQL.init(...)");
		return database;
	}

	/**
	 * If for whatever reason a custom logger needs to be set, use this
	 *
	 * @param customLogger Your custom logger
	 */
	public static void setLogger(Logger customLogger) { logger = customLogger; }

	/**
	 * Retrieves the logger SimpleSQL uses
	 *
	 * @return the logger
	 */
	public static Logger getLogger() { return logger; }

}
