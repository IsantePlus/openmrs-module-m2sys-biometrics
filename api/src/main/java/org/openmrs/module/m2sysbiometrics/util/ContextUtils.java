package org.openmrs.module.m2sysbiometrics.util;

import org.openmrs.api.context.Context;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import java.util.List;

public final class ContextUtils {

    public static <T> T getFirstRegisteredComponent(Class<T> clazz) {
        List<T> list = Context.getRegisteredComponents(clazz);
        if (list.isEmpty()) {
            throw new M2SysBiometricsException(String.format("Not found any instances of '%s' component in the context",
                    clazz.getName()));
        }
        return Context.getRegisteredComponents(clazz).get(0);
    }

    private ContextUtils() {

    }
}
