package sample.jersey;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.emf.ecore.resource.Resource;
import sample.jersey.service.Service;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

@Path("/")
public class SampleResource {

	@Inject
	Service store;

	@GET
	@Path("/{id}")
	@Produces("application/json")
	public Response get(@PathParam("id") String id) {
		Resource resource;
		try {
			resource = store.get(id);
		} catch (IOException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		return Response.ok(resource).build();
	}

	@POST
	@Consumes("application/json")
	public Response post(JsonNode node) {
		Resource resource;
		try {
			resource = store.create(node);
		} catch (Exception e) {
			return Response.serverError().build();
		}

		final URI uri = UriBuilder.fromPath(resource.getURI().path()).build();

		return Response.created(uri).build();
	}

	@PUT
	@Path("/{id}")
	@Produces("application/json")
	@Consumes("application/json")
	public Response put(@PathParam("id") String id, JsonNode node) {
		Resource resource;
		try {
			resource = store.update(id, node);
		} catch (Exception e) {
			return Response.serverError().build();
		}

		return Response.ok(resource).build();
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") String id) {
		try {
			store.delete(id);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}

		return Response.noContent().build();
	}

}
