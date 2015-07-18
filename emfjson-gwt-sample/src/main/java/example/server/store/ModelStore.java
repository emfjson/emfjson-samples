package example.server.store;

import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface ModelStore {

	Resource get(String id) throws IOException;

	URI save(String id, Resource resource) throws IOException, URISyntaxException;

	void delete(String id) throws Exception;

	URI create() throws IOException, URISyntaxException;

}
