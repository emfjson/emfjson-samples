package example.server;

import example.server.providers.JsonResourceBodyReader;
import example.server.providers.JsonResourceBodyWriter;
import example.server.store.ModelStore;
import example.server.store.SampleStore;
import example.shared.model.ModelPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emfjson.jackson.resource.JsonResourceFactory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("api")
public class ServerApplication extends ResourceConfig {

	public ServerApplication() {
		register(new AbstractBinder() {
			@Override
			protected void configure() {
				bind(createResourceSet()).to(ResourceSet.class);
				bind(SampleStore.class).to(ModelStore.class).in(Singleton.class);
			}
		});

		packages("example.server.resources");

		register(JsonResourceBodyReader.class);
		register(JsonResourceBodyWriter.class);
	}

	private static ResourceSet createResourceSet() {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(ModelPackage.eNS_URI, ModelPackage.eINSTANCE);

		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				"*",
				new JsonResourceFactory());

		resourceSet.getURIConverter().getURIMap().put(
				URI.createURI("http://resources/"),
				URI.createURI("files/"));

		return resourceSet;
	}

}
