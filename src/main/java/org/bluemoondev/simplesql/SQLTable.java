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

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bluemoondev.simplesql.columns.SQLColumn;
import org.bluemoondev.simplesql.exceptions.SSQLException;
import org.bluemoondev.simplesql.utils.Checks;
import org.bluemoondev.simplesql.utils.DataSet;
import org.bluemoondev.simplesql.utils.Injector;
import org.bluemoondev.simplesql.utils.TableManager;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> SQLTable.java<br>
 * <p>
 * Represents a table in an SQL database
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public abstract class SQLTable {

	protected final String	tableName;
	protected String		primaryKey;

	protected Map<String, SQLColumn<?>> columns;

	/**
	 * Constructs an instance of <code>SQLTable</code>
	 *
	 * @param tableName The name of the table
	 */
	public SQLTable(String tableName) {
		this.tableName = tableName;
		columns = new HashMap<>();
		for (Field field : getClass().getDeclaredFields()) {
			if (field.getType() == SQLColumn.class)
				try {
					addColumn((SQLColumn<?>) field.get(null));
				} catch (IllegalArgumentException | IllegalAccessException ex) {
					SimpleSQL.getLogger().error(ex.getMessage(), ex);
				}

		}

		TableManager.add(this);
	}

	/**
	 * Adds a column to this table
	 *
	 * @param col The column to add
	 */
	protected final void addColumn(SQLColumn<?> col) {
		columns.put(col.name, col);
		if (col.primary)
			primaryKey = col.name;
	}

	/**
	 * Updates the specified column with the specified value. If the key does
	 * not exist in the table, it will be inserted. Use this method when you
	 * know the primary key.
	 *
	 * @param  keyValue      The key value where the table will be updated
	 * @param  name          The name of the column to update
	 * @param  newValue      The new value for that column
	 * @throws SSQLException
	 */
	public void update(Object keyValue, String name, Object newValue) throws SSQLException {
		if (!Checks.isValidObject(keyValue)) throw new SSQLException("The keyValue is an invalid type");
		if (!Checks.isValidObject(newValue)) throw new SSQLException("The newValue is an invalid type");
		if (!exists(primaryKey, keyValue))
			insert(keyValue);
		String query = "UPDATE "	+ tableName + " SET " + columns.get(name).set() + " WHERE "
						+ columns.get(primaryKey).set() + ";";
		Injector injector = new Injector();
		injector.put(1, newValue);
		injector.put(2, keyValue);
		write(query, injector);
	}

	/**
	 * Updates the specified column with the specified value. Use this method
	 * when you don't know the primary key but instead know what a few other
	 * values are that would be unique to the row
	 *
	 * @param  name          The name of the column to update
	 * @param  newValue      The new value for that column
	 * @param  data          An array of
	 *                       {@link org.bluemoondev.simplesql.utils.DataSet
	 *                       DataSets} containing the column key-value
	 *                       pairs
	 * @throws SSQLException
	 */
	public void update(String name, Object newValue, DataSet... data) throws SSQLException {
		if (!Checks.isValidObject(newValue)) throw new SSQLException("The newValue is an invalid type");
		if (data == null) throw new SSQLException("The DataSet array cannot be null");
		if (data.length == 0) throw new SSQLException("The DataSet array cannot be empty");
		if (data.length == 1) {
			update(data[0].value, name, newValue);
			return;
		}

		if (!exists(data))
			insert(data);

		StringBuilder sb = new StringBuilder("UPDATE ").append(tableName).append(" SET ")
				.append(columns.get(name).set()).append(" WHERE ").append(columns.get(data[0].name).set());

		for (int i = 1; i < data.length; i++) { sb.append(" AND ").append(columns.get(data[i].name).set()); }

		Injector injector = new Injector();
		injector.put(1, newValue);
		for (int i = 0; i < data.length; i++) { injector.put(i + 2, data[i].value); }

		sb.append(";");

		write(sb.toString(), injector);
	}

	private void insert(Object keyValue) throws SSQLException {
		if (!Checks.isValidObject(keyValue)) throw new SSQLException("The keyValue is an invalid type");
		String query = "INSERT INTO " + tableName + " (" + primaryKey + ") VALUES (?);";

		Injector injector = new Injector();
		injector.put(1, keyValue);
		write(query, injector);
	}

	private void insert(DataSet... data) throws SSQLException {
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(tableName).append(" (");

		for (int k = 0; k < data.length - 1; k++) {
			if (!columns.containsKey(data[k].name))
				throw new SSQLException(data[k].name + " is not a valid column name");
			sb.append(data[k].name).append(", ");
		}

		sb.append(data[data.length - 1].name).append(")");

		sb.append(" VALUES (");

		for (int k = 0; k < data.length - 1; k++) { sb.append("?, "); }

		sb.append("?);");

		Injector injector = new Injector();

		for (int k = 0; k < data.length; k++) {
			Object o = data[k].value;
			injector.put(k + 1, o);
		}
		write(sb.toString(), injector);
	}

	/**
	 * Creates the table.You will not need to use this. Tables are created by
	 * the <code>TableManager</code> when
	 * {@link org.bluemoondev.simplesql.SimpleSQL#init(org.bluemoondev.simplesql.Database)
	 * SimpleSQL.init(database)}
	 * is called
	 *
	 * @throws java.sql.SQLException If the table failed to create due to an SQL
	 *                               error
	 * @throws SSQLException
	 */
	public void create() throws SQLException, SSQLException {

		StringBuilder sb = new StringBuilder();

		if (SimpleSQL.getDatabase().getConnection().getMetaData()
				.getTables(SimpleSQL.getDatabase().name, null, tableName, null).next()) {
			boolean alteration = false;
			for (Map.Entry<String, SQLColumn<?>> e : columns.entrySet()) {
				String s = e.getKey();
				SQLColumn<?> c = e.getValue();

				if (!SimpleSQL.getDatabase().getConnection().getMetaData().getColumns(null, null, tableName, s)
						.next()) {
					sb.append("ALTER TABLE ").append(tableName).append(" ADD ").append(s).append(" ").append(c
							.getDescriptor());
					writeNew(sb.toString());
					sb = new StringBuilder();
					alteration = true;
				}
			}

			if (alteration)
				return;
		}

		sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

		if (primaryKey == null)
			if (SimpleSQL.getDatabase() instanceof SQLite)
				sb.append("auto_id INTEGER PRIMARY KEY AUTOINCREMENT,");
			else
				sb.append("auto_id INTEGER PRIMARY KEY AUTO_INCREMENT,");

		int i = 1;
		for (Map.Entry<String, SQLColumn<?>> e : columns.entrySet()) {
			if (i == columns.size())
				sb.append(e.getValue().name).append(" ").append(e.getValue().getDescriptor()).append(");");
			else
				sb.append(e.getValue().name).append(" ").append(e.getValue().getDescriptor()).append(", ");
			i++;
		}

		writeNew(sb.toString());
	}

	private String select(SQLColumn<?> column, String... wheres) {
		StringBuilder sb = new StringBuilder();
		sb.append(wheres[0]);
		for (int i = 1; i < wheres.length; i++) { sb.append(" AND ").append(wheres[i]); }

		return select(sb.toString(), column.name);
	}

	private String selectAllFor(String col) {
		return select(null, col);
	}

	private String selectAll(String... wheres) {
		StringBuilder sb = new StringBuilder(wheres[0]);
		for (int i = 1; i < wheres.length; i++) { sb.append(" AND ").append(wheres[i]); }

		return select(sb.toString(), "*");
	}

	private String selectAll() {
		return select(null, "*");
	}

	private String select(String where, String columns) {
		return "SELECT "	+ columns + " FROM " + tableName + (where == null	? ""
																				: " WHERE " + where)
				+ ";";
	}

	/**
	 * Checks if the table contains this key and value
	 *
	 * @param  key           The key to check
	 * @param  value         The value to check
	 * @return               True if a row exists with this key and value. False
	 *                       otherwise
	 * @throws SSQLException
	 */
	public boolean exists(String key, Object value) throws SSQLException {
		if (!Checks.isValidObject(value)) throw new SSQLException("The value is an invalid type");
		String query = selectAll(columns.get(key).set());
		Injector injector = new Injector();
		injector.put(1, value);
		return read(query, injector, results -> {
			return results.next();
		});
	}

	/**
	 * Checks if the table contains these keys and values
	 *
	 * @param  data          An array of key-value pairs to check
	 * @return               True if a row exists with these keys and values. False
	 *                       otherwise
	 * @throws SSQLException
	 */
	public boolean exists(DataSet... data) throws SSQLException {

		if (data == null) throw new SSQLException("The DataSet array must not be null");
		if (data.length == 0) throw new SSQLException("The DataSet array must not be empty");
		if (data.length == 1) return exists(data[0].name, data[0].value);

		String[] wheres = new String[data.length];

		Injector injector = new Injector();

		for (int i = 0; i < wheres.length; i++) {
			wheres[i] = columns.get(data[i].name).set();
			injector.put(i + 1, data[i].value);
		}
		String query = selectAll(wheres);
		return read(query, injector, results -> {
			return results.next();
		});
	}

	/**
	 * Resets the row with the given key and column name to the default value
	 *
	 * @param  key           The key from where the data will be reset
	 * @param  toReset       The name of the column to reset
	 * @throws SSQLException
	 */
	public void reset(Object key, String toReset) throws SSQLException {
		update(key, toReset, columns.get(toReset).defaultValue);
	}

	/**
	 * Resets the row with the given key and column name to the default value
	 *
	 * @param  toReset       The column to reset
	 * @param  data          The key-value pairs to reset
	 * @throws SSQLException
	 */
	public void reset(String toReset, DataSet... data) throws SSQLException {
		update(toReset, columns.get(toReset).defaultValue, data);
	}

	/**
	 * Deletes a row at the specified location
	 *
	 * @param  key           The key of the row to delete
	 * @throws SSQLException
	 */
	public void delete(Object key) throws SSQLException {
		if (!Checks.isValidObject(key)) throw new SSQLException("The key is an invalid type");
		String query = "DELETE FROM " + tableName + " WHERE " + columns.get(primaryKey).set() + ";";
		Injector injector = new Injector();
		injector.put(1, key);
		write(query, injector);
	}

	/**
	 * Deletes a row where the specified keys and values can be found
	 *
	 * @param  data          The key-value pairs to delete
	 * @throws SSQLException
	 */
	public void delete(DataSet... data) throws SSQLException {
		if (data.length == 1) {
			delete(data[0].value);
			return;
		}
		StringBuilder sb = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ")
				.append(columns.get(data[0].name).set());

		Injector injector = new Injector();

		injector.put(1, data[0].value);
		for (int i = 1; i < data.length; i++) {
			sb.append(" AND ").append(columns.get(data[i].name).set());
			injector.put(i + 1, data[i].value);
		}
		sb.append((";"));

		write(sb.toString(), injector);

	}

	/**
	 * Retrieves the long from the specified column where the list of keys and
	 * values can be found
	 *
	 * @param  <T>           The type of value to return
	 * @param  name          The name of the column to retrieve
	 *                       data from
	 * @param  dataSets      An array of the key-value pairs to use as locators
	 * @return               The T stored at this location
	 * @throws SSQLException If no
	 *                       {@link org.bluemoondev.simplesql.columns.SQLColumn
	 *                       SQLColumn} has
	 *                       been created with that name, or if the dataSets array
	 *                       is null or empty
	 */
	public <T> T getValue(String name, DataSet... dataSets) throws SSQLException {
		if (!checks(name, dataSets)) return null;
		if (dataSets.length == 1) return getValue(dataSets[0].value, name);
		String strs[] = new String[dataSets.length];

		Injector injector = getInjector((i, s) -> {
			strs[i] = s;
		}, dataSets);

		String query = select(columns.get(name), strs);

		Class<?> clazz = columns.get(name).getTypeClass();
		return (T) read(query, injector, results -> {
			if (results.next()) {
				try {
					return results.getObject(name, clazz);
				} catch (SQLFeatureNotSupportedException ex) {
					return getValueFallback(clazz, results, name);
				}
			}
			return null;
		});
	}

	/**
	 * Retrieves the long from the specified column where the list of keys and
	 * values can be found
	 *
	 * @param  name          The name of the <code>LongColumn</code> to retrieve
	 *                       data from
	 * @param  data          An array of the key-value pairs to use as locators
	 * @return               The long stored at this location
	 * @throws SSQLException If no
	 *                       {@link org.bluemoondev.simplesql.columns.LongColumn
	 *                       LongColumn} has
	 *                       been created with that name, or if the dataSets array
	 *                       is null or empty
	 */
	public long getLong(String name, DataSet... data) throws SSQLException {
		return getValue(name, data);
	}

	/**
	 * Retrieves the int from the specified column where the list of keys and
	 * values can be found
	 *
	 * @param  name          The name of the <code>IntColumn</code> to retrieve data
	 *                       from
	 * @param  data          An array of the key-value pairs to use as locators
	 * @return               The int stored at this location
	 * @throws SSQLException If no
	 *                       {@link org.bluemoondev.simplesql.columns.IntColumn
	 *                       IntColumn} has
	 *                       been created with that name, or if the dataSets array
	 *                       is null or empty
	 */
	public int getInt(String name, DataSet... data) throws SSQLException {
		return getValue(name, data);
	}

	/**
	 * Retrieves the String from the specified column where the list of keys and
	 * values can be found
	 *
	 * @param  name          The name of the <code>StringColumn</code> to retrieve
	 *                       data
	 *                       from
	 * @param  data          An array of key-value pairs to use as locators
	 * @return               The String stored at this location
	 * @throws SSQLException If no
	 *                       {@link org.bluemoondev.simplesql.columns.StringColumn
	 *                       StringColumn} has
	 *                       been created with that name, or if the dataSets array
	 *                       is null or empty
	 */
	public String getString(String name, DataSet... data) throws SSQLException {
		return getValue(name, data);
	}

	/**
	 * Retrieves the boolean from the specified column where the list of keys
	 * and values can be found
	 *
	 * @param  name          The name of the <code>BoolColumn</code> to retrieve
	 *                       data from
	 * @param  data          An array of key-value pairs to use as locators
	 * @return               The boolean stored at this location
	 * @throws SSQLException If no
	 *                       {@link org.bluemoondev.simplesql.columns.BoolColumn
	 *                       BoolColumn} has
	 *                       been created with that name, or if the dataSets array
	 *                       is null or empty
	 */
	public boolean getBool(String name, DataSet... data) throws SSQLException {
		return getValue(name, data);
	}

	// TODO: Test this
	/**
	 * Retrieves the value from the specified column at the row where the
	 * primary key value can be found
	 * 
	 * @param  <T>           The type to return, must be supported by SQL
	 * @param  keyValue      The primary key value of the row to look for
	 * @param  name          The name of the column to get data from
	 * @return               The long value at this location
	 * @throws SSQLException
	 */
	public <T> T getValue(Object keyValue, String name) throws SSQLException {
		String query = select(columns.get(primaryKey).set(), name);
		Injector injector = new Injector();
		injector.put(1, keyValue);
		Class<?> clazz = columns.get(name).getTypeClass();
		System.out.println(clazz);
		return (T) read(query, injector, results -> {
			System.out.println(results != null);
			if (results.next()) {
				try {
				return results.getObject(name, clazz);
				}catch(SQLFeatureNotSupportedException ex) {
					return getValueFallback(clazz, results, name);
				}
			}
			return null;
		});
	}

	/**
	 * Retrieves the value from the specified column at the row where the
	 * primary key value can be found
	 *
	 * @param  keyValue      The primary key value of the row to look for
	 * @param  name          The name of the column to get data from
	 * @return               The long value at this location
	 * @throws SSQLException
	 */
	public long getLong(Object keyValue, String name) throws SSQLException {
		return getValue(keyValue, name);
	}

	/**
	 * Retrieves the value from the specified column at the row where the
	 * primary key value can be found
	 *
	 * @param  keyValue      The primary key value of the row to look for
	 * @param  name          The name of the column to get data from
	 * @return               The int value at this location
	 * @throws SSQLException
	 */
	public int getInt(Object keyValue, String name) throws SSQLException {
		return getValue(keyValue, name);
	}

	/**
	 * Retrieves the value from the specified column at the row where the
	 * primary key value can be found
	 *
	 * @param  keyValue      The primary key value of the row to look for
	 * @param  name          The name of the column to get data from
	 * @return               The String value at this location
	 * @throws SSQLException
	 */
	public String getString(Object keyValue, String name) throws SSQLException {
		return getValue(keyValue, name);
	}

	/**
	 * Retrieves the value from the specified column at the row where the
	 * primary key value can be found
	 *
	 * @param  keyValue      The primary key value of the row to look for
	 * @param  name          The name of the column to get data from
	 * @return               The boolean value at this location
	 * @throws SSQLException
	 */
	public boolean getBool(Object keyValue, String name) throws SSQLException {
		return getValue(keyValue, name);
	}

	/**
	 * Retrieves a list of all values at every row for the specified column
	 *
	 * @param  <T>           The type of value for the column
	 * @param  name          The column to retrieve the data from
	 * @return               A list of the values
	 * @throws SSQLException If no such
	 *                       {@link org.bluemoondev.simplesql.columns.SQLColumn
	 *                       SQLColumn} of type T has been added to the table
	 */
	public <T> List<T> getValues(String name) throws SSQLException {
		String query = selectAllFor(name);
		List<T> values = new ArrayList<>();
		Class<T> clazz = columns.get(name).getTypeClass();
		set(query, results -> {
			while (results.next()) {
				try {
					values.add(results.getObject(name, clazz));
				} catch (SQLFeatureNotSupportedException ex) {
					values.add(getValueFallback(clazz, results, name));
				}
			}
		});

		return values;
	}

	/**
	 * Retrieves a list of all values at every row for the specified column
	 *
	 * @param  name          The column to retrieve the data from
	 * @return               A list of the long values
	 * @throws SSQLException If no such
	 *                       {@link org.bluemoondev.simplesql.columns.LongColumn
	 *                       LongColumn} has been added to the table
	 */
	public List<Long> getLongs(String name) throws SSQLException {
		return getValues(name);
	}

	/**
	 * Retrieves a list of all values at every row for the specified column
	 *
	 * @param  name          The column to retrieve the data from
	 * @return               A list of the int values
	 * @throws SSQLException If no such
	 *                       {@link org.bluemoondev.simplesql.columns.IntColumn
	 *                       IntColumn} has been added to the table
	 */
	public List<Integer> getInts(String name) throws SSQLException {
		return getValues(name);
	}

	/**
	 * Retrieves a list of all values at every row for the specified column
	 *
	 * @param  name          The column to retrieve the data from
	 * @return               A list of the String values
	 * @throws SSQLException If no such
	 *                       {@link org.bluemoondev.simplesql.columns.StringColumn
	 *                       StringColumn} has been added to the table
	 */
	public List<String> getStrings(String name) throws SSQLException {
		return getValues(name);
	}

	/**
	 * Retrieves a list of all values at every row for the specified column
	 *
	 * @param  name          The column to retrieve the data from
	 * @return               A list of the boolean values
	 * @throws SSQLException If no such
	 *                       {@link org.bluemoondev.simplesql.columns.BoolColumn
	 *                       BoolColumn} has been added to the table
	 */
	public List<Boolean> getBools(String name) throws SSQLException {
		return getValues(name);
	}

	/**
	 * Retrieves a list of all values at every row for the supplied column name with
	 * the given data set key-value pairs
	 *
	 * @param  <T>           The type of value to return, must be supported my SQL
	 * @param  name          The name of the column of values to retrieve
	 * @param  keys          An array of key-value pairs to use as locators
	 * @return               A list of the values of type T
	 * @throws SSQLException
	 */
	public <T> List<T> getValues(String name, DataSet... dataSets) throws SSQLException {
		if (!checks(name, dataSets)) return null;
		String strs[] = new String[dataSets.length];
	
		Injector injector = getInjector((i, s) -> {
			strs[i] = s;
		}, dataSets);
	
		String query = select(columns.get(name), strs);
		List<T> values = new ArrayList<>();
		Class<?> clazz = columns.get(name).getTypeClass();
		set(query, injector, results -> {
			while (results.next()) { values.add((T) results.getObject(name, clazz)); }
		});
		return values;
	}

	/**
	 * Retrieves a list of all values at every row for the supplied column name with
	 * the given data set key-value pairs
	 *
	 * @param  name          The name of the column of values to retrieve
	 * @param  keys          An array of key-value pairs to use as locators
	 * @return               A list of the long values
	 * @throws SSQLException
	 */
	public List<Long> getLongs(String name, DataSet... keys) throws SSQLException {
		return getValues(name, keys);
	}

	/**
	 * Retrieves a list of all values at every row for the supplied column name with
	 * the given data set key-value pairs
	 *
	 * @param  name          The name of the column of values to retrieve
	 * @param  keys          An array of key-value pairs to use as locators
	 * @return               A list of the long values
	 * @throws SSQLException
	 */
	public List<Integer> getInts(String name, DataSet... keys) throws SSQLException {
		return getValues(name, keys);
	}

	/**
	 * Retrieves a list of all values at every row for the supplied column name with
	 * the given data set key-value pairs
	 *
	 * @param  name          The name of the column of values to retrieve
	 * @param  keys          An array of key-value pairs to use as locators
	 * @return               A list of the long values
	 * @throws SSQLException
	 */
	public List<String> getStrings(String name, DataSet... keys) throws SSQLException {
		return getValues(name, keys);
	}

	/**
	 * Retrieves a list of all values at every row for the supplied column name with
	 * the given data set key-value pairs
	 *
	 * @param  name          The name of the column of values to retrieve
	 * @param  keys          An array of key-value pairs to use as locators
	 * @return               A list of the long values
	 * @throws SSQLException
	 */
	public List<Boolean> getBools(String name, DataSet... keys) throws SSQLException {
		return getValues(name, keys);
	}

	/**
	 * Retrieves a list of all values for the given column at every row with the
	 * given key column name and value for the specified column
	 * 
	 * @param  <T>           The type of value to return, must be supported by SQL
	 * @param  keyName       The name of the column to use as the key
	 * @param  key           The value at the key column
	 * @param  name          The name of the column to grab values from
	 * @return               A list of the values of type T
	 * @throws SSQLException
	 */
	public <T> List<T> getValues(String keyName, Object key, String name) throws SSQLException {
		String query = select(columns.get(keyName).set(), name);
		Injector injector = new Injector();
		injector.put(1, key);
		List<T> values = new ArrayList<>();
		Class<?> clazz = columns.get(name).getTypeClass();
		set(query, injector, results -> {
			while (results.next()) { values.add((T) results.getObject(name, clazz)); }
		});
		return values;
	}

	/**
	 * Retrieves a list of all values at every row with the given key column name
	 * and value for the specified column
	 *
	 * @param  keyName       The name of the column to use as the key
	 * @param  key           The value at the key column
	 * @param  name          The name of the column to grab values from
	 * @return               A list of the long values
	 * @throws SSQLException
	 */
	public List<Long> getLongs(String keyName, Object key, String name) throws SSQLException {
		return getValues(keyName, key, name);
	}

	/**
	 * Retrieves a list of all values at every row with the given key column name
	 * and value for the specified column
	 *
	 * @param  keyName       The name of the column to use as the key
	 * @param  key           The value at the key column
	 * @param  name          The name of the column to grab values from
	 * @return               A list of the int values
	 * @throws SSQLException
	 */
	public List<Integer> getInts(String keyName, Object key, String name) throws SSQLException {
		return getValues(keyName, key, name);
	}

	/**
	 * Retrieves a list of all values at every row with the given key column name
	 * and value for the specified column
	 *
	 * @param  keyName       The name of the column to use as the key
	 * @param  key           The value at the key column
	 * @param  name          The name of the column to grab values from
	 * @return               A list of the String values
	 * @throws SSQLException
	 */
	public List<String> getStrings(String keyName, Object key, String name) throws SSQLException {
		return getValues(keyName, key, name);
	}

	/**
	 * Retrieves a list of all values at every row with the given key column name
	 * and value for the specified column
	 *
	 * @param  keyName       The name of the column to use as the key
	 * @param  key           The value at the key column
	 * @param  name          The name of the column to grab values from
	 * @return               A list of the boolean values
	 * @throws SSQLException
	 */
	public List<Boolean> getBools(String keyName, Object key, String name) throws SSQLException {
		return getValues(keyName, key, name);
	}

	private void set(String query, ResultsConsumer consumer) throws SSQLException {
		set(query, null, consumer);
	}

	private void set(String query, Injector injector, ResultsConsumer consumer) throws SSQLException {
		try {
			PreparedStatement ps = SimpleSQL.getDatabase().getPreparedStatement(query);
			if(injector != null) injector.inject(ps);
			ResultSet results = ps.executeQuery();
			consumer.consume(results);
			SimpleSQL.getDatabase().closeResultSet(results);
			SimpleSQL.getDatabase().closeStatement(ps);
		} catch (SQLException ex) {
			SimpleSQL.getLogger().error("Failed to execute SQL query: " + query, ex);
		}
	}

	private <T> T read(String query, Injector injector, ResultsSupplier<T> supplier) throws SSQLException {

		try {
			PreparedStatement ps = SimpleSQL.getDatabase().getPreparedStatement(query);
			injector.inject(ps);
			ResultSet results = ps.executeQuery();
			T re = supplier.apply(results);
			SimpleSQL.getDatabase().closeResultSet(results);
			SimpleSQL.getDatabase().closeStatement(ps);

			return re;
		} catch (SQLException ex) {
			SimpleSQL.getLogger().error("Failed to execute SQL query: " + query, ex);
		}

		return null;
	}

	private void write(String query, Injector injector) throws SSQLException {
		try {
			PreparedStatement ps = SimpleSQL.getDatabase().getPreparedStatement(query);
			injector.inject(ps);
			ps.executeUpdate();
			SimpleSQL.getDatabase().closeStatement(ps);
		} catch (SQLException ex) {
			SimpleSQL.getLogger().error("Failed to execute SQL query: " + query, ex);
		}

	}

	private void writeNew(String query) throws SSQLException {
		try {
			PreparedStatement ps = SimpleSQL.getDatabase().getPreparedStatement(query);
			ps.execute();
			SimpleSQL.getDatabase().closeStatement(ps);
		} catch (SQLException ex) {
			SimpleSQL.getLogger().error("Failed to execute SQL query: " + query, ex);
		}
	}

	private Injector getInjector(InjectorConsumer consumer, DataSet... dataSets) {
		Injector injector = new Injector();

		for (int i = 0; i < dataSets.length; i++) {
			consumer.consume(i, columns.get(dataSets[i].name).set());
			injector.put(i + 1, dataSets[i].value);
		}

		return injector;
	}

	// TODO make more checks
	private boolean checks(String name, DataSet... dataSets) throws SSQLException {
		if (!columns.containsKey(name)) throw new SSQLException(name + " is not a valid column name");
		if (dataSets == null) throw new SSQLException("The DataSet array must not be null");
		if (dataSets.length == 0) throw new SSQLException("The DataSet array must not be empty");

		return exists(dataSets);
	}

	private <T> T getValueFallback(Class<T> clazz, ResultSet results, String name) throws SQLException {
		if (clazz.equals(String.class)) return (T) results.getString(name);
		if (clazz.equals(Long.class)) return (T) (Long) results.getLong(name);
		if (clazz.equals(Integer.class)) return (T) (Integer) results.getInt(name);
		if (clazz.equals(Boolean.class)) return (T) (Boolean) results.getBoolean(name);
		// TODO add more
		return null;
	}

	/**
	 * Gets the name of this table
	 *
	 * @return The tableName
	 */
	public String getName() { return tableName; }

	@FunctionalInterface
	private interface InjectorConsumer {
		public void consume(int index, String s);
	}

	/**
	 * The functional interface to get called when no return value is needed
	 */
	@FunctionalInterface
	private interface ResultsConsumer {

		public void consume(ResultSet results) throws SQLException;
	}

	/**
	 * The functional interface to get called when a return value is needed
	 */
	@FunctionalInterface
	private interface ResultsSupplier<T> {

		public T apply(ResultSet results) throws SQLException;
	}

}
