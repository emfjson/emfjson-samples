package org.emfjson;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emfjson.jackson.JacksonOptions;
import org.emfjson.jackson.module.EMFModule;
import org.emfjson.jackson.resource.JsonResource;
import org.emfjson.jackson.resource.JsonResourceFactory;
import org.emfjson.model.Address;
import org.emfjson.model.ModelFactory;
import org.emfjson.model.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This example demonstrates how to use UUIDs.
 */
public class Example3 {

	public static void main(String[] args) throws JsonProcessingException {
		final ObjectMapper mapper = new ObjectMapper();
		final ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new JsonResourceFactory() {
			@Override
			public Resource createResource(URI uri) {
				return new JsonResource(uri) {
					protected boolean useUUIDs() {
						return true;
					}
				};
			}
		});

		mapper.registerModule(new EMFModule(resourceSet,
				new JacksonOptions.Builder()
						.withID(true)
						.build()));

		User u1 = ModelFactory.eINSTANCE.createUser();
		User u2 = ModelFactory.eINSTANCE.createUser();
		User u3 = ModelFactory.eINSTANCE.createUser();

		Address a1 = ModelFactory.eINSTANCE.createAddress();
		a1.setCity("Prague");
		u1.getAddresses().add(a1);

		u1.setName("Bart");
		u2.setName("Kenny");
		u3.setName("Kyle");

		u1.getFriends().add(u2);
		u1.getFriends().add(u3);

		u2.getFriends().add(u1);

		resourceSet.createResource(URI.createURI("user-1")).getContents().add(u1);
		resourceSet.createResource(URI.createURI("user-2")).getContents().add(u2);
		resourceSet.createResource(URI.createURI("user-3")).getContents().add(u3);

		System.out.println(mapper.writeValueAsString(u1));
		System.out.println(mapper.writeValueAsString(u2));
		System.out.println(mapper.writeValueAsString(u3));
	}

}
