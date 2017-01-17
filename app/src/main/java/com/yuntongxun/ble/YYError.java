package com.yuntongxun.ble;

import android.os.Parcel;
import android.os.Parcelable;

public class YYError implements Parcelable {

    /**
     * 错误码
     */
    public int errorCode;//9966
    /**
     * 错误描述
     */
    public String errorMsg;

    public YYError() {
        this(-1, "未知错误");
    }

    public YYError(int code, String errorMsg) {
        this.errorCode = code;
        this.errorMsg  = errorMsg;
    }

    private YYError(Parcel in) {
        this.errorCode = in.readInt();
        this.errorMsg = in.readString();
    }


    @Override
    public String toString() {
        return "[errorCode : " + errorCode + " , errorMsg : " + errorMsg + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public static final Parcelable.Creator<YYError> CREATOR = new Parcelable.Creator<YYError>() {
        public YYError createFromParcel(Parcel in) {

            return new YYError(in);
        }

        public YYError[] newArray(int size) {

            return new YYError[size];
        }
    };
}