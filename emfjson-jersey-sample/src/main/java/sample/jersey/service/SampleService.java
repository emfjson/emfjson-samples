package sample.jersey.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emfjson.jackson.JacksonOptions;
import org.emfjson.jackson.module.EMFModule;

import javax.inject.Inject;
import java.io.IOException;

public class SampleService implements Service {

	@Inject
	ResourceSet resourceSet;

	@Override
	public Resource get(String id) throws IOException {
		return resource(id);
	}

	@Override
	public Resource create(JsonNode value) throws JsonProcessingException {
		final EClass root = (EClass) resourceSet.getEObject(
				URI.createURI("http://example.org/model#//User"),
				true);

		final JacksonOptions options = new JacksonOptions.Builder()
				.withRoot(root)
				.build();

		final ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new EMFModule(resourceSet, options));

		final Resource resource = mapper.treeToValue(value, Resource.class);
		resource.setURI(uri(value.get("name").asText()));

		return resource;
	}

	@Override
	public Resource update(String id, JsonNode value) {
		Resource resource;
		try {
			resource = resourceSet.getResource(uri(id), true);
		} catch (Exception e) {
			throw new RuntimeException("Cannot find resource " + id);
		}

		if (resource.getContents().isEmpty()) {
			throw new RuntimeException("Resource should not be empty");
		}

		EObject user = resource.getContents().get(0);
		user.eSet(user.eClass().getEStructuralFeature("name"), value.get("name").asText());

		return resource;
	}

	@Override
	public void delete(String id) throws IOException {
		Resource resource = resource(id);
		resourceSet.getResources().remove(resource);
	}

	public Resource resource(String id) {
		try {
			return resourceSet.getResource(uri(id), true);
		} catch (Exception e) {
			return resourceSet.getResource(uri(id), false);
		}
	}

	private URI uri(String id) {
		return URI.createURI("http://example.org/" + id);
	}

}
