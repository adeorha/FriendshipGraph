package com.mycompany.friendshipgraph;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Person {
    private String id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if ((o instanceof Person) && (((Person) o).getId().equals(this.getId()))) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = this.getId().hashCode();
        return result;
    }
}
