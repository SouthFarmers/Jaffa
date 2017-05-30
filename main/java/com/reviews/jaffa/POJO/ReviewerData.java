package com.reviews.jaffa.POJO;

/**
 * Created by GauthamVejandla on 5/28/17.
 */

public class ReviewerData {
    String name;
    String type;
    String version_number;
    String feature;

    public ReviewerData(String name, String type, String version_number, String feature ) {
        this.name=name;
        this.type=type;
        this.version_number=version_number;
        this.feature=feature;

    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVersion_number() {
        return version_number;
    }

    public String getFeature() {
        return feature;
    }

}
