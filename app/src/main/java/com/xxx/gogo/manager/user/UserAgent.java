package com.xxx.gogo.manager.user;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xxx.gogo.net.NetworkInterface;
import com.xxx.gogo.net.VolleyWrapper;
import com.xxx.gogo.utils.ThreadManager;

class UserAgent {
    private Callback mCb;
    private Object mLoginTag = new Object();
    private Object mRegisterTag = new Object();
    private Object mFindPwdTag = new Object();

    UserAgent(Callback callback){
        mCb = callback;
    }

    void findPassword(String phoneNum){
        Request request = new StringRequest(
                NetworkInterface.USER_FIND_PASSWORD_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCb.onFindPasswordSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ThreadManager.postTask(ThreadManager.TYPE_UI, new Runnable() {
                    @Override
                    public void run() {
                        mCb.onFindPasswordSuccess();
                    }
                }, 2000);
                //mCb.onFindPasswordFail();
            }
        });
        request.setTag(mFindPwdTag);
        VolleyWrapper.getInstance().requestQueue().add(request);
    }

    void register(String userName, String password,
                         String checkSum, String invitationNum){
        Request request = new StringRequest(
                NetworkInterface.USER_REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mCb.onRegisterSuccess();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ThreadManager.postTask(ThreadManager.TYPE_UI, new Runnable() {
                    @Override
                    public void run() {
                        mCb.onRegisterSuccess();
                    }
                }, 2000);

                //mCb.onRegisterFail();
            }
        });
        request.setTag(mRegisterTag);
        VolleyWrapper.getInstance().requestQueue().add(request);
    }

    void login(String userName, String password){
        Request request = new StringRequest(
                NetworkInterface.USER_LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                UserLoginProtocol.UserLoginResponse userLoginResponse
                        = UserLoginProtocol.parseLoginResponse(response);
                if(userLoginResponse == null || userLoginResponse.data.result == 0){
                    mCb.onLoginFail();
                }else {
                    mCb.onLoginSuccess(userLoginResponse.data.data.customer_id);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ThreadManager.postTask(ThreadManager.TYPE_UI, new Runnable() {
                    @Override
                    public void run() {
                        mCb.onLoginSuccess("0000000001");
                    }
                }, 2000);
                //mCb.onLoginFail();
            }
        });
        request.setTag(mLoginTag);
        VolleyWrapper.getInstance().requestQueue().add(request);
    }

    void cancelLogin(){
        VolleyWrapper.getInstance().requestQueue().cancelAll(mLoginTag);
    }

    void cancelRegister(){
        VolleyWrapper.getInstance().requestQueue().cancelAll(mRegisterTag);
    }

    void cancelFindPwd(){
        VolleyWrapper.getInstance().requestQueue().cancelAll(mFindPwdTag);
    }

    public interface Callback{
        void onFindPasswordSuccess();
        void onRegisterSuccess();
        void onLoginSuccess(String userId);
        void onFindPasswordFail();
        void onRegisterFail();
        void onLoginFail();
    }
}
