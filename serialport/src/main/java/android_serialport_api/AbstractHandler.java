package android_serialport_api;

/**
 * Created by mutoukenji on 17-8-12.
 */

public abstract class AbstractHandler<T> {
    private Bootstrap client;
    public abstract boolean handle(T message, Bootstrap client) throws Exception;
    void client(Bootstrap client) {
        this.client = client;
    }
}
