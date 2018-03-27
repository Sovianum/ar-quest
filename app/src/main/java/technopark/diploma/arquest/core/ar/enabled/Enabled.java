package technopark.diploma.arquest.core.ar.enabled;

public class Enabled implements IEnabled {
    private boolean enabled;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
