package com.matainja.door.model;


public class Devices {

    private String name;
    private String type;
    String version_number;
    String feature;

    private String device_serial,device_wifi_name,device_wifi_password,device_mac_address,online,status,join_date;

    public Devices() {


    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVersion_number() {
        return version_number;
    }

    public String getFeature() {
        return feature;
    }

    public String getDevice_serial() {
        return device_serial;
    }

    public void setDevice_serial(String device_serial) {
        this.device_serial = device_serial;
    }

    public String getDevice_wifi_name() {
        return device_wifi_name;
    }

    public void setDevice_wifi_name(String device_wifi_name) {
        this.device_wifi_name = device_wifi_name;
    }

    public String getDevice_wifi_password() {
        return device_wifi_password;
    }

    public void setDevice_wifi_password(String device_wifi_password) {
        this.device_wifi_password = device_wifi_password;
    }

    public String getDevice_mac_address() {
        return device_mac_address;
    }

    public void setDevice_mac_address(String device_mac_address) {
        this.device_mac_address = device_mac_address;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJoin_date() {
        return join_date;
    }

    public void setJoin_date(String join_date) {
        this.join_date = join_date;
    }
}