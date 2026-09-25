package org.flexitech.projects.erp.admin.configs;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

	@Value("${image.path}")
	private String imagePath;

	@Value("${image.context.path}")
	private String imageContextPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(imageContextPath + "**").addResourceLocations("file:" + imagePath + "/");
	}

	@Bean
	public String imagePath() {
		return imagePath;
	}

	@Bean
	public String imageContextPath() {
		return imageContextPath;
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.requestFactory(() -> createRequestFactory())
				.additionalInterceptors(Collections.singletonList(loggingInterceptor()))
				.errorHandler(new ResponseErrorHandler() {

					@Override
					public boolean hasError(ClientHttpResponse response) throws IOException {
						return response.getStatusCode().is4xxClientError()
								|| response.getStatusCode().is5xxServerError();
					}

					@Override
					public void handleError(ClientHttpResponse response) throws IOException {
						log.error("API call error with status code: {}, message: {}", response.getStatusCode(),
								response.getStatusText());
					}
				}).build();
	}

	private ClientHttpRequestFactory createRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(Duration.ofSeconds(5));
		factory.setReadTimeout(Duration.ofSeconds(30));
		return factory;
	}

	private ClientHttpRequestInterceptor loggingInterceptor() {
		return (request, body, execution) -> {
			log.debug("Request: " + request.getMethod() + " " + request.getURI());
			return execution.execute(request, body);
		};
	}
}
