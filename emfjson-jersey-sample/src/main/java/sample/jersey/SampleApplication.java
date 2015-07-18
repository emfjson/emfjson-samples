package sample.jersey;

import org.glassfish.jersey.jackson.JacksonFeature;
import sample.jersey.providers.JsonResourceBodyReader;
import sample.jersey.providers.JsonResourceBodyWriter;

import javax.ws.rs.core.Application;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SampleApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		final Set<Class<?>> classes = new HashSet<>();
		classes.add(SampleResource.class);
		classes.add(JsonResourceBodyReader.class);
		classes.add(JsonResourceBodyWriter.class);
		classes.add(JacksonFeature.class);

		return Collections.unmodifiableSet(classes);
	}

	@Override
	public Set<Object> getSingletons() {
		final Set<Object> singletons = new HashSet<>();
		singletons.add(new SampleBinder());

		return Collections.unmodifiableSet(singletons);
	}

}
