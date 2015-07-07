package com.sample.javacv.samplerecord.models;

import java.util.Arrays;

public class FrameItem {
    private byte[] data;
    private long time;

    public FrameItem(byte[] data, long time) {
        this.data = data;
        this.time = time;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "FrameItem{" +
                "data=" + Arrays.toString(data) +
                ", time=" + time +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrameItem frameItem = (FrameItem) o;

        if (time != frameItem.time) return false;
        return Arrays.equals(data, frameItem.data);

    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(data);
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }
}
