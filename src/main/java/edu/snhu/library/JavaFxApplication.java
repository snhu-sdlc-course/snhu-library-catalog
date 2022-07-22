package edu.snhu.library;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import edu.snhu.library.events.StageReadyEvent;
import edu.snhu.library.models.Book;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

import java.io.IOException;
import java.nio.file.Path;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class JavaFxApplication extends Application {

    private static final int mongoPort = 27017;
    private static final String mongoDatabase = "snhulibrarycatalog";

    private ConfigurableApplicationContext context;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() {
        this.context = new SpringApplicationBuilder(JavaFxApplication.class)
                .initializers(applicationContext -> applicationContext.getBeanFactory().registerResolvableDependency(Application.class, JavaFxApplication.this))
                .run(this.getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Bean
    public MongodConfig mongodConfig() {
        final String userHome = System.getProperty("user.home");
        final Storage dbStorage = new Storage(Path.of(userHome, "snhuLibraryCatalog").toString(), null, 0);
        return ImmutableMongodConfig.builder()
                .replication(dbStorage)
                .version(Version.Main.PRODUCTION)
                .net(new Net("localhost", mongoPort, false))
                .build();
    }

    @Bean("parameters")
    public Parameters getJavaFxParameters() {
        return getParameters();
    }

    @Bean("hostServices")
    public HostServices getJavaFxHostServices() {
        return getHostServices();
    }

    @Bean
    public MongoClient mongoClient() {
        final ConnectionString connectionString = new ConnectionString("mongodb://localhost:" + mongoPort + "/" + mongoDatabase);
        final MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(clientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), mongoDatabase);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public Jackson2RepositoryPopulatorFactoryBean repositoryPopulator(
            @Value("classpath:testdata.json") final Resource testData,
            final MongoTemplate mongoTemplate
    ) throws Exception {
        final long count = mongoTemplate.estimatedCount(Book.class);

        final Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setMapper(objectMapper());

        // Only store data if the database is empty
        if(count < 1) {
            factory.setResources(new Resource[]{testData});
        }

        factory.afterPropertiesSet();
        return factory;
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
