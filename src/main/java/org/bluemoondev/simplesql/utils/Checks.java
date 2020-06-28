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

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> Checks.java<br>
 * <p>
 * Just a small class to check if certain conditions are met
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class Checks {

	/**
	 * Ensures the given object is a type supported by the SimpleSQL API
	 *
	 * @param o The object to check the validity of
	 * @return True if the object's type is supported. False otherwise
	 */
	public static boolean isValidObject(Object o) {
		return o instanceof Long || o instanceof Integer || o instanceof String || o instanceof Boolean;
	}

}
