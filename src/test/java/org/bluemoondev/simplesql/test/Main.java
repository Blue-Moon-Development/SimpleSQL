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
package org.bluemoondev.simplesql.test;

import java.io.File;

import org.bluemoondev.simplesql.SQLite;
import org.bluemoondev.simplesql.SimpleSQL;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> Main.java<br>
 * <p>
 * Main test class
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class Main {

	public static final TestTable TEST = new TestTable();

	public static void main(String[] args) {
		SimpleSQL.init(new SQLite(new File("./database.db")));
		// SimpleSQL.init(new MySql("host", "user", "password", "databaseName", 3306,
		// MySql.CST));

		TEST.setAge("John", "Smith", 18);
		TEST.setAge("Matt", "Smith", 21);
		TEST.setAge("Sara", "Bananas", 17);

		TEST.setPassing("John", "Smith", true);
		TEST.setPassing("Sara", "Bananas", true);

		TEST.setGrade("Matt", "Smith", "Senior");

		System.out.println(	"John, age: "	+ TEST.getAge("John", "Smith") + ", passing: "
							+ TEST.getPassing("John", "Smith"));
		System.out.println(	"Matt, age: "	+ TEST.getAge("Matt", "Smith") + ", passing: "
							+ TEST.getPassing("Matt", "Smith"));
		System.out.println(	"Sara, age: "	+ TEST.getAge("Sara", "Bananas") + ", passing: "
							+ TEST.getPassing("Sara", "Bananas"));
		System.out.println("Matt grade: " + TEST.getGrade("Matt", "Smith"));
		System.out.println("Sara grade: " + TEST.getGrade("Sara", "Bananas")); // Outputs that her grade is null, since
																				 // no default value was specified

	}

}
