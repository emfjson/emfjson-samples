package example.server.store;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;

public class SampleStore implements ModelStore {

	@Inject
	ResourceSet resourceSet;

	@Override
	public Resource get(String id) throws IOException {
		Resource resource = resource(id);
		resource.load(null);

		return resource;
	}

	@Override
	public java.net.URI save(String id, Resource resource) throws IOException, URISyntaxException {
		if (resource.getResourceSet() == null) {
			resourceSet.getResources().add(resource);
		}

		resource.setURI(URI.createURI("http://resources/" + id));
		resource.save(null);

		return new java.net.URI(resource.getURI().toString());
	}

	@Override
	public void delete(String id) throws Exception {
		Resource resource = resource(id);
		resource.delete(null);
	}

	public Resource resource(String id) {
		try {
			return resourceSet.getResource(URI.createURI("http://resources/" + id), true);
		} catch (Exception e) {
			return resourceSet.getResource(URI.createURI("http://resources/" + id), false);
		}
	}

	public java.net.URI create() throws IOException, URISyntaxException {
		String uuid = EcoreUtil.generateUUID();
		Resource resource = resource(uuid);
		resource.save(null);

		return new java.net.URI(resource.getURI().toString());
	}

}
