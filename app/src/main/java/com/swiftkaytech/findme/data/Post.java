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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.swiftkaytech.findme.managers.CommentsManager;
import com.swiftkaytech.findme.managers.ConnectionManager;
import com.swiftkaytech.findme.views.tagview.Tag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class Post implements Serializable{
    /**
     * this class will be used to store data about an individual post. will include all methods
     * involved in handling posts such as checkmark_liked post, comment, report, unlike, getting information, disliking etc
     */

    public static final String TAG = "FindMe-Post";
    private String mPostId;
    private static String mUid;
    private String mPostingUsersId;
    private User mUser;
    private String mPostText;
    private String mTime;
    private int mNumLikes;
    private String mPostImage;
    private int mNumComments;
    private boolean mLiked;
    private ArrayList<Comment> comments;
    private ArrayList<Tag> mTags;
    private static Context mContext;

    public static Post createPost(String uid, Context context){
        Post post = new Post();
        mUid = uid;
        mContext = context;
        return post;
    }

    public Post fetchPost(String postid){
        mPostId = postid;
        try {
            new FetchPostTask(postid, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null).get();
            setUser(User.createUser(mUid, mContext).fetchUser(getPostingUsersId(), mContext));
            setComments(CommentsManager.getInstance(mUid, mContext).fetchComments(mPostId));
            return this;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "fetchPost - returning null");
        return this;
    }

    private static class FetchPostTask extends AsyncTask<Void,Void,Post> implements  Serializable{
        String postid;
        Post post;
        public FetchPostTask(String postid,Post post){
            this.postid = postid;
            this.post = post;
        }

        @Override
        protected Post doInBackground(Void... params) {
            Log.i(TAG, "fetchPost-doInBackground");

            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getpost.php");
            connectionManager.addParam("postid", postid);
            connectionManager.addParam("uid", mUid);

            try {
                JSONObject jsonObject = new JSONObject(connectionManager.sendHttpRequest());
                post.setPostText(jsonObject.getString("post"));
                post.setPostingUsersId(jsonObject.getString("postingusersid"));
                post.setNumComments(Integer.parseInt(jsonObject.getString("numcomments")));
                post.setNumLikes(Integer.parseInt(jsonObject.getString("numlikes")));
                post.setTime(jsonObject.getString("time"));
                post.setLiked(jsonObject.getBoolean("liked"));

            } catch(JSONException e) {
                e.printStackTrace();
            }
            return post;
        }
    }

    /**
     * sets the id of the posting user
     * @param id uid of posting user
     */
    public void setPostingUsersId(String id){
        mPostingUsersId = id;
    }

    /**
     * sets the url location of the posts posted image
     * @param imgloc url String pointing to posted image
     */
    public void setPostImage(String imgloc){
        mPostImage = imgloc;
    }

    /**
     * gets the string url location of the posted image
     * @return url String location pointing to posted image
     */
    public String getPostImage(){
        return mPostImage;
    }

    /**
     * gets the uid of the posting user
     * @return uid of posting user
     */
    public String getPostingUsersId(){
        return mPostingUsersId;
    }

    /**
     * gets the list of tags for this post
     * @return arraylist of tags
     */
    public ArrayList<Tag> getTags() {
        return mTags;
    }

    /**
     * sets the tags for this post
     * @param mTags arraylist of tags
     */
    public void setTags(ArrayList<Tag> mTags) {
        this.mTags = mTags;
    }

    /**
     * gets the user
     * @return User who posted post
     */
    public User getUser() {
        return mUser;
    }

    /**
     * sets the user who posted the post
     * @param user User who posted post
     */
    public void setUser(User user) {
        this.mUser = user;
    }

    /**
     * gets the post text
     * @return String post text
     */
    public String getPostText() {
        return mPostText;
    }

    /**
     * sets the post text
     * @param postText String text for post
     */
    public void setPostText(String postText) {
        this.mPostText = postText;
    }

    /**
     * gets the device users uid
     * @return device users uid
     */
    public String getUid(){
        return mUid;
    }

    /**
     * gets arraylist of comments for post
     * @return List of comments for post
     */
    public ArrayList<Comment> getComments() {
        return comments;
    }

    /**
     * sets the list of comments for post
     * @param comments List of comments
     */
    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    /**
     * gets the number of comments for this post
     * @return Integer number of comments
     */
    public int getNumComments() {
        return mNumComments;
    }

    /**
     * sets the number of comments on this post
     * @param mNumComments Integer number of comments on this post
     */
    public void setNumComments(int mNumComments) {
        this.mNumComments = mNumComments;
    }

    /**
     * gets the number of likes for this post
     * @return number of likes on this post
     */
    public int getNumLikes() {
        return mNumLikes;
    }

    /**
     * sets the number of likes for post
     * @param mNumLikes number of likes for post
     */
    public void setNumLikes(int mNumLikes) {
        this.mNumLikes = mNumLikes;
    }

    /**
     * gets the amount of time since this post was posted
     * @return String time since post was posted
     */
    public String getTime() {
        return mTime;
    }

    /**
     * sets the amount of time since this post was posted
     * @param mTime time since post was posted
     */
    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    /**
     * gets the id associated with this post
     * @return this post's id
     */
    public String getPostId(){
        return mPostId;
    }

    /**
     * sets the id for this post
     * @param postId String for post id
     */
    public void setPostId(String postId){
        mPostId = postId;
    }

    /**
     * sets whether the user has liked this post
     * @param liked true if user has liked this post
     */
    public void setLiked(boolean liked){
        mLiked = liked;
    }

    /**
     * gets whether the user has liked this post
     * @return true if user has liked this post
     */
    public boolean getLiked(){
        return mLiked;
    }

}