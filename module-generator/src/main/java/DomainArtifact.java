import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.Artifact;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.jetbrains.annotations.NotNull;

public class DomainArtifact implements SoftwareComponent, Artifact {
    private final String name;
    private final Property<Configuration> configuration;

    public DomainArtifact(String name, ObjectFactory objectFactory) {
        this.name = name;
        this.configuration = objectFactory.property(Configuration.class);
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration.set(configuration);
    }

    public Property<Configuration> getConfiguration() {
        return configuration;
    }
}
