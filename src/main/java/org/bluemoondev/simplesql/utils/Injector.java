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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bluemoondev.simplesql.SimpleSQL;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> Injector.java<br>
 * <p>
 * Used to inject data into a Prepared Statement
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class Injector {

	private Map<Integer, Object> injections;

	/**
	 * Creates a new injector
	 */
	public Injector() {
		injections = new HashMap<>();
	}

	/**
	 * Maps data to be injected
	 *
	 * @param key   The key/name where the value will be injected
	 * @param value The value to get injected
	 */
	public void put(int key, Object value) {
		injections.put(key, value);
	}

	public void inject(PreparedStatement ps) {
		for (Map.Entry<Integer, Object> e : injections.entrySet()) {
			try {
				int i = e.getKey();
				Object o = e.getValue();
				if (o instanceof Long) ps.setLong(i, (Long) o);
				else if (o instanceof Integer) ps.setInt(i, (Integer) o);
				else if (o instanceof String) ps.setString(i, (String) o);
				else if (o instanceof Boolean) ps.setBoolean(i, (Boolean) o);
			} catch (SQLException ex) {
				SimpleSQL.getLogger().log(Level.SEVERE, null, ex);
			}
		}
	}

}
