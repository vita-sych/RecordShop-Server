package org.vita.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class Category {
    private int categoryId;
    private String name;
    private String description;

    public Category() {}

    @Override
    public String toString() {
        return "Category {Name: " + name + ", Desc: " + description + "}";
    }
}
