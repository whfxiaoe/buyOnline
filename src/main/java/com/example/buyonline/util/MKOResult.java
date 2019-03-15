package com.example.buyonline.util;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kevin on 20/09/2017.
 */
@Setter
@Getter
public class MKOResult {
    public boolean success;
    public String message;
    public String params;

    public MKOResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static MKOResult makeFailResult(String message) {
        return new MKOResult(false, message);
    }

    public static MKOResult makeSuccessResult(String message) {
        return new MKOResult(true, message);
    }
}