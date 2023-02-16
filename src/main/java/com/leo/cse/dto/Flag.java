package com.leo.cse.dto;

import com.leo.cse.backend.mci.MCI;

import java.util.Objects;

public class Flag {
    public static final int FLAGS_COUNT = 8000;

    public static final String DESC_NONE = "(no description)";
    public static final String DESC_ENGINE = "(engine flag)";
    public static final String DESC_MIM = "(<MIM data)";
    public static final String DESC_VAR = "(<VAR data)";
    public static final String DESC_PHY = "(<PHY data)";
    public static final String DESC_BUY = "(<BUY data)";

    public final int id;
    public final String description;
    public final boolean value;

    public Flag(int id, String description, boolean value) {
        this.id = id;
        this.description = description;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Flag flag = (Flag) o;

        if (id != flag.id) return false;
        if (value != flag.value) return false;
        return Objects.equals(description, flag.description);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (value ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Flag{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", value=" + value +
                '}';
    }

    public static String getDescription(MCI mci, int id) {
        if (id <= 10) {
            return DESC_ENGINE;
        }
        if (mci.hasSpecial("VarHack")) {
            if (id >= 6000 && id <= FLAGS_COUNT) {
                return DESC_VAR;
            }
            if (mci.hasSpecial("PhysVarHack"))
                if (id >= 5632 && id <= 5888) {
                    return DESC_PHY;
                }
        } else if (mci.hasSpecial("MimHack")) {
            if (id >= 7968 && id <= 7993) {
                return DESC_MIM;
            }
        } else if (mci.hasSpecial("BuyHack")) {
            if (id >= 7968 && id <= 7993) {
                return DESC_BUY;
            }
        }
        return mci.getFlagDescription(id);
    }

    public static boolean isValid(MCI mci, int id) {
        if (id <= 10) {
            return false;
        }
        if (mci.hasSpecial("VarHack")) {
            boolean ret = (id < 5999 || id > 7999);
            if (mci.hasSpecial("PhysVarHack")) {
                ret &= (id < 5632 || id > 5888);
            }
            return ret;
        } else if (mci.hasSpecial("MimHack")) {
            return (id < 7968 || id > 7993);
        } else if (mci.hasSpecial("BuyHack")) {
            return (id < 7968 || id > 7999);
        }
        return id != mci.getSaveFlagId();
    }
}
