package echo.configuration;

import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

import javax.servlet.ServletContext;

@RewriteConfiguration
public class RewriteConfigurationProvider extends HttpConfigurationProvider {
    @Override
    public Configuration getConfiguration(ServletContext servletContext) {
        return ConfigurationBuilder.begin()
                .addRule(Join.path("/login").to("/login.xhtml"))
                .addRule(Join.path("/").to("/login.xhtml"))
                .addRule(Join.path("/dashboard").to("/dashboard.xhtml"))
                .addRule(Join.path("/person-config").to("/person-config.xhtml"))
                .addRule(Join.path("/person-management").to("/person-management.xhtml"))
                .addRule(Join.path("/contract").to("/contract.xhtml"))
                .addRule(Join.path("/workreport").to("/enter-workreport.xhtml"))
                .addRule(Join.path("/timesheet").to("/timesheet-overview.xhtml"));
    }

    @Override
    public int priority() {
        return 10;
    }
}
