package com.bnymellon.scratch;

import java.io.ByteArrayOutputStream;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonWriter;

/**
 * Simple JSON printing example, just to show use of the JsonBuilderFactory and
 * JsonObject classes.
 * 
 * @author D. Roesch
 * 
 */
public class Main {

	/**
	 * Entry point
	 * 
	 * @param args
	 *            Ignored
	 */
	public static void main(final String[] args) {
		try {
			new Main().run();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Build a JSON object and print it.
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception {

		// Create
		final JsonBuilderFactory f = Json.createBuilderFactory(null);
		final JsonObject value = f.createObjectBuilder().add("firstName", "John").add("lastName", "Smith")
				.add("age", 25)
				.add("address",
						f.createObjectBuilder().add("streetAddress", "21 2nd Street").add("city", "New York")
								.add("state", "NY").add("postalCode", "10021"))
				.add("phoneNumber",
						f.createArrayBuilder()
								.add(f.createObjectBuilder().add("type", "home").add("number", "212 555-1234"))
								.add(f.createObjectBuilder().add("type", "fax").add("number", "646 555-4567")))
				.build();

		// Print
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final JsonWriter w = Json.createWriter(os);
		w.writeObject(value);
		w.close();

		final String s = new String(os.toByteArray(), "UTF-8");

		// Display
		System.out.println(s);
	}
}
