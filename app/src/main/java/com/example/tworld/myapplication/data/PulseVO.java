package com.example.tworld.myapplication.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 表示脉搏的值对象
 */

public class PulseVO implements Parcelable {

    /**
     * 心率
     */
    private Integer bpm;

    /**
     * 信号
     */
    private Integer signal;

    public Integer getBpm() {
        return bpm;
    }

    public void setBpm(Integer bpm) {
        this.bpm = bpm;
    }

    public Integer getSignal() {
        return signal;
    }

    public void setSignal(Integer signal) {
        this.signal = signal;
    }

    @Override
    public String toString() {
        return "PulseVO{" +
                "bpm=" + bpm +
                ", signal=" + signal +
                '}' + "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bpm);
        dest.writeInt(signal);
    }
}
