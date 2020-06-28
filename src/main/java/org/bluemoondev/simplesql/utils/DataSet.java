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

import org.bluemoondev.simplesql.exceptions.SSQLException;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> DataSet.java<br>
 * <p>
 * Stores the name of a column and its current value
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class DataSet {

	/** The name of the column */
	public final String name;

	/** The value at this column */
	public final Object value;

	/**
	 * Creates a new <code>DataSet</code> with the given column name and value
	 *
	 * @param name  The name of the column
	 * @param value The value for this instance
	 * @throws SSQLException 
	 */
	public DataSet(String name, Object value) throws SSQLException {
		this.name = name;
		this.value = value;
		if (!Checks.isValidObject(value)) throw new SSQLException(	"The DataSet value for key "	+ name
																	+ " is of an invalid type");
	}

	@Override
	public String toString() {
		return name;
	}

}
