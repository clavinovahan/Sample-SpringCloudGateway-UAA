package sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

public class WebClientConfig {
	private static final Logger LOG = LoggerFactory.getLogger(WebClientConfig.class);

	@Bean
	public WebClient webClient(final ClientHttpConnector clientHttpConnector) {
		return WebClient.builder()
				.clientConnector(clientHttpConnector)
				.build();
	}
	
	@Bean
	public ClientHttpConnector clientHttpConnector() {
		return new ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.newConnection()));
	}
}
