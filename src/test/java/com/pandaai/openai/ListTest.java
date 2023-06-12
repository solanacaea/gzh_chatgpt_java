package com.pandaai.openai;

import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ListTest {
    public static void main(String[] args) {
        List<String> history = new ArrayList<>();
        history.add("1");
        history.add("2");
        history.add("3");
        history.add("4");
        history.add("5");
        history.add("6");
        history.add("7");
        history.add("8");
        history.add("9");
        history.add("10");
        int historySize = CollectionUtils.size(history);
        if (historySize > 5) {
            List subList = history.subList(historySize - 5, historySize);
            System.out.println(subList);
        }

    }
}
