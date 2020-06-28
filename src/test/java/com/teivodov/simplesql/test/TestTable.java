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
package com.teivodov.simplesql.test;

import org.bluemoondev.simplesql.SQLTable;
import org.bluemoondev.simplesql.columns.BoolColumn;
import org.bluemoondev.simplesql.columns.IntColumn;
import org.bluemoondev.simplesql.columns.LongColumn;
import org.bluemoondev.simplesql.columns.SQLColumn;
import org.bluemoondev.simplesql.columns.StringColumn;
import org.bluemoondev.simplesql.exceptions.SSQLException;
import org.bluemoondev.simplesql.utils.DataSet;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> TestTable.java<br>
 * <p>
 * Test table
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class TestTable extends SQLTable {

	public static final SQLColumn<?>	FIRSTNAME	= new StringColumn("first_name", "John", 64);
	public static final SQLColumn<?>	LASTNAME	= new StringColumn("last_name", "Doe", 64);
	public static final SQLColumn<?>	PASSING		= new BoolColumn("passing", false);
	public static final SQLColumn<?>	AGE			= new IntColumn("age", 18);
	public static final SQLColumn<?>	GRADE		= new StringColumn("grade", null, 32);
	public static final SQLColumn<?>	TEST_ALTER	= new LongColumn("testingaltertable", 57L);

	public TestTable() {
		super("simplesqltest45");
	}

	public void setGrade(String first, String last, String grade) {
		try {
			update(GRADE.name, grade, new DataSet(FIRSTNAME.name, first), new DataSet(LASTNAME.name, first));
		} catch (SSQLException ex) {
			ex.printStackTrace();
		}
	}

	public String getGrade(String first, String last) {
		try {
			return getString(GRADE.name, new DataSet(FIRSTNAME.name, first), new DataSet(LASTNAME.name, first));
		} catch (SSQLException ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public void setPassing(String first, String last, boolean pass) {
		try {
			update(PASSING.name, pass, new DataSet(FIRSTNAME.name, first), new DataSet(LASTNAME.name, first));
		} catch (SSQLException ex) {
			ex.printStackTrace();
		}
	}

	public void setAge(String first, String last, int age) {
		try {
			update(AGE.name, age, new DataSet(FIRSTNAME.name, first), new DataSet(LASTNAME.name, first));
		} catch (SSQLException ex) {
			ex.printStackTrace();
		}
	}

	public boolean getPassing(String first, String last) {
		try {
			return getBool(PASSING.name, new DataSet(FIRSTNAME.name, first), new DataSet(LASTNAME.name, first));
		} catch (SSQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public int getAge(String first, String last) {
		try {
			return getInt(AGE.name, new DataSet(FIRSTNAME.name, first), new DataSet(LASTNAME.name, first));
		} catch (SSQLException ex) {
			ex.printStackTrace();
			return -1;
		}
	}

}
