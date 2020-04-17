package android_serialport_api;

/**
 * Created by mutoukenji on 17-8-12.
 */

public abstract class AbstractDecoder<T> {
    public abstract T decode(byte[] data) throws Exception;
}
