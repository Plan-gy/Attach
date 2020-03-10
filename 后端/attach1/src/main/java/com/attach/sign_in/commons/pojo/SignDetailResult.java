package com.attach.sign_in.commons.pojo;

import java.util.Arrays;
import java.util.Date;

public class SignDetailResult {
    private int EffSignInSum;
    private int ShouldSignInSum;
    private Date[] EffectiveDateList;
    private Date[] TotalDateList;

    public int getEffSignInSum() {
        return EffSignInSum;
    }

    public void setEffSignInSum(int effSignInSum) {
        EffSignInSum = effSignInSum;
    }

    public int getShouldSignInSum() {
        return ShouldSignInSum;
    }

    public void setShouldSignInSum(int shouldSignInSum) {
        ShouldSignInSum = shouldSignInSum;
    }

    public Date[] getEffectiveDateList() {
        return EffectiveDateList;
    }

    public void setEffectiveDateList(Date[] effectiveDateList) {
        EffectiveDateList = effectiveDateList;
    }

    public Date[] getTotalDateList() {
        return TotalDateList;
    }

    public void setTotalDateList(Date[] totalDateList) {
        TotalDateList = totalDateList;
    }

    @Override
    public String toString() {
        return "签到任务有效签到次数:" + EffSignInSum +
                "签到任务应签到次数:" + ShouldSignInSum +
                "有效签到日期列表" + Arrays.toString(EffectiveDateList) +
                "应签到日期列表" + Arrays.toString(TotalDateList) +
                '}';
    }
}
