package com.leo.cse.dto;

public class Challenge {
    public final String name;
    public final long time;

    public Challenge(String name, long time) {
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Challenge challenge = (Challenge) o;

        if (!name.equals(challenge.name)) return false;
        return time == challenge.time;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Long.hashCode(time);
        return result;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "name='" + name + '\'' +
                ", time=" + time +
                '}';
    }
}
