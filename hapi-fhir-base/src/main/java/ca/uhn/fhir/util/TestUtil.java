package ca.uhn.fhir.util;

/*
 * #%L
 * HAPI FHIR - Core Library
 * %%
 * Copyright (C) 2014 - 2016 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Locale;

public class TestUtil {
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(TestUtil.class);

	/**
	 * THIS IS FOR UNIT TESTS ONLY
	 * 
	 * When we run the unit tests in cobertura, JUnit doesn't seem to clean up static fields which leads to 
	 * tons of memory being used by the end and the JVM crashes in Travis. Manually clearing all of the
	 * static fields seems to solve this.
	 */
	public static void clearAllStaticFieldsForUnitTest() {
		
		Class<?> theType;
		try {
			throw new Exception();
		} catch (Exception e) {
			StackTraceElement[] st = e.getStackTrace();
			StackTraceElement elem = st[1];
			String clazzName = elem.getClassName();
			try {
				theType = Class.forName(clazzName);
			} catch (ClassNotFoundException e1) {
				throw new Error(e);
			}
		}
		
		for (Field next : Arrays.asList(theType.getDeclaredFields())) {
			if (Modifier.isStatic(next.getModifiers())) {
				if (!Modifier.isFinal(next.getModifiers()) && !next.getType().isPrimitive()) {
					ourLog.info("Clearing value of field: {}", next.toString());
					try {
						next.setAccessible(true);
						next.set(theType, null);
					} catch (Exception e) {
						throw new Error(e);
					}
				}
			}
		}

		/*
		 * Set some system properties randomly after each test.. this is kind of hackish,
		 * but it helps us make sure we don't have any tests that depend on a particular
		 * environment 
		 */
		Locale[] available = { Locale.CANADA, Locale.GERMANY, Locale.TAIWAN };
		Locale newLocale = available[(int) (Math.random() * available.length)];
		Locale.setDefault(newLocale);
		ourLog.info("Tests are running in locale: " + newLocale.getDisplayName());
		if (Math.random() < 0.5) {
			ourLog.info("Tests are using WINDOWS line endings and ISO-8851-1");
			System.setProperty("file.encoding", "ISO-8859-1");
			System.setProperty("line.separator", "\r\n");
		} else {
			ourLog.info("Tests are using UNIX line endings and UTF-8");
			System.setProperty("file.encoding", "UTF-8");
			System.setProperty("line.separator", "\n");
		}

		
	}
	
}
