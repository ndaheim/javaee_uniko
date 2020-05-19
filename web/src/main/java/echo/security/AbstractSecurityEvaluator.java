package echo.security;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractSecurityEvaluator {

    protected boolean invoke(String methodName) {
        try {
            Method method = this.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return (boolean) method.invoke(this);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    protected boolean invoke(String methodName, Object target) {
        try {
            Method method = this.getClass().getDeclaredMethod(methodName, target.getClass());
            method.setAccessible(true);
            return (boolean) method.invoke(this, target);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }

    protected boolean evaluate(String expression, Object... args) {
        Matcher matcher = Pattern.compile("#(?<method>.+?)\\(\\$(?<argument>.+?)\\)").matcher(expression);
        while (matcher.find()) {
            boolean bResult = false;
            MatchResult result = matcher.toMatchResult();
            if (result.group(2).equals("null")) {
                bResult = invoke(result.group(1));
            } else {
                bResult = invoke(result.group(1), args[Integer.parseInt(result.group(2))]);
            }
            expression = expression.replace(result.group(0), Boolean.toString(bResult));
        }
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        try {
            return (boolean) engine.eval(expression);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return false;

    }
}
