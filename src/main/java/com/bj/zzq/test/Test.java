package com.bj.zzq.test;

import com.bj.zzq.utils.EmailUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @Author: zhaozhiqiang
 * @Date: 2019/2/13
 * @Description:
 */
public class Test {

    public static void main(String[] args) throws IOException {
        String p1 = "C:\\Users\\18511\\Desktop\\aaa.csv";
        String p2 = "C:\\Users\\18511\\Desktop\\think_dept.csv";
        BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(new File(p1)),"gbk"));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(new File(p2)),"utf-8"));
        ArrayList<String> l1 = new ArrayList<>();
        ArrayList<String> l2 = new ArrayList<>();

        String s="";
        try {
            while ((s = br2.readLine()) != null) {
                String[] split = s.split(",");
                l2.add(split[4].replace("\"",""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("aaaaaaaaaaaa"+s);
        }
        s = "";
        while ((s = br1.readLine()) != null) {
            l1.add(s);
        }
        for (String ss : l1.toArray(new String[]{})) {
            if (!find(ss, l2)) {
                System.out.println(ss);
            }
        }
        System.out.println("l1 size:"+l1.size());
        HashSet<String> set = new HashSet<>(l1);
        System.out.println("l1 set size:"+set.size());

    }

    public static boolean find(String s, List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String s1 = list.get(i);
            if (s1.contains(s)) {
                return true;
            }
        }
        return false;
    }

}
