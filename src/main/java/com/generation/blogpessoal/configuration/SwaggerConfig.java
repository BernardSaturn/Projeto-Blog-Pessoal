package com.generation.blogpessoal.configuration;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

@Configuration
public class SwaggerConfig {
	
	@Bean
	OpenAPI springBlogPessoalOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Projeto Blog Pessoal Bernardo")
						.description("Projeto Blog Pessoal Desenvolvido na Generation Brasil")
						.version ("v0.0.1")
						.license(new License()
								.name("Bernardo Henrique")
								.url("https://www.linkedin.com/in/bernardohenrique/"))
						.contact(new Contact()
								.name("Bernardo Henrique")
								.url("https://github.com/BernardSaturn")
								.email("bernardo200114@hotmail.com")))
				.externalDocs (new ExternalDocumentation ()
						.description ("Github")
						.url("https://github.com/BernardSaturn/Projeto-Blog-Pessoal"));
	}
	
	@Bean
	OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {
		
		return openApi -> {
			openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations()
					.forEach(operation -> {
						
						ApiResponses apiResponses = operation.getResponses();
						
						apiResponses.addApiResponse("200", createApiResponse("Sucesso!!"));
						apiResponses.addApiResponse("201", createApiResponse("Objeto Persistido!!"));
						apiResponses.addApiResponse("204", createApiResponse("Objeto Excluído!!"));
						apiResponses.addApiResponse("400", createApiResponse("Erro na Requisição!!"));
						apiResponses.addApiResponse("401", createApiResponse("Acesso Não Autorizado!!"));
						apiResponses.addApiResponse("403", createApiResponse("Acesso Proibido!!"));
						apiResponses.addApiResponse("404", createApiResponse("Objeto Não Encontrado!!"));
						apiResponses.addApiResponse("500", createApiResponse("Erro na Aplicação!!"));
					
					})
					);
		};
	}
							
	private ApiResponse createApiResponse(String message) {
		
		return new ApiResponse().description(message);
	}

}
