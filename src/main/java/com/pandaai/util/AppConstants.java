package com.pandaai.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface AppConstants {
    String DEFAULT_PAGE_NUMBER = "0";
    String DEFAULT_PAGE_SIZE = "30";

    int MAX_PAGE_SIZE = 50;

    DateFormat DF_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    String LIMIT_CONFIG_TYPE = "USAGE_LIMIT";
    String ERROR_RESP_MSG = "暂时无法回答，请稍后再试。";
    String WELCOME_MSG = "欢迎关注！";
}
