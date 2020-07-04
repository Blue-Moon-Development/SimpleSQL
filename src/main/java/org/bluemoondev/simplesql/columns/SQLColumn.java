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

import org.bluemoondev.blutilities.generics.AbstractType;
import org.bluemoondev.simplesql.utils.Checks;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> SQLColumn.java<br>
 * <p>
 * Represents an abstract column in an SQL table
 * </p>
 *
 * @author     <a href = "https://bluemoondev.org"> Matt</a>
 * @param  <T> The data type this column stores
 */
public abstract class SQLColumn<T> extends AbstractType<T> {

	public final String		name;
	public final T			defaultValue;
	public final boolean	primary;

	private boolean nullable;

	/**
	 * Creates an instance of an <code>SQLColumn</code> of the given type. Can be
	 * <code>IntColumn, LongColumn, BoolColumn, StringColumn</code> and any other
	 * derived type.
	 *
	 * @param name         The name of this column
	 * @param defaultValue The default value, or null if it shall have no default
	 *                     value
	 * @param primary      Is this column the primary key of the table?
	 */
	public SQLColumn(String name, T defaultValue, boolean primary) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.primary = primary;
		this.nullable = defaultValue == null;
	}

	/**
	 * Creates an instance of an <code>SQLColumn</code> of the given type. Can be
	 * <code>IntColumn, LongColumn, BoolColumn, StringColumn</code> and any other
	 * derived type. It is important to note
	 * that using this constructor, the <strong><code>primary</code> will be set to
	 * false</strong> meaning this column
	 * cannot be used as the primary key of the table
	 *
	 * @param name         The name of this column
	 * @param defaultValue The default value, or null if it shall have no default
	 *                     value
	 */
	public SQLColumn(String name, T defaultValue) {
		this(name, defaultValue, false);
	}

	/**
	 * Should this column be allowed to have a null value?
	 * 
	 * @param  nullable True if the value is allowed to be null
	 * @return          this
	 */
	public SQLColumn<T> setNullable(boolean nullable) {
		this.nullable = nullable;
		return this;
	}

	protected abstract String getType();

	public String getDescriptor() { return getType() + defaultable() + nullable() + primable(); }

	protected String nullable() {
		return nullable ? "" : " NOT NULL";
	}

	protected String defaultable() {
		return defaultValue == null ? "" : " DEFAULT " + defaultValue;
	}

	protected String primable() {
		return primary ? " PRIMARY KEY" : "";
	}

	public String is(Object value) {
		if (!Checks.isValidObject(value)) return null;
		return name + " = " + value.toString();
	}

	public String set() {
		return name + " = ?";
	}

	public String isLessThan(long value) {
		return name + " < " + value;
	}

	public String isGreaterThan(long value) {
		return name + " > " + value;
	}

	@Override
	public String toString() {
		return name;
	}

}
