/*
 *      Copyright (C) 2015 Kevin Haines
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.swiftkaytech.findme.data;

import android.os.AsyncTask;
import android.util.Log;

import com.swiftkaytech.findme.managers.ConnectionManager;
import com.swiftkaytech.findme.views.tagview.Tag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class User implements Serializable{

    public static final String TAG = "FindMe - User";

    public enum Gender{
        MALE, FEMALE
    }
    public enum Orientation{
        STRAIGHT, GAY, LESBIAN, BISEXUAL, OTHER
    }
    public enum OnlineStatus{
        ONLINE, OFFLINE, HIDDEN, UNKNOWN
    }
    public enum InterestedIn{
        MEN, WOMEN, BOTH
    }

    private static String mUid;
    private String mOuid;
    private String mPropicloc;
    private Gender mGender;
    private String mName;
    private String mFirstname;
    private String mLastname;
    private int mAge;
    private boolean mIsFriend;
    private boolean mIsBlocked;
    private boolean mIsMatch;
    private Location mLocation;
    private Orientation mOrientation;
    private OnlineStatus mOnlineStatus;
    private List<String> mPictures;
    private List<Tag> mTags;
    private String mAboutMe;
    private InterestedIn mInterestIn;

    /**
     * creates a new instance of a user
     * @param uid the current device users unique id
     * @return new instance of User
     */
    public static User createUser(String uid){
        User user = new User();
        mUid = uid;
        return user;
    }

    public User fetchUser(String ouid){
        Log.i(TAG,"fetchUser");
        mOuid = ouid;
        try {
            return new FetchUserTask(ouid,this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG,"fetchUser - returning null");
        return this;
    }

    /**
     * class to fetch all of the users details
     */
    private class FetchUserTask extends AsyncTask<Void,Void,User>{
        String uid;
        User user;
        public FetchUserTask(String uid, User user){
            this.uid = uid;
            this.user = user;
        }

        @Override
        protected User doInBackground(Void... params) {
            Log.i(TAG, "fetchUser-doInBackground");
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getuser.php");
            connectionManager.addParam("uid",mUid);
            connectionManager.addParam("ouid",uid);

            try {
                JSONObject jsonObject = new JSONObject(connectionManager.sendHttpRequest());
                user.setName(jsonObject.getString("name"));
                user.setFirstname(jsonObject.getString("firstname"));
                user.setLastname(jsonObject.getString("lastname"));
                user.setGender(setGenderFromString(jsonObject.getString("gender")));
                user.setPropicloc(jsonObject.getString("propicloc"));
                user.setAge(Integer.parseInt(jsonObject.getString("age")));
                user.setIsFriend(jsonObject.getBoolean("isfriend"));
                user.setIsBlocked(jsonObject.getBoolean("isblocked"));
                user.setIsMatch(jsonObject.getBoolean("ismatch"));
                user.setOrientation(setOrientationFromString(jsonObject.getString("orientation")));
                user.setOnlineStatus(setOnlineStatusFromString(jsonObject.getString("onlinestatus")));
                user.setAboutMe(jsonObject.getString("aboutme"));
                user.setInterestedIn(setInterestedInFromString(jsonObject.getString("looking_for_gender")));
                user.setLocation(setLocationFromArray(jsonObject.getJSONObject("location")));

            } catch(JSONException e) {
                e.printStackTrace();
            }
            return user;
        }
    }

    /**
     * sets the gender enum based on a string
     * @param gender Gender object representing the users gender
     */
    public Gender setGenderFromString(String gender){
        switch (gender){
            case "Male":{
                return Gender.MALE;
            }
            case "Female":{
                return Gender.FEMALE;
            }
            default:{
                return null;
            }
        }
    }

    /**
     * sets orientation enum based on string orientation
     * @param orientation string to be used to set orientation
     * @return Orientation enum from string
     */
    public Orientation setOrientationFromString(String orientation) {
        if (orientation.equalsIgnoreCase("Straight")) {
            return Orientation.STRAIGHT;
        } else if (orientation.equalsIgnoreCase("Gay")) {
            return Orientation.GAY;
        } else if (orientation.equalsIgnoreCase("Lesbian")) {
            return Orientation.LESBIAN;
        } else if (orientation.equalsIgnoreCase("Bisexual")) {
            return Orientation.BISEXUAL;
        } else {
            return Orientation.OTHER;
        }
    }

    /**
     * sets online status based on string online status
     * @param status string to be used to set online status
     * @return OnlineStatus enum from string value
     */
    public OnlineStatus setOnlineStatusFromString(String status) {
        if (status.equals("online")) {
            return OnlineStatus.ONLINE;
        } else if (status.equals("offline")) {
            return OnlineStatus.OFFLINE;
        } else if (status.equals("hidden")) {
            return OnlineStatus.HIDDEN;
        } else {
            return OnlineStatus.UNKNOWN;
        }
    }

    public InterestedIn setInterestedInFromString(String interest) {
        if (interest.equals("Men")) {
            return InterestedIn.MEN;
        } else if (interest.equals("Women")) {
            return InterestedIn.WOMEN;
        } else {
            return InterestedIn.BOTH;
        }
    }

    public Location setLocationFromArray(JSONObject array) throws JSONException{
        //todo: this is going to be changed to geocoding
        String city = array.getString("city");
        String distance = array.getString("distance");
        Country country = new Country("United States");
        Location loc = new Location(Float.valueOf(distance), city, country);
        return loc;
    }

    /**
     * fetches the users mFirstname
     * @return String of users mFirstname
     */
    public String getFirstname() {
        return mFirstname;
    }

    /**
     * sets the users mFirstname
     * @param firstname mFirstname of user
     */
    public void setFirstname(String firstname) {
        this.mFirstname = firstname;
    }

    /**
     * gets the mGender that the user is interested in
     * @return InterestedIn enum for what mGender the user
     * is interested in
     */
    public InterestedIn getInterestedIn() {
        return mInterestIn;
    }

    /**
     * sets the mGender that the user is interested in
     * @param interestedIn mGender user is interested in
     */
    public void setInterestedIn(InterestedIn interestedIn) {
        this.mInterestIn = interestedIn;
    }

    /**
     * gets the users about me
     * @return String users about me
     */
    public String getAboutMe() {
        return mAboutMe;
    }

    /**
     * sets the users about me
     * @param aboutMe string users about me
     */
    public void setAboutMe(String aboutMe) {
        this.mAboutMe = aboutMe;
    }

    /**
     * gets the list of mTags pertaining to things the user is interested in
     * @return list of users interest mTags
     */
    public List<Tag> getTags() {
        //todo: issue # 12 implement getting tags
        return mTags;
    }

    /**
     * sets a list of mTags the user is interested int
     * EX: netflix, outdoors, puppies, cats etc.
     * @param tags list of mTags
     */
    public void setTags(List<Tag> tags) {
        this.mTags = tags;
    }

    /**
     * gets a list of the users picture uri locations
     * @return list of picture uri locations
     */
    public List<String> getPictures() {
        //todo: issue #11 - need to add way of getting users photos
        //and then a way to add to that list when needing to view more
        if (mPictures != null) {
            return  mPictures;
        } else {
            new AsyncTask<String,String,String>(){

                @Override
                protected String doInBackground(String... params) {
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                }
            }.execute();
        }
        return mPictures;
    }

    /**
     * sets the list of picture mLocation uris
     * @param pictures list of URIs pointing to the mLocation of
     *                 the users mPictures
     */
    public void setPictures(List<String> pictures) {
        this.mPictures = pictures;
    }

    /**
     *
     * @return online status enum
     */
    public OnlineStatus getOnlineStatus() {
        return mOnlineStatus;
    }

    /**
     *
     * @param onlineStatus set online status enum
     */
    public void setOnlineStatus(OnlineStatus onlineStatus) {
        this.mOnlineStatus = onlineStatus;
    }

    /**
     *
     * @return get the users sexual mOrientation enum
     */
    public Orientation getOrientation() {
        return mOrientation;
    }

    /**
     *
     * @param orientation set the users sexual mOrientation enum
     */
    public void setOrientation(Orientation orientation) {
        this.mOrientation = orientation;
    }

    /**
     *
     * @return Location object referencing users mLocation
     */
    public Location getLocation() {
        return mLocation;
    }

    /**
     *
     * @param location Location object referencing users mLocation
     */
    public void setLocation(Location location) {
        this.mLocation = location;
    }

    /**
     *
     * @return boolean true if matched false if not
     */
    public boolean isMatch() {
        return mIsMatch;
    }

    /**
     *
     * @param isMatch boolean true if matched false if not
     */
    public void setIsMatch(boolean isMatch) {
        this.mIsMatch = isMatch;
    }

    /**
     *
     * @return boolean true if blocked false if not
     */
    public boolean isBlocked() {
        return mIsBlocked;
    }

    /**
     *
     * @param isBlocked boolean true if blocked false if not
     */
    public void setIsBlocked(boolean isBlocked) {
        this.mIsBlocked = isBlocked;
    }

    /**
     *
     * @return boolean true if user is friend to current user false if not
     */
    public boolean isFriend() {
        return mIsFriend;
    }

    /**
     *
     * @param isFriend boolean true if user is friends with current user false if not
     */
    public void setIsFriend(boolean isFriend) {
        this.mIsFriend = isFriend;
    }

    /**
     *
     * @return int users mAge
     */
    public int getAge() {
        return mAge;
    }

    /**
     *
     * @param age int users mAge
     */
    public void setAge(int age) {
        this.mAge = age;
    }

    /**
     *
     * @return string users mLastname
     */
    public String getLastname() {
        return mLastname;
    }

    /**
     * sets the users last mName.
     * also if the mFirstname is not empty and the users
     * full mName is this will smartly update the users fullname.
     * set first mName does not do this same smart update
     * @param lastname string users last mName
     */
    public void setLastname(String lastname) {
        this.mLastname = lastname;

        if ((!mFirstname.equals("")) && (mName.equals(""))) {
            mName = mFirstname + " " + lastname;
        }
    }

    /**
     *
     * @return string users full mName
     */
    public String getName() {
        return mName;
    }

    /**
     *
     * @param name string users full mName
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     *
     * @return Gender enum for users mGender
     */
    public Gender getGender() {
        return mGender;
    }

    /**
     *
     * @param gender Gender enum for users mGender
     */
    public void setGender(Gender gender) {
        this.mGender = gender;
    }

    /**
     *
     * @return users profile picture uri mLocation
     */
    public String getPropicloc() {
        return mPropicloc;
    }

    /**
     *
     * @param propicloc users profile picture uri mLocation
     */
    public void setPropicloc(String propicloc) {
        this.mPropicloc = propicloc;
    }

    /**
     *
     * @return users unique user id
     */
    public String getOuid() {
        return mOuid;
    }

    /**
     *
     * @param uid users unique user id
     */
    public void setOuid(String uid) {
        this.mOuid = uid;
    }

    /**
     *returns a string representation of the User
     * @return string representation of User object
     */
    public String toString(){
        return "User-Name: " + mName + "Firstname: " + mFirstname + "Lastname: " + mLastname +
                "Gender: " + mGender + "mUid: "+ mUid + " mPropicloc: " + mPropicloc + " mAge: " + Integer.toString(mAge) +
                "mIsFriend: " + String.valueOf(mIsFriend) + " mIsBlocked: " + String.valueOf(mIsBlocked) + " mIsMatch: " +
                String.valueOf(mIsMatch) + " Location: " + mLocation.toString() + " mOrientation: " + mOrientation +
                " mOnlineStatus: " + mOnlineStatus + " mInterestIn: " + mInterestIn;
    }

    /**
     * determines if the users being compared are the same user
     * @param user user to be compared to
     * @return true if the two users are the same user
     */
    public boolean isSameAs(User user){
       return mOuid.equals(user.getOuid());
    }
}
