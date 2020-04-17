package com.fotile.x1i.bean.event;

public class EventAwaitMessage extends BaseEventMessage {

    public int hashCode;

    public String title;

    public EventAwaitMessage(Class from_class, int hashCode) {
        super(from_class, -1);
        this.hashCode = hashCode;
    }

    public void setTitle(String title){
        this.title = title;
    }
}
