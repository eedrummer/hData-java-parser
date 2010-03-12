/*
 *
 *
 *    Copyright 2009 The MITRE Corporation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.projecthdata.hdata.hrf;

/**
 *
 * @author GBEUCHELT
 */
public class ExtensionMissingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2978297922438544979L;


	/**
     * Creates a new instance of <code>ExtensionMissingException</code> without detail message.
     */
    public ExtensionMissingException() {
    }


    /**
     * Constructs an instance of <code>ExtensionMissingException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ExtensionMissingException(String msg) {
        super(msg);
    }
}
