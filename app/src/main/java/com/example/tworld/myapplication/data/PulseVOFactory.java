package com.example.tworld.myapplication.data;

import android.util.Log;

/**
 * 构造脉搏的工厂类
 */
public class PulseVOFactory {

    private static String TAG = "pulseVoFactory";

    /**
     * 根据蓝牙接受到的数据来构造PulseVO
     * rawData必须是一个完整的数据, 拆粘包已经在蓝牙层解决
     *
     * @param rawData
     * @return
     */
    public static PulseVO createPulseVO(String rawData) {

        Log.d(TAG, "rawData is " + rawData);

        // 首先根据rawData拆分
        String[] rawStrs = rawData.split("\n");


        // RATE | bpm | signal | @

        PulseVO pulseVO = new PulseVO();

        pulseVO.setBpm(Integer.valueOf(rawStrs[1].trim()));
        pulseVO.setSignal(Integer.valueOf(rawStrs[2].trim()));

        return pulseVO;
    }
}
