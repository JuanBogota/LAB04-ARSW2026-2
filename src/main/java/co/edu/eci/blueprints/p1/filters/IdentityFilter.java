package co.edu.eci.blueprints.p1.filters;

import org.springframework.stereotype.Component;
import co.edu.eci.blueprints.p1.model.Blueprint;

@Component
public class IdentityFilter implements BlueprintsFilter {
    @Override
    public Blueprint filter(Blueprint blueprint) {
        return blueprint;
    }
}