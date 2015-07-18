package sample.jersey;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

public class Main {

	static final URI uri = UriBuilder.fromUri("http://localhost/")
			.port(8080)
			.build();

	public static void main(String[] args) throws IOException, InterruptedException {
		final HttpServer server = getServer();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("stopping server...");
				server.stop(0);
			}
		}));
		server.start();

		System.out.println("server started on " + uri);

		Thread.currentThread().join();
	}

	public static HttpServer getServer() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(uri.getPort()), 0);
		HttpHandler handler = RuntimeDelegate
				.getInstance()
				.createEndpoint(new SampleApplication(), HttpHandler.class);

		server.createContext(uri.getPath(), handler);

		return server;
	}

}
