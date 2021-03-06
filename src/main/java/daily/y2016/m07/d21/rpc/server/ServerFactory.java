package daily.y2016.m07.d21.rpc.server;

import daily.y2016.m07.d21.rpc.api.RpcFactory;

public class ServerFactory {

	private static Server server;
	
	static {
		server = new Server(RpcFactory.DEFAULT_PORT, 10);
		server.start();
	}
	
	public static Server getServer() {
		return server;
	}
}
