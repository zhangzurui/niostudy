package daily.y2016.m08.d18.netty.example.echo;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class EchoServer {

	public static void main(String[] args) {
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(), 
						Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new EchoServerHandler());
			}
		});
		
		bootstrap.bind(new InetSocketAddress(8080));
	}
}
