package at.ac.fhcampuswien.fhmdb.ui;

import javafx.util.Callback;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerFactory implements Callback<Class<?>, Object> {

    private final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    @Override
    public Object call(Class<?> controllerClass) {
        return instances.computeIfAbsent(controllerClass, cls -> {
            try {
                return cls.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Controller-Instanzierung fehlgeschlagen: " + cls, e);
            }
        });
    }
}