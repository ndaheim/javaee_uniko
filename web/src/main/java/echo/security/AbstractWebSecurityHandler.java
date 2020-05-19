package echo.security;

import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

abstract class AbstractWebSecurityHandler extends AbstractSecurityEvaluator {

    private String redirectTarget = "error.xhtml";

    public void setRedirectTarget(String redirectTarget) {
        this.redirectTarget = redirectTarget;
    }


    public void isPostConstructGranted(String expression) {
        if (!evaluate(expression)) {
            FacesContext context = FacesContext.getCurrentInstance();
            RequestDispatcher dispatcher =
                    ((ServletRequest) context.getExternalContext().getRequest())
                            .getRequestDispatcher(redirectTarget);

            try {
                dispatcher.forward((ServletRequest) context.getExternalContext().getRequest(),
                        (ServletResponse) context.getExternalContext().getResponse());
            } catch (ServletException e) {
                System.out.println("ServletException");
            } catch (IOException e) {
                System.out.println("IOException");
            }
        }
    }

    public void isPostConstructGranted(String expression, Object... args) {
        if (!evaluate(expression, args)) {
            FacesContext context = FacesContext.getCurrentInstance();
            RequestDispatcher dispatcher =
                    ((ServletRequest) context.getExternalContext().getRequest())
                            .getRequestDispatcher(redirectTarget);

            try {
                dispatcher.forward((ServletRequest) context.getExternalContext().getRequest(),
                        (ServletResponse) context.getExternalContext().getResponse());
            } catch (ServletException e) {
                System.out.println("ServletException");
            } catch (IOException e) {
                System.out.println("IOException");
            }
        }
    }

    public void isGranted(String expression) {
        if (!evaluate(expression)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getApplication().getNavigationHandler().handleNavigation(context, null, redirectTarget);
        }
    }

    public void isGranted(String expression, Object... args) {
        if (!evaluate(expression, args)) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getApplication().getNavigationHandler().handleNavigation(context, null, redirectTarget);
        }
    }
}
