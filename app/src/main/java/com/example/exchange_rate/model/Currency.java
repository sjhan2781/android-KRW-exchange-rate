package com.example.exchange_rate.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Currency implements Parcelable, Comparable<Currency> {
    private String code;
    private String name;
    private String sending = "0";   //통화 구매시
    private String receiving = "0"; //통화 판매시
    private Date date;
    public static filter order = filter.CODE;

    public enum filter {
        CODE, NAME, BUY, SOLD, DATE
    }

    public Currency() {
    }

    public Currency(String code, String name, String sending, String receiving) {
        this.code = code;
        this.name = name;
        this.sending = sending;
        this.receiving = receiving;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSending() {
        return sending.replace(",", "");
    }

    public void setSending(String sending) {
        this.sending = sending;
    }

    public String getReceiving() {
        return receiving.replace(",", "");
    }

    public void setReceiving(String receiving) {
        this.receiving = receiving;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getSendingFloat() {
        return Float.parseFloat(getSending());
    }

    public Float getReceivingFloat() {
        return Float.parseFloat(getReceiving());
    }

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", sending='" + sending + '\'' +
                ", receiving='" + receiving + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeString(this.sending);
        dest.writeString(this.receiving);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    protected Currency(Parcel in) {
        this.code = in.readString();
        this.name = in.readString();
        this.sending = in.readString();
        this.receiving = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel source) {
            return new Currency(source);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    @Override
    public int compareTo(Currency o) {
        switch (order) {
            case NAME:
                return this.name.compareTo(o.name);
            case BUY:
                return this.getSendingFloat().compareTo(o.getSendingFloat());
            case SOLD:
                return this.getReceivingFloat().compareTo(o.getReceivingFloat());
            case DATE:
                return this.getDate().compareTo(o.getDate());
            default:
                return this.code.compareTo(o.code);
        }
    }
}
