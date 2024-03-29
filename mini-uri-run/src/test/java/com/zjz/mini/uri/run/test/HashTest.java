package com.zjz.mini.uri.run.test;

import cn.hutool.core.util.HashUtil;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.stream.IntStream;

public class HashTest {

    /**
     * MurmurHash: 是非加密哈希函数家族的一部分，设计快速并适合散列非加密应用；
     * CityHash: 由Google开发，用于字符串和其他数据项的快速散列计算；
     * xxHash: 宣称是速度极快的哈希算法，同时保持了相对较好的碰撞抵抗性；
     * Fowler–Noll–Vo (FNV): 获得广泛应用的另一种快速哈希算法。
     */

    @Test
    public void HashTest() {
        int count = 1000000;
        String url = "https://www.zhangjinzhao.com/short-chain-system/#%E7%9F%AD%E9%93%BE%E6%8E%A5%E8%B7%AF%E7%94%B1%E5%88%B0%E5%8E%9F%E9%93%BE%E6%8E%A5";

        StopWatch stopWatch = new StopWatch();


        stopWatch.start("murmurHash32");
        IntStream.range(0, count).forEach(e -> {
            int i = HashUtil.murmur32((url + e).getBytes());
        });
        stopWatch.stop();

        stopWatch.start("mixHash");
        IntStream.range(0, count).forEach(e -> {
            long i = HashUtil.mixHash((url + e));
        });
        stopWatch.stop();

        stopWatch.start("FNV");
        IntStream.range(0, count).forEach(e -> {
            int i = HashUtil.fnvHash((url + e).getBytes());
        });
        stopWatch.stop();

        stopWatch.start("CityHash");
        IntStream.range(0, count).forEach(e -> {
            int i = HashUtil.cityHash32((url + e).getBytes());
        });
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
    }
}
