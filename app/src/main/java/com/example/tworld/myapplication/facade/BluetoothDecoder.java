package com.example.tworld.myapplication.facade;

import android.util.Log;

import com.example.tworld.myapplication.data.PulseVO;
import com.example.tworld.myapplication.data.PulseVOFactory;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙数据解码成为脉搏数据
 */
public class BluetoothDecoder {

    private static final String TAG = "BluetoothDecoder ";

    private static final String SEP = "@";

    // 存储蓝牙序列化数据, 待反序列化
    private static StringBuilder stringBuilder = new StringBuilder();

    // 上次之后未处理完的数据
    private static StringBuilder remain = new StringBuilder();

    public ArrayList<PulseVO> parse(String input) {

        Log.d(TAG, "the input is " + input);

        // 首先把remain拼接上去
        stringBuilder = new StringBuilder(remain);
        stringBuilder.append(input);

        // 拼接好后的tmp
        String tmp = stringBuilder.toString();

        Log.d(TAG, "the tmp is " + tmp);

        // 判断新组装的字符串是否是以R开头的, 如果不是, 向下遍历, 直到找到第一个R为止, 前面全部删除
        int firstRIndex = tmp.indexOf("R");

        // 如果没有找到R开头的, 有可能是因为后面的有效数据还没进入, 所以跳过, 等待下一个数据进来
        if (firstRIndex == -1) {
            remain = new StringBuilder(tmp);
            return new ArrayList<>();
        } else {
            // 如果找到R了, 那么把这个R前面所有的东西都去掉, 因为那些都是无效数据
            String newStr = tmp.substring(0, firstRIndex);
            stringBuilder = new StringBuilder(newStr);

            Log.d("TAG", "new string is " + newStr);
        }

        // 根据分隔符分割, 然后构造对象
        String[] rawPulses = tmp.split(SEP);

        // 判断组装的报文是否是完整的1个或者几个报文
        boolean complete = tmp.lastIndexOf("@") == tmp.length() - 1;

        ArrayList<PulseVO> results = Lists.newArrayList();

        // 截取字符串, 剩下的部分扔进remain
        if (complete) {
            // 如果报文完整, 说明每个字符串都对应一个vo
            for (String rawPulse : rawPulses) {
                // 构造pulseVO
                PulseVO pulseVO = PulseVOFactory.createPulseVO(rawPulse);

                results.add(pulseVO);
            }

            remain = new StringBuilder();

        } else {

            // 不完整, 那么最后一个就是落下的

            int lastRemainIndex = 0;

            for (int i = 0; i < rawPulses.length - 1; i++) {
                // 构造pulseVO
                PulseVO pulseVO = PulseVOFactory.createPulseVO(rawPulses[i]);

                // 原报文 + @ 所以要加1
                lastRemainIndex += rawPulses[i].length() + 1;

                results.add(pulseVO);
            }

            // 这时数组的最后一个元素, 一定是一个完整报文的一部分, 扔进remain, 等待下一次解析
            remain = new StringBuilder(tmp.substring(lastRemainIndex, tmp.length()));
        }

        return results;
    }
}
