package sample.jersey;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emfjson.jackson.resource.JsonResourceFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import sample.jersey.model.ModelPackage;
import sample.jersey.service.SampleService;
import sample.jersey.service.Service;

import javax.inject.Singleton;

public class SampleBinder extends AbstractBinder {

	@Override
	protected void configure() {
		bind(SampleService.class).to(Service.class).in(Singleton.class);
		bind(createResourceSet()).to(ResourceSet.class);
	}

	/*
		Configure the resourceSet and creates a model.
	 */
	private ResourceSet createResourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(ModelPackage.eNS_URI, ModelPackage.eINSTANCE);

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				"*",
				new JsonResourceFactory());

		return resourceSet;
	}

}
