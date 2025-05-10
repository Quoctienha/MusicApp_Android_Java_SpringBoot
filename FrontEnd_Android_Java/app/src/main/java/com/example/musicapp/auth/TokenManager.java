package com.example.musicapp.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import java.util.Date;

//Dùng SharedPreferences để giữ accessToken và refreshToken:
public class TokenManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_ACCESS = "ACCESS_TOKEN";
    private static final String KEY_REFRESH = "REFRESH_TOKEN";

    private final SharedPreferences prefs;

    public TokenManager(Context ctx) {
        prefs = ctx.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveTokens(String access, String refresh) {
        prefs.edit()
                .putString(KEY_ACCESS, access)
                .putString(KEY_REFRESH, refresh)
                .apply();
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH, null);
    }
    public void clear() {
        prefs.edit().clear().apply();
    }

    public boolean isTokenExpired(String token) {
        try {
            JWT jwt = new JWT(token);
            Date expiresAt = jwt.getExpiresAt();
            return expiresAt != null && expiresAt.before(new Date());
        } catch (Exception e) {
            Log.e("JWT", "Invalid token format", e);
            return true; // Nếu token lỗi định dạng, coi như hết hạn
        }
    }
}

