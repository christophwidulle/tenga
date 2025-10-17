package de.christophdick.tenga.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class SpaWebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);

                        // If resource exists, return it
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }

                        // Don't forward API requests to index.html
                        if (resourcePath.startsWith("api/") ||
                            resourcePath.startsWith("mcp/") ||
                            resourcePath.startsWith("swagger-ui/") ||
                            resourcePath.startsWith("v3/api-docs/") ||
                            resourcePath.startsWith("actuator/")) {
                            return null;
                        }

                        // For all other requests (Vue routes), return index.html
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
