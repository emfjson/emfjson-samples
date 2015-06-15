package org.emfjson.cdo.sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.emfjson.jackson.JacksonOptions;
import org.emfjson.jackson.databind.ser.ResourceSerializer;
import org.emfjson.jackson.module.EMFModule;
import org.emfjson.model.ModelFactory;
import org.emfjson.model.ModelPackage;
import org.emfjson.model.User;

public class Main {

	public static void main(String[] args) throws Exception {
		CDOServer server = new CDOServer("sample");
		server.start();

		CDOSessionConfiguration configuration = server.getSessionConfiguration();
		CDOSession session = configuration.openSession();
		session.getPackageRegistry().putEPackage(ModelPackage.eINSTANCE);

		CDOTransaction transaction = session.openTransaction();
		createUsers(transaction);

		transaction.commit();

		ObjectMapper mapper = new ObjectMapper();
		JacksonOptions options = new JacksonOptions.Builder()
				.withID(true)
				.withIdSerializer(new CDOIDSerializer())
				.withReferenceSerializer(new CDOReferenceSerializer())
				.withReferenceDeserializer(new CDOReferenceDeserializer(transaction))
				.build();

		EMFModule module = new EMFModule(transaction.getResourceSet(), options);
		module.addSerializer(CDOResource.class, new ResourceSerializer());
		mapper.registerModule(module);

		System.out.println(mapper.valueToTree(transaction.getResource("u1")));
		System.out.println(mapper.valueToTree(transaction.getResource("u2")));

		User u2 = (User) transaction.getResource("u2").getContents().get(0);

		JsonNode node = data(mapper, u2);

		System.out.println("Create resource r3 from " + node);
		Resource r3 = mapper.reader()
				.withAttribute("resource", transaction.createResource("u3"))
				.treeToValue(node, Resource.class);

		User u3 = (User) r3.getContents().get(0);
		System.out.println(u3.getName() + " is friend with " + u2.getName() + " > " + u3.getFriends().contains(u2));

		transaction.close();
		session.close();

		server.stop();
	}

	private static void createUsers(CDOTransaction transaction) {
		CDOResource r1 = transaction.getOrCreateResource("u1");
		r1.getContents().clear();

		User u1 = ModelFactory.eINSTANCE.createUser();
		u1.setName("Bob");
		r1.getContents().add(u1);

		CDOResource r2 = transaction.getOrCreateResource("u2");
		r2.getContents().clear();

		User u2 = ModelFactory.eINSTANCE.createUser();
		u2.setName("Peter");
		u2.getFriends().add(u1);
		r2.getContents().add(u2);
	}

	private static JsonNode data(ObjectMapper mapper, EObject o) {
		CDOObject oo = CDOUtil.getCDOObject(o);
		long i = CDOIDUtil.getLong(CDOIDUtil.getCDOID(oo));

		return mapper.createObjectNode()
				.put("eClass", "http://www.foo.org/model#//User")
				.put("name", "Frank")
				.set("friends", mapper.createArrayNode()
						.add(i));
	}

}
