package sample.jersey.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.IOException;

public interface Service {

	Resource get(String id) throws IOException;

	Resource create(JsonNode value) throws JsonProcessingException;

	void delete(String id) throws IOException;

	Resource update(String id, JsonNode node);

}
