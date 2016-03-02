package com.mobilejazz.sample.model;

import com.mobilejazz.cacheio.alternative.RxCache;

public class User {

    private int id;
    private String name;
    private long version = 1L;

    public User(int id, String name, long version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public User() {
    }

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public long getVersion() {
        return version;
    }

    public User setVersion(long version) {
        this.version = version;
        return this;
    }

    public static final class VersionMapper implements RxCache.VersionMapper<User> {
        @Override
        public long getVersion(User model) {
            return model.getVersion();
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version=" + version +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (version != user.version) return false;
        return !(name != null ? !name.equals(user.name) : user.name != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (version ^ (version >>> 32));
        return result;
    }
}
