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
package org.bluemoondev.simplesql.columns;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> StringColumn.java<br>
 * <p>
 * Represents a column for String data
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class StringColumn extends SQLColumn<String> {

	private final int maxLength;

	public StringColumn(String name, String defaultValue, boolean primary, int maxLength) {
		super(name, defaultValue == null ? null : "'" + defaultValue + "'", primary);
		this.maxLength = maxLength;
	}

	public StringColumn(String name, String defaultValue, int maxLength) {
		super(name, defaultValue == null ? null : "'" + defaultValue + "'");
		this.maxLength = maxLength;
	}

	@Override
	protected String getType() { return "VARCHAR(" + maxLength + ")"; }

}
