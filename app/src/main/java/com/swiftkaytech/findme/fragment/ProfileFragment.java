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

package com.swiftkaytech.findme.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.activity.MessagesActivity;
import com.swiftkaytech.findme.adapters.PostAdapter;
import com.swiftkaytech.findme.data.Post;
import com.swiftkaytech.findme.data.User;
import com.swiftkaytech.findme.managers.PostManager;

import java.util.ArrayList;

public class ProfileFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
View.OnClickListener{
    private static final String TAG = "ProfileFragment";
    private static final String ARG_USER = "ARG_USER";
    private static final String ARG_POSTS = "ARG_POSTS";

    private static User user;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView    mTvAgeLocation;
    private TextView    mTvOrientation;

    private FloatingActionButton mFabBase;
    private FloatingActionButton mFabLeft;
    private FloatingActionButton mFabCenter;
    private FloatingActionButton mFabUpper;

    private PostAdapter mPostAdapter;
    private ArrayList<Post> mPostList;

    public static ProfileFragment newInstance(User user, String uid) {
        ProfileFragment frag = new ProfileFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_USER, user);
        b.putString(ARG_UID, uid);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            user = (User) savedInstanceState.getSerializable(ARG_USER);
            mPostList = (ArrayList) savedInstanceState.getSerializable(ARG_POSTS);
            uid = savedInstanceState.getString(ARG_UID);
        } else {
            if (getArguments() != null) {
                user = (User) getArguments().getSerializable(ARG_USER);
                uid = getArguments().getString(ARG_UID);
            }
            mPostList = PostManager.getInstance(uid, getActivity()).fetchUserPosts(getActivity(), user, "0", false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.profilefrag, container, false);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.profileToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle(user.getName());
        toolbar.inflateMenu(R.menu.profile_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mFabBase = (FloatingActionButton) layout.findViewById(R.id.profileFabBase);
        mFabLeft = (FloatingActionButton) layout.findViewById(R.id.profileFabLeft);
        mFabCenter = (FloatingActionButton) layout.findViewById(R.id.profileFabCenter);
        mFabUpper = (FloatingActionButton) layout.findViewById(R.id.profileFabUpper);

        mFabBase.setOnClickListener(this);
        mFabLeft.setOnClickListener(this);
        mFabCenter.setOnClickListener(this);
        mFabUpper.setOnClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                R.color.base_green,
                android.R.color.holo_blue_bright,
                R.color.base_green);
        mTvAgeLocation = (TextView) layout.findViewById(R.id.tvprofileage);
        mTvOrientation = (TextView) layout.findViewById(R.id.tvProfileOrientation);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recyclerViewProfile);

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTvAgeLocation.setText(user.getAge() + "/" + user.getGender().toString() + "/" + user.getLocation().getCity());
        if (mPostAdapter != null) {
            log("adapter is not null");
            mRecyclerView.setAdapter(mPostAdapter);
        } else {
            log("adapter is null");
            log("size " + Integer.toString(mPostList.size()));
            mPostAdapter = new PostAdapter(getActivity(), mPostList);
            mRecyclerView.setAdapter(mPostAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(ARG_POSTS, mPostList);
        outState.putSerializable(ARG_USER, user);
        outState.putString(ARG_UID, uid);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mPostList = PostManager.getInstance(uid, getActivity()).fetchUserPosts(getActivity(), user, "0", false);
        mPostAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Expands the fabs for the users own profile
     */
    private void expandOwnProfileFabs() {
        mFabLeft.setImageResource(R.drawable.ic_action_edit_dark);
        setVisibility();

    }

    /**
     * Expands the fabs for a different users profile
     */
    private void expandOtherProfileFabs() {
        mFabLeft.setImageResource(R.mipmap.ic_message_black_24dp);
        mFabLeft.setColorFilter(Color.WHITE);
        mFabCenter.setImageResource(R.mipmap.ic_person_add_white_24dp);
        mFabCenter.setVisibility(View.VISIBLE);
        setVisibility();
    }

    private void setVisibility () {
        if (mFabUpper.getVisibility() == View.GONE) {
            mFabUpper.setVisibility(View.VISIBLE);
            mFabLeft.setVisibility(View.VISIBLE);
        } else {
            mFabUpper.setVisibility(View.GONE);
            mFabCenter.setVisibility(View.GONE);
            mFabLeft.setVisibility(View.GONE);
        }
    }

    /**
     * Starts activity for sending a message
     */
    private void startSendMessage() {
        startActivity(MessagesActivity.createIntent(getActivity(), user));
    }

    /**
     * Starts User to add friend
     */
    private void addFriend() {

    }

    /**
     * Starts User to match user
     */
    private void matchUser() {

    }

    /**
     * Starts activity to edit profile
     */
    private void editProfile() {

    }

    /**
     * Start activity to view users photos
     */
    private void viewPhotos() {

    }

    @Override
    public void onClick(View v) {
        boolean isSameProfile = false;
        if (user.getOuid().equals(uid)) {
            isSameProfile = true;
        }
        if (v.getId() == R.id.profileFabBase) {
            if (isSameProfile) {
                expandOwnProfileFabs();
            } else {
                expandOtherProfileFabs();
            }
        } else if (v.getId() == R.id.profileFabLeft) {
            if (isSameProfile) {

            } else {
                startSendMessage();
            }
        } else if (v.getId() == R.id.profileFabCenter) {
            if (isSameProfile) {

            }
        } else if (v.getId() == R.id.profileFabUpper) {
            if (isSameProfile) {

            }
        }
    }
}