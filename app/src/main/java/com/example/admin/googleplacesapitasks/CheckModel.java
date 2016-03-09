package com.example.admin.googleplacesapitasks;

import java.io.Serializable;

/**
 * Created by Admin on 2/4/2016.
 */
public class CheckModel implements Serializable {
    boolean isSelected=false;
    String types;

    public CheckModel( String types,boolean isSelected) {
        this.types = types;
        this.isSelected = isSelected;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
