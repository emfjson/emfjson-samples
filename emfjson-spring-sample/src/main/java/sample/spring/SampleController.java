package sample.spring;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.emfjson.jackson.resource.JsonResource;
import org.emfjson.model.ModelFactory;
import org.emfjson.model.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class SampleController {

	@RequestMapping("/users")
	public Resource get(@RequestParam(value="name", defaultValue="u1") String name) {
		final Resource resource = new JsonResource(URI.createURI("/users/" + name));
		final User u1 = ModelFactory.eINSTANCE.createUser();
		u1.setId("u1");
		u1.setName("Momo");
		resource.getContents().add(u1);

		return resource;
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public void post(@RequestBody Resource resource) {
		System.out.println(resource);
	}

}
