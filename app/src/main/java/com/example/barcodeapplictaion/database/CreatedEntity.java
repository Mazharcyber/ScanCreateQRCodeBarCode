package com.example.barcodeapplictaion.database;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "created_table")
public class CreatedEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    @NonNull
    @ColumnInfo(name = "content_type")
    private String content_type;

    @NonNull
    @ColumnInfo(name = "format")
    private int format;

    @NonNull
    @ColumnInfo(name = "timestamp")
    private String timestamp;

    @NonNull
    @ColumnInfo(name = "barcode_type")
    private String barcodeType;

    @ColumnInfo(name = "bitmap")
    private byte[] image_bytes;


    //WIFI
    @ColumnInfo(name = "ssid")
    private String ssid;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "encryption_type")
    private String encryptionType;



    //URL
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "url")
    private String url;


    //SMS Or Phone
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "message")
    private String message;


    //EMAIL
    @ColumnInfo(name = "email_address")
    private String emailAddress;

    @ColumnInfo(name = "subject")
    private String subject;

    @ColumnInfo(name = "body")
    private String body;

    //CONTACT INFO

    @ColumnInfo(name = "formatted_name")
    private String formatted_name;




    public CreatedEntity() {
    }

    public CreatedEntity(@NonNull String content, @NonNull String content_type, @NonNull int format, @NonNull String timestamp) {
        this.content = content;
        this.content_type = content_type;
        this.format = format;
        this.timestamp = timestamp;
    }

    @ColumnInfo(name = "display_content")
    private String display_content;

    @NonNull
    public String getDisplay_content() {
        return display_content;
    }

    public void setDisplay_content(@NonNull String display_content) {
        this.display_content = display_content;
    }

    @NonNull
    public byte[] getImage_bytes() {
        return image_bytes;
    }

    public void setImage_bytes(@NonNull byte[] image_bytes) {
        this.image_bytes = image_bytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(@NonNull String content_type) {
        this.content_type = content_type;
    }

    @NonNull
    public int getFormat() {
        return format;
    }

    public void setFormat(@NonNull int format) {
        this.format = format;
    }

    @NonNull
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull String timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull
    public String getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(@NonNull String barcodeType) {
        this.barcodeType = barcodeType;
    }

    @NonNull
    public String getSsid() {
        return ssid;
    }

    public void setSsid(@NonNull String ssid) {
        this.ssid = ssid;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    @NonNull
    public String getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(@NonNull String encryptionType) {
        this.encryptionType = encryptionType;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @NonNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NonNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NonNull String message) {
        this.message = message;
    }

    @NonNull
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(@NonNull String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @NonNull
    public String getSubject() {
        return subject;
    }

    public void setSubject(@NonNull String subject) {
        this.subject = subject;
    }

    @NonNull
    public String getBody() {
        return body;
    }

    public void setBody(@NonNull String body) {
        this.body = body;
    }

    @NonNull
    public String getFormatted_name() {
        return formatted_name;
    }

    public void setFormatted_name(@NonNull String formatted_name) {
        this.formatted_name = formatted_name;
    }
}
