package co.edu.eci.blueprints.p1.services;

import java.util.Set;
import org.springframework.stereotype.Service;

import co.edu.eci.blueprints.p1.filters.BlueprintsFilter;
import co.edu.eci.blueprints.p1.model.Blueprint;
import co.edu.eci.blueprints.p1.persistence.BlueprintNotFoundException;
import co.edu.eci.blueprints.p1.persistence.BlueprintPersistence;
import co.edu.eci.blueprints.p1.persistence.BlueprintPersistenceException;

@Service
public class BlueprintsServices {

    private final BlueprintPersistence persistence;
    private final BlueprintsFilter filter;

    public BlueprintsServices(BlueprintPersistence persistence, BlueprintsFilter filter) {
        this.persistence = persistence;
        this.filter = filter;
    }

    public void addNewBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        persistence.saveBlueprint(bp);
    }

    public Set<Blueprint> getAllBlueprints() {
        return persistence.getAllBlueprints();
    }

    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        return persistence.getBlueprintsByAuthor(author);
    }

    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        Blueprint bp = persistence.getBlueprint(author, name);
        return filter.filter(bp);
    }

    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        persistence.addPoint(author, name, x, y);
    }
}