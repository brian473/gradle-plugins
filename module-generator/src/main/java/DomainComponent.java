import org.gradle.api.artifacts.Configuration;
import org.gradle.api.component.ComponentWithVariants;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DomainComponent implements ComponentWithVariants {
    private final String name;
    private SetProperty<DomainArtifact> artifacts;
    private Property<Configuration> sharedConfiguration;

    public DomainComponent(String name, ObjectFactory objectFactory) {
        this.name = name;
        this.artifacts = objectFactory.setProperty(DomainArtifact.class);
        this.sharedConfiguration = objectFactory.property(Configuration.class);
    }

    public void setArtifacts(Iterable<DomainArtifact> artifacts) {
        this.artifacts.set(artifacts);
    }

    @NotNull
    @Override
    public Set<DomainArtifact> getVariants() {
        return artifacts.get();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    public Property<Configuration> getSharedConfiguration() {
        return sharedConfiguration;
    }

    public void setSharedConfiguration(Configuration sharedConfiguration) {
        this.sharedConfiguration.set(sharedConfiguration);
    }
}
