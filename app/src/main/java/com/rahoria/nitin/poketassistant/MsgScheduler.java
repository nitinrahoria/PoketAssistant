package com.rahoria.nitin.poketassistant;

import android.util.Log;

/**
 * Created by nitin on 4/3/2016.
 */
public class MsgScheduler {
    private long _id;
    private int type;
    private String sending_to, cc, bcc, message_subject,
            message_body, sending_at, updated_at, created_at;
    public static final int MSG_SCHDULER_TYPE_DRAFT = 0;
    public static final int MSG_SCHDULER_TYPE_SMS = 1;
    public static final int MSG_SCHDULER_TYPE_EMAIL = 2;
    public static final int MSG_SCHDULER_TYPE_WHATSAPP = 3;

    public MsgScheduler() {
    }

    /**
     * For Email
     * @param type
     * @param sending_to
     * @param cc
     * @param bcc
     * @param message_subject
     * @param message_body
     * @param sending_at
     */
    public MsgScheduler(int type, String sending_to, String cc, String bcc, String message_subject, String message_body, String sending_at) {
        this.type = type;
        this.sending_to = sending_to;
        this.cc = cc;
        this.bcc = bcc;
        this.message_subject = message_subject;
        this.message_body = message_body;
        this.sending_at = sending_at;
    }

    /**
     * For SMS, WhatsApp
     * @param type
     * @param sending_to
     * @param message_body
     * @param sending_at
     */
    public MsgScheduler(int type, String sending_to, String message_body, String sending_at) {
        Log.d("NITIN", "type while saving : " + type);
        this.type = type;
        this.sending_to = sending_to;
        this.message_body = message_body;
        this.sending_at = sending_at;
    }

    public long get_id() {
        return _id;
    }

    public MsgScheduler set_id(long _id) {
        this._id = _id;
        return this;
    }

    public int getType() {
        return type;
    }

    public MsgScheduler setType(int type) {
        this.type = type;
        return this;
    }

    public String getSending_to() {
        return sending_to;
    }

    public MsgScheduler setSending_to(String sending_to) {
        this.sending_to = sending_to;
        return this;
    }

    public String getCc() {
        return cc;
    }

    public MsgScheduler setCc(String cc) {
        this.cc = cc;
        return this;
    }

    public String getBcc() {
        return bcc;
    }

    public MsgScheduler setBcc(String bcc) {
        this.bcc = bcc;
        return this;
    }

    public String getMessage_subject() {
        return message_subject;
    }

    public MsgScheduler setMessage_subject(String message_subject) {
        this.message_subject = message_subject;
        return this;
    }

    public String getMessage_body() {
        return message_body;
    }

    public MsgScheduler setMessage_body(String message_body) {
        this.message_body = message_body;
        return this;
    }

    public String getSending_at() {
        return sending_at;
    }

    public MsgScheduler setSending_at(String sending_at) {
        this.sending_at = sending_at;
        return this;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public MsgScheduler setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
        return this;
    }

    public String getCreated_at() {
        return created_at;
    }

    public MsgScheduler setCreated_at(String created_at) {
        this.created_at = created_at;
        return this;
    }
}
