package echo.dao;

import echo.entities.WorkReport;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@Stateless
@LocalBean
public class WorkReportAccess extends SimpleEntityAccess<WorkReport> {

    public WorkReportAccess() {
        super(WorkReport.class);
    }

}
