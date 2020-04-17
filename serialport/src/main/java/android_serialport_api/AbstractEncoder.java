package android_serialport_api;

/**
 * Created by mutoukenji on 17-8-12.
 */

public abstract class AbstractEncoder<T> {
    public abstract byte[] encode(T message) throws Exception;
}
