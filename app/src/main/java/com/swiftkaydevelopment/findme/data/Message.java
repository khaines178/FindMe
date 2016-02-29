package com.swiftkaydevelopment.findme.data;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.MessagesListActivity;
import com.swiftkaydevelopment.findme.data.datainterfaces.Notifiable;
import com.swiftkaydevelopment.findme.managers.AccountManager;
import com.swiftkaydevelopment.findme.managers.MessagesManager;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class Message implements Serializable, Notifiable {
    /**
     * this class will be responsible for storing and managing message data as well as performing
     * operations such as delete, unsend, resend, storing until regained internet access, reporting,
     * getting information about, editing sent messages, sending read status and delete status
     */

    public static final int READ = 1;
    public static final int UNREAD = 0;
    public static final int SEEN = 1;
    public static final int UNSEEN = 0;
    public static final int DELETED = 1;
    public static final int NOT_DELETED = 0;

    public static final int MESSAGE_NOTIFICATION_ID = 1234;

    private String mMessageId;
    private String mMessage;
    private String mTime;
    private String mOuid;
    private User user;
    private String mThreadId;
    private int mReadStatus;
    private int mSeenStatus;
    private int mDeletedStatus;
    private String mTag;
    private String mSenderId;
    private String mMessageImageLocation;

    public static Message instance() {
        return new Message();
    }

    public String getMessageId() {
        return mMessageId;
    }

    public void setMessageId(String mMessageId) {
        this.mMessageId = mMessageId;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getOuid() {
        return mOuid;
    }

    public String getSenderId() { return mSenderId; }

    public void setSenderId(String id) { mSenderId = id; }

    public void setOuid(String mOuid) {
        this.mOuid = mOuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getThreadId() {
        return mThreadId;
    }

    public void setThreadId(String mThreadId) {
        this.mThreadId = mThreadId;
    }

    public int getReadStatus() {
        return mReadStatus;
    }

    public void setReadStatus(int mreadStatus) {
        this.mReadStatus = mreadStatus;
    }

    public int getSeenStatus() {
        return mSeenStatus;
    }

    public void setSeenStatus(int mSeenStatus) {
        this.mSeenStatus = mSeenStatus;
    }

    public int getDeletedStatus() {
        return mDeletedStatus;
    }

    public void setDeletedStatus(int mDeletedStatus) {
        this.mDeletedStatus = mDeletedStatus;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    @Override
    public PushData getPushData(Bundle data, Context context) {

        setMessageId(data.getString("id"));
        setDeletedStatus(0);
        setReadStatus(0);
        setMessage(data.getString("message"));
        setSeenStatus(0);
        setThreadId(data.getString("threadid"));
        setTime(data.getString("time"));
        //todo: the user needs to be passed in with the json data
        setUser(User.createUser().fetchUser(data.getString("senderid"), AccountManager.getInstance(context).getUserId()));
        //todo: this is where eventbus will be called.
        MessagesManager.getInstance("").messageNotificationReceived(this);

        PushData pushData = new PushData();
        pushData.title = user.getFirstname();
        pushData.message = mMessage;
        pushData.resId = R.mipmap.ic_message_black_24dp;
        if (TextUtils.isEmpty(getUser().getPropicloc())) {
            pushData.icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_placeholder);
        } else {
            try {
                pushData.icon = Picasso.with(context)
                        .load(user.getPropicloc())
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
                pushData.icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_placeholder);
            }
        }
        pushData.notificationId = getNotificationId();
        pushData.intent = PushData.createPendingIntent(MessagesListActivity.createIntent(context), context);
        return pushData;
    }

    @Override
    public int getNotificationId() {
        return MESSAGE_NOTIFICATION_ID;
    }

    @Override
    public int getNotificationTypeCount() {
        return 0;
    }
}
