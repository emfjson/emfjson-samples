package sample.jersey;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sample.jersey.providers.JsonResourceBodyReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class SampleResourceTest {

	private HttpServer server;
	private Client client;

	@Before
	public void setUp() throws Exception {
		server = Main.getServer();
		server.start();

		client = ClientBuilder.newClient()
				.register(new SampleBinder())
				.register(JacksonFeature.class)
				.register(JsonResourceBodyReader.class);
	}

	@After
	public void tearDown() throws Exception {
		server.stop(0);
	}

	@Test
	public void testGet() throws Exception {
		WebTarget target = client.target(Main.uri + "u1");

		Resource resource = target.request(MediaType.APPLICATION_JSON).get(Resource.class);
		assertEquals(1, resource.getContents().size());

		EObject u1 = resource.getContents().get(0);
		assertEquals("User", u1.eClass().getName());
		assertEquals("u1", u1.eGet(u1.eClass().getEStructuralFeature("name")));
	}

	@Test
	public void testPost() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.createObjectNode()
				.put("name", "u2");

		Response response = client.target(Main.uri)
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(node, MediaType.APPLICATION_JSON));

		assertEquals(201, response.getStatus());
		assertEquals("http://localhost:8080/u2", response.getLocation().toString());

		WebTarget target = client.target(Main.uri + "u2");

		Resource resource = target.request(MediaType.APPLICATION_JSON).get(Resource.class);
		assertEquals(1, resource.getContents().size());

		EObject u1 = resource.getContents().get(0);
		assertEquals("User", u1.eClass().getName());
		assertEquals("u2", u1.eGet(u1.eClass().getEStructuralFeature("name")));
	}

	@Test
	public void testPut() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.createObjectNode()
				.put("name", "u3");

		Response response = client.target(Main.uri + "u1")
				.request(MediaType.APPLICATION_JSON)
				.put(Entity.entity(node, MediaType.APPLICATION_JSON));

		assertEquals(200, response.getStatus());

		Resource resource = response.readEntity(Resource.class);

		assertEquals(1, resource.getContents().size());

		EObject u1 = resource.getContents().get(0);
		assertEquals("User", u1.eClass().getName());
		assertEquals("u3", u1.eGet(u1.eClass().getEStructuralFeature("name")));
	}

	@Test
	public void testDelete() {
		Response response = client.target(Main.uri + "u1")
				.request()
				.delete();

		assertEquals(204, response.getStatus());
	}

}