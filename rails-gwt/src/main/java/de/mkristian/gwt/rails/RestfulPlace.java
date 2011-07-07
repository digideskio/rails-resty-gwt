package de.mkristian.gwt.rails;

import com.google.gwt.place.shared.Place;

public abstract class RestfulPlace extends Place {

    public final RestfulAction action;

    public final int id;

    public RestfulPlace(RestfulAction restfulAction) {
        this(0, restfulAction);
    }
    
    public RestfulPlace(int id, RestfulAction restfulAction) {
        this.action = restfulAction;    
        this.id = id;
    }
    
    public RestfulPlace(String id, RestfulAction restfulAction) {
        this(id == null? 0 : Integer.parseInt(id), restfulAction);
    }
    
    public String identifier(){
        return "" + id;
    }   
}