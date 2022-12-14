package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> userMap = new HashMap<>();
    private Long id = 0L;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getById(Long id) {
            return userMap.get(id);
    }

    @Override
    public User create(User user) {
        id++;
        user.setId(id);
        userMap.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        userMap.replace(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        userMap.remove(id);
    }
}
