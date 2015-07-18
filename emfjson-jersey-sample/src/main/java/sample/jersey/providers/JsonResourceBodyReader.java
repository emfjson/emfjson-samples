package sample.jersey.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emfjson.jackson.module.EMFModule;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes("application/json")
public class JsonResourceBodyReader implements MessageBodyReader<Resource> {

	@Inject
	ResourceSet resourceSet;

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type == Resource.class || Resource.class.isAssignableFrom(type);
	}

	@Override
	public Resource readFrom(Class<Resource> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new EMFModule(resourceSet));

		Resource resource;
		try {
			resource = mapper.readValue(entityStream, Resource.class);
		} catch (IOException e) {
			return null;
		}

		return resource;
	}

}
