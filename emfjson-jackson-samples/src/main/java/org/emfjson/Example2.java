package org.emfjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emfjson.jackson.JacksonOptions;
import org.emfjson.jackson.module.EMFModule;
import org.emfjson.model.Address;
import org.emfjson.model.ModelFactory;
import org.emfjson.model.ModelPackage;
import org.emfjson.model.User;
import org.joda.time.DateTime;

import java.io.IOException;

/**
 * This example demonstrates the integration of EMFJson with the Jackson API.
 */
public class Example2 {

	public static void main(String[] args) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();

		// Register EMFModule to handle EObject and Resource types.
		// Set as option the type of the object we want to read
		JacksonOptions options = new JacksonOptions.Builder()
				.withRoot(ModelPackage.Literals.USER)
				.build();

		mapper.registerModule(new EMFModule(new ResourceSetImpl(), options));

		// First we create a JSON object using Jackson API
		final ObjectNode objectNode = mapper.createObjectNode();
		objectNode.put("name","John Doe");

		String stringNode = mapper.writeValueAsString(objectNode);

		System.out.println(stringNode);

		// Convert object node to an EObject.

		User user = (User) mapper.readValue(stringNode, EObject.class);

		// We add more information about the user
		user.setBirthDate(new DateTime(1975, 10, 5, 12, 0, 0, 0).toDate());

		Address address = ModelFactory.eINSTANCE.createAddress();
		address.setCity("Paris");
		address.setCountry("France");
		address.setNumber(12);
		address.setStreet("Montmartre");
		user.getAddresses().add(address);

		// Write it back into a JSON Object

		System.out.println(mapper.writeValueAsString(user));
	}

}
