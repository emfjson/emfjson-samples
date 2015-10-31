package sample.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emfjson.jackson.module.EMFModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration
public class SampleConfiguration extends RepositoryRestMvcConfiguration {

	@Override
	protected void configureJacksonObjectMapper(ObjectMapper objectMapper) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		objectMapper.registerModule(new EMFModule(resourceSet));
	}

}
