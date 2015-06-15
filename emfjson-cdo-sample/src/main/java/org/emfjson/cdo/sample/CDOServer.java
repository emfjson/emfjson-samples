package org.emfjson.cdo.sample;

import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.jvm.IJVMConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.emf.cdo.server.CDOServerUtil.createRepository;

public class CDOServer {

	private final IManagedContainer container;
	private final IRepository repository;
	private final DataSource dataSource;
	private final String name;
	private IJVMConnector connector;

	public CDOServer(String name) {
		this.name = name;
		this.container = getContainer();
		this.dataSource = createDataSource();
		this.repository = createRepository(getName(), createStore(), createProperties());
	}

	public void start() {
		container.activate();

		CDOServerUtil.addRepository(container, repository);
		JVMUtil.getAcceptor(container, getName());

		connector = JVMUtil.getConnector(container, getName());
	}

	public void stop() {
		container.deactivate();
		connector.close();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public DataSource createDataSource() {
		final JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:mem:" + getName() + ";DB_CLOSE_DELAY=-1");

		return dataSource;
	}

	public IDBAdapter getAdapter() {
		return new H2Adapter();
	}

	public String getName() {
		return name;
	}

	public IConnector getConnector() {
		if (connector == null && container.isActive()) {
			connector = JVMUtil.getConnector(container, getName());
		}
		return connector;
	}

	protected IStore createStore() {
		final IDBAdapter dbAdapter = getAdapter();

		final IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true);
		final IDBConnectionProvider dbConnectionProvider = dbAdapter.createConnectionProvider(getDataSource());

		return CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);
	}

	protected Map<String, String> createProperties() {
		final Map<String, String> props = new HashMap<>();
		props.put(IRepository.Props.OVERRIDE_UUID, getName());
		props.put(IRepository.Props.SUPPORTING_AUDITS, "true");
		props.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
		props.put(IRepository.Props.ID_GENERATION_LOCATION, "STORE");
		props.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");

		return props;
	}

	protected IManagedContainer getContainer() {
		final IManagedContainer container = ContainerUtil.createContainer();
		Net4jUtil.prepareContainer(container);
		JVMUtil.prepareContainer(container);
		CDONet4jUtil.prepareContainer(container);
		CDONet4jServerUtil.prepareContainer(container);

		return container;
	}

	public CDOSessionConfiguration getSessionConfiguration() {
		CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
		configuration.setConnector(getConnector());
		configuration.setRepositoryName(getName());

		return configuration;
	}

}