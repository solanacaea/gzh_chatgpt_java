package com.pandaai.payload;

import lombok.Data;

@Data
public class UserRequest {
    private int count = 1;
    private String result;

    public UserRequest(int count) {
        this.count = count;
    }

    public int plus() {
        count++;
        return count;
    }
}
