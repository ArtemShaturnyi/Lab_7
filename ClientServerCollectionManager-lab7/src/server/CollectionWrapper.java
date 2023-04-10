package server;

import entities.Car;
import entities.Coordinates;
import entities.HumanBeing;
import entities.WeaponType;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class CollectionWrapper {
    private CopyOnWriteArrayList<HumanBeing> collection;
    private final LocalDateTime initializationDateTime;

    public CollectionWrapper() {
        collection = new CopyOnWriteArrayList<>();
        initializationDateTime = LocalDateTime.now();
    }

    public CollectionWrapper(ResultSet resultSet) throws SQLException {
        initializationDateTime = LocalDateTime.now();
        collection = new CopyOnWriteArrayList<>();

        while(resultSet.next()) {
            HumanBeing humanBeing = new HumanBeing();
            humanBeing.setId(resultSet.getInt("id"));
            humanBeing.setName(resultSet.getString("name"));
            humanBeing.setCreationDate(resultSet.getDate("created_by").toLocalDate().atStartOfDay());
            humanBeing.setRealHero(resultSet.getBoolean("real_hero"));
            humanBeing.setHasToothpick(resultSet.getBoolean("has_toothpick"));
            humanBeing.setImpactSpeed(resultSet.getInt("impact_speed"));
            humanBeing.setSoundtrackName(resultSet.getString("soundtrack_name"));
            humanBeing.setMinutesOfWaiting(resultSet.getLong("minutes_of_waiting"));
            humanBeing.setWeaponType(WeaponType.valueOf(resultSet.getString("weapon_type")));
            humanBeing.setCar(new Car(resultSet.getString("car_name")));
            humanBeing.setCoordinates(new Coordinates(resultSet.getDouble("coord_x"), resultSet.getLong("coord_y")));
            humanBeing.setAuthorId(resultSet.getInt("author_id"));
            collection.add(humanBeing);
        }
    }


    public Collection<HumanBeing> getCollection() {
        return collection;
    }


    public void setCollection(Collection<HumanBeing> collection) {
        this.collection = (CopyOnWriteArrayList<HumanBeing>) collection;
    }

    public LocalDateTime getInitializationDateTime() {
        return initializationDateTime;
    }


    public void add(HumanBeing humanBeing) {
        collection.add(humanBeing);
    }

    public boolean update(int id, HumanBeing humanBeing, int userId) {
        HumanBeing human = collection.stream().
                filter(humanCmp -> humanCmp.getId() == id && humanCmp.getAuthorId() == userId)
                .findAny()
                .orElse(null);

        if(human == null) {
            return false;
        }

        collection.remove(human);

        collection.add(humanBeing);
        humanBeing.setId(id);
        humanBeing.setAuthorId(userId);
        return true;
    }

    public HumanBeing find(int id) {
        return collection.stream().filter(human -> human.getId() == id).findAny().orElse(null);
    }


    public boolean removeById(int id, int userId) {
        return collection.removeIf(human -> human.getId() == id && human.getAuthorId() == userId);
    }

}
