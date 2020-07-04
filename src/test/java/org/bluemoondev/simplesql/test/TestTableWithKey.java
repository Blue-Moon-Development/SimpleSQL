/*
 * Copyright (C) 2020 Blue Moon Development
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
package org.bluemoondev.simplesql.test;

import org.bluemoondev.simplesql.SQLTable;
import org.bluemoondev.simplesql.columns.IntColumn;
import org.bluemoondev.simplesql.columns.LongColumn;
import org.bluemoondev.simplesql.columns.SQLColumn;
import org.bluemoondev.simplesql.columns.StringColumn;
import org.bluemoondev.simplesql.exceptions.SSQLException;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> TestTableWithKey.java<br>
 * <p>
 * TODO: Add description
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class TestTableWithKey extends SQLTable {

	public static final SQLColumn<?>	ID		= new LongColumn("id", null, true).setNullable(false);
	public static final SQLColumn<?>	NAME	= new StringColumn("full_name", null, 64).setNullable(false);// TODO: Create update method
																							// to set multiple columns
																							// at
																							// once.setNullable(false);
	public static final SQLColumn<?>	TEAM	= new StringColumn("team", "null", 32);
	public static final SQLColumn<?>	SCORE	= new IntColumn("score", 10);

	public TestTableWithKey() {
		super("test_table_with_primary_key");
	}

	public void newStudent(long id, String name) {
		try {
			update(id, NAME.name, name);
		} catch (SSQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public String getStudentName(long id) {
		try {
			return getString(id, NAME.name);
		} catch (SSQLException ex) {
			ex.printStackTrace();
		}
		
		return "404 not found";
	}

}
