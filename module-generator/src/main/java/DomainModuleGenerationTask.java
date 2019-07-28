import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class DomainModuleGenerationTask extends DefaultTask {
    private DomainComponent component;

    public void setComponent(DomainComponent component) {
        this.component = component;
    }

    @Input
    public DomainComponent getComponent() {
        return component;
    }

    @TaskAction
    public void generateModule() {
        getProject().getComponents().add(component);
    }
}
