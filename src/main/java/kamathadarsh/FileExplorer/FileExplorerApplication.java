package kamathadarsh.FileExplorer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties()
@SpringBootApplication
public class FileExplorerApplication {
	public static void main(String[] args) {
		SpringApplication.run(FileExplorerApplication.class, args);
	}

}
