package KrkrDataLoader.network;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KrkrDocConfig
{
	@Bean
	public OpenAPI openAPI()
	{
		return new OpenAPI().info(new Info().title("KrkrDataLoader API")
											.description(
													"API for Krkr data loader, provide diverse functions to handle Krkr game data.")
											.version("v1.0.0")
											.license(null)
											.contact(new Contact().name("pjh456").email("147148383@qq.com")));
	}
	
	@Bean
	public GroupedOpenApi sceneAPI()
	{
		return GroupedOpenApi.builder().group("Scene").pathsToMatch("/krkr/api/scene/**").build();
	}
	
	@Bean
	public GroupedOpenApi configAPI()
	{
		return GroupedOpenApi.builder().group("Config").pathsToMatch("/krkr/api/config/**").build();
	}
}
