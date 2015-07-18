package sample.jersey;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emfjson.jackson.resource.JsonResourceFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import sample.jersey.service.Service;
import sample.jersey.service.SampleService;

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

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				"*",
				new JsonResourceFactory());

		createModel(resourceSet);

		return resourceSet;
	}

	/*
		Creates a sample ecore model and an instance
	 */
	private void createModel(ResourceSet resourceSet) {
		EPackage p = EcoreFactory.eINSTANCE.createEPackage();
		p.setName("model");
		p.setNsPrefix("model");
		p.setNsURI("http://example.org/model");

		EClass user = EcoreFactory.eINSTANCE.createEClass();
		user.setName("User");

		EAttribute name = EcoreFactory.eINSTANCE.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);

		user.getEStructuralFeatures().add(name);
		p.getEClassifiers().add(user);

		resourceSet
				.createResource(URI.createURI("http://example.org/model"))
				.getContents()
				.add(p);

		EObject u1 = EcoreUtil.create(user);
		u1.eSet(name, "u1");

		resourceSet
				.createResource(URI.createURI("http://example.org/u1"))
				.getContents()
				.add(u1);
	}

}
