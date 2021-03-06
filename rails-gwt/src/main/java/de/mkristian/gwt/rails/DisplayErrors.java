package de.mkristian.gwt.rails;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.fusesource.restygwt.client.FailedStatusCodeException;
import org.fusesource.restygwt.client.Method;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;


@Singleton
public class DisplayErrors {

    public enum Type {
        FORBIDDEN, NOT_FOUND, CONFLICT, UNPROCESSABLE_ENTITY, GENERAL
    }
    
    private final Notice notice;

    @Inject
    public DisplayErrors(Notice notice){
        this.notice = notice;
    }
    
    public Type getType(Throwable exp){
        if( exp instanceof FailedStatusCodeException){
            switch(((FailedStatusCodeException)exp).getStatusCode()){
                case 422: return Type.UNPROCESSABLE_ENTITY;
                case 409: return Type.CONFLICT;
                case 403: return Type.FORBIDDEN;
                case 404: return Type.NOT_FOUND;
            }
        }
        return Type.GENERAL;
    }
    
    public void showErrors(Method method){
        String text = method.getResponse().getText();
        JSONObject obj = JSONParser.parseStrict(text).isObject();
        if (obj != null){
            if (obj.containsKey("errors")){
                obj = obj.get("errors").isObject();
            }
            StringBuffer buf = new StringBuffer();
            for(String key : obj.keySet()) {
                buf.append(key)
                    .append(": ")
                    .append(obj.get(key).toString().replaceAll("\\[|\"|\\]", ""))
                    .append("\n");
            }
            notice.error(buf.toString());
        }
        else {
            notice.error(text);
        }
    }
    
    public void show(String text){
        notice.error(text);
    }
    
    public Type showMessages(Method method, Throwable exp) {
        if( exp instanceof FailedStatusCodeException){
            switch(((FailedStatusCodeException)exp).getStatusCode()){
                case 422:
                    JSONObject obj = JSONParser.parseStrict(method.getResponse().getText()).isObject();
                    if (obj != null){
                        StringBuffer buf = new StringBuffer();
                        for(String key : obj.keySet()) {
                            buf.append(key)
                                .append(": ")
                                .append(obj.get(key).toString().replaceAll("\\[\\]", ""))
                                .append("\n");
                        }
                        notice.error(buf.toString());
                    }
                    else {
                        // TODO
                    }
                    return Type.UNPROCESSABLE_ENTITY;
                case 409:
                    // TODO
                    return Type.CONFLICT;
                default:
                    // TODO
            }
        }
        return Type.GENERAL;
    }
}