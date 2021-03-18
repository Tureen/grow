package club.tulane.concurrent;

import com.alibaba.fastjson.JSON;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Test {

    public static void main(String[] args) {
        Queue queue = new LinkedList();
        queue.add("123");
        queue.add("456");
        queue.add("789");

        String json = JSON.toJSONString(queue);

        List<String> strings = JSON.parseArray(json, String.class);
        Queue queue1 = JSON.parseObject(json, Queue.class);

        System.out.println();
    }
}
