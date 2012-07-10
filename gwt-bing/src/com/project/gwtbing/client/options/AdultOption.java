package com.project.gwtbing.client.options;

public enum AdultOption  {
    OFF,
    MODERATE,
    STRICT;
    
    @Override
    public String toString() {
        String name = this.name();
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
