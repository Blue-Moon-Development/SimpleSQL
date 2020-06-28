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

package org.bluemoondev.simplesql.exceptions;

import java.io.IOException;

/**
 * <strong>Project:</strong> SimpleSQL<br>
 * <strong>File:</strong> SSQLException.java<br>
 * <p>
 * An exception thrown in the event of a SimpleSQL error
 * </p>
 *
 * @author <a href = "https://bluemoondev.org"> Matt</a>
 */
public class SSQLException extends Exception {

	/**
	 * Creates a new instance of <code>SSQLException</code> without detail message.
	 */
	public SSQLException() {
		super();
	}

	/**
	 * Constructs an instance of <code>SSQLException</code> with the specified
	 * detail message.
	 * 
	 * @param msg the detail message.
	 */
	public SSQLException(String msg) {
		super(msg);
	}
}
