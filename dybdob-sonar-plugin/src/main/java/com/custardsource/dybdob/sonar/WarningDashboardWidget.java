package com.custardsource.dybdob.sonar;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWidget;

public class WarningDashboardWidget extends AbstractRubyTemplate implements RubyRailsWidget {
    @Override
    public String getId() {
        return "warning_widget";
    }

    @Override
    public String getTitle() {
        return "Compiler Warnings";
    }

    @Override
    protected String getTemplatePath() {
        return "/com/custardsource/dybdob/sonar/warning_widget.erb";
    }
}
