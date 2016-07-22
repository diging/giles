package edu.asu.giles.db4o;

import java.io.Serializable;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;

public class DatabaseManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3325272288078647257L;
	private ObjectServer server;
	private String databasePath;

	public void init() {
		close();
		ServerConfiguration configuration = Db4oClientServer
				.newServerConfiguration();
		configuration.file().blockSize(80);
		server = Db4oClientServer.openServer(configuration, databasePath, 0);
	}

	public ObjectContainer getClient() {
		ObjectContainer container = server.openClient();
		return container;
	}

	public String getDatabasePath() {
		return databasePath;
	}

	public void setDatabasePath(String databasePath) {
		this.databasePath = databasePath;
	}

	private void close() {
		if (server != null) {
			server.close();
		}
		server = null;
	}

	public void shutdown() {
		close();
	}
}