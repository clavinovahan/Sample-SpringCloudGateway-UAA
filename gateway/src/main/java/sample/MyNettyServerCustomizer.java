package sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import reactor.netty.http.server.HttpServer;



@Component
public class MyNettyServerCustomizer implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

	private static final Logger LOG = LoggerFactory.getLogger(MyNettyServerCustomizer.class);

	@Override
	public void customize(final NettyReactiveWebServerFactory factory) {
		factory.addServerCustomizers(new IdleConnectionsCleanerCustomizer());
	}

	public class IdleConnectionsCleanerCustomizer implements NettyServerCustomizer {
		@Override
		public HttpServer apply(final HttpServer httpServer) {
			return httpServer.tcpConfiguration(tcpServer -> tcpServer.bootstrap(
					serverBootstrap -> {
						// https://netty.io/4.0/api/io/netty/handler/timeout/IdleStateHandler.html
						serverBootstrap
						.childHandler(new IdleStateHandler(10, 10, 10))
						.childHandler(new MyHandler());
						return serverBootstrap;
					}));
		}
	}


	@ChannelDuplexHandler.Sharable
	public class MyHandler extends ChannelDuplexHandler {              
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			LOG.debug("Idle event comes!");
			if (evt instanceof IdleStateEvent) {
				LOG.debug("Channle closed!");
				ctx.close();
			}
		}
	}
}

 