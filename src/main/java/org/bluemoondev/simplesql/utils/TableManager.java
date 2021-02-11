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
package org.bluemoondev.simplesql.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bluemoondev.blutilities.debug.Log;
import org.bluemoondev.simplesql.SQLTable;
import org.bluemoondev.simplesql.SimpleSQL;
import org.bluemoondev.simplesql.exceptions.SSQLException;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> TableManager.java<br>
 * <p>
 * Manages the SQL tables
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class TableManager {

	private static final Log LOG = Log.get("SimpleSQL", TableManager.class);

	private static final Map<String, SQLTable> TABLES = new HashMap<>();

	/**
	 * Adds a table to the manager. This is called in the SQLTable constructor
	 *
	 * @param table The table to add
	 */
	public static void add(SQLTable table) {
		TABLES.put(table.getName(), table);
	}

	/**
	 * Creates the tables being managed. Called by
	 * {@link org.bluemoondev.simplesql.SimpleSQL#init(org.bluemoondev.simplesql.Database)
	 * SimpleSQL.init(database)}
	 */
	public static void createTables() {
		TABLES.entrySet().forEach((e) -> {
			try {
				e.getValue().create();
			} catch (SQLException | SSQLException ex) {
				LOG.error("Failed to create SQL table: " + e.getValue().getName(), ex);
			}
		});
	}

	/**
	 * Gets the table associated with this name
	 *
	 * @param  name The name of the table
	 * @return      The SQLTable associated with this name
	 */
	public static SQLTable get(String name) {
		return TABLES.get(name);
	}

}
