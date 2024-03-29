package com.attach.sign_in.service.impl;

import com.attach.sign_in.commons.pojo.SignDetailResult;
import com.attach.sign_in.commons.pojo.effectiveSignIn;
import com.attach.sign_in.commons.pojo.signInResult;
import com.attach.sign_in.commons.pojo.signinDetailResult;
import com.attach.sign_in.commons.utils.GetId;
import com.attach.sign_in.commons.utils.JsonUtils;
import com.attach.sign_in.mapper.*;
import com.attach.sign_in.pojo.*;
import com.attach.sign_in.service.SignInService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class SiginInServiceImpl implements SignInService {
    @Resource
    private SignInMapper signInMapper;
    @Resource
    private UserSignInMapper userSignInMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ParticipateMapper participateMapper;
    @Resource
    private StatisticsMapper statisticsMapper;

    @Override
    public String create_sign_in(SignIn signIn, HttpServletRequest req, HttpServletResponse resp) {
        signInResult result = new signInResult();
        if (signIn.getUserId() != null && signIn.getSignInName().length() != 0 && signIn.getSiteLa() != null && signIn.getSiteLo() != null
                && signIn.getStartTime() != null && signIn.getEndTime() != null && signIn.getMaxNumber() != null) {
            UserExample userExample = new UserExample();
            userExample.createCriteria().andUserIdEqualTo(signIn.getUserId());
            List<User> users = userMapper.selectByExample(userExample);
            if (users != null && users.size() > 0) {
                int SignInId = GetId.getSigInId();
                String SignInPwd = GetId.getSignInPassword(4);
                signIn.setSignInId(SignInId);
                signIn.setSignInPassword(SignInPwd);
                signIn.setEffective((byte) 1);
                int index = signInMapper.insert(signIn);
                if (index > 0) {
                    result.setSignInId(SignInId);
                    result.setSignInPassword(SignInPwd);
                    UserSignIn userSignIn = new UserSignIn();
                    userSignIn.setUserId(signIn.getUserId());
                    userSignIn.setSignInId(signIn.getSignInId());
                    userSignIn.setEffective((byte) 1);
                    int insert = userSignInMapper.insert(userSignIn);
                    if (insert > 0) {
                        result.setStatus("success");
                    } else {
                        return "fail";
                    }
                    return JsonUtils.objectToJson(result);
                } else {
                    result.setStatus("fail");
                    return JsonUtils.objectToJson(result);
                }
            }
            result.setStatus("fail");
            return JsonUtils.objectToJson(result);

        }
        result.setStatus("fail");
        return JsonUtils.objectToJson(result);
    }

    @Override
    public String join_sign_in(Integer userId, Integer signInId, String signInPassword) {
        if (userId == null || signInId == null || signInPassword.length() != 4) {
            return JsonUtils.objectToJson("fail");
        }
        SignInExample example = new SignInExample();
        example.createCriteria().andSignInIdEqualTo(signInId).andSignInPasswordEqualTo(signInPassword);
        List<SignIn> signIns = signInMapper.selectByExample(example);
        if (signIns != null && signIns.size() > 0) {
            if (signIns.get(0).getSignInId().equals(signInId)) {
                UserSignIn userSignIn = new UserSignIn();
                userSignIn.setUserId(userId);
                userSignIn.setSignInId(signInId);
                userSignIn.setEffective((byte) 1);
                int index = userSignInMapper.insert(userSignIn);
                if (index > 0) {
                    return JsonUtils.objectToJson("success");
                } else {
                    return JsonUtils.objectToJson("fail");
                }
            } else {
                return JsonUtils.objectToJson("fail");
            }
        }
        return JsonUtils.objectToJson("fail");
    }

    @Override
    public String out_sign_in(Integer userId, Integer signInId, String signInPassword) {
        if (userId == null || signInId == null || signInPassword.length() != 4) {
            return JsonUtils.objectToJson("fail");
        }
        SignInExample example = new SignInExample();
        example.createCriteria().andSignInIdEqualTo(signInId).andSignInPasswordEqualTo(signInPassword);
        List<SignIn> signIns = signInMapper.selectByExample(example);
        if (signIns != null && signIns.size() > 0) {
            UserSignInExample userSignInExample = new UserSignInExample();
            userSignInExample.createCriteria().andUserIdEqualTo(userId).andSignInIdEqualTo(signInId);
            int index = userSignInMapper.deleteByExample(userSignInExample);
            if (index > 0) {
                return JsonUtils.objectToJson("success");
            }
            return JsonUtils.objectToJson("fail");
        }
        return JsonUtils.objectToJson("fail");
    }


    @Override
    public String get_myeffective_sign_in(Integer userId) {

        if (userId != null) {
            SignInExample example = new SignInExample();
            example.createCriteria().andUserIdEqualTo(userId).andEffectiveEqualTo((byte) 1);
            List<SignIn> signIns = signInMapper.selectByExample(example);
            List<effectiveSignIn> effectiveSignIns = new ArrayList<>();
            for (SignIn ins : signIns) {
                effectiveSignIn effectiveSignIn = new effectiveSignIn();
                effectiveSignIn.setSignInId(ins.getSignInId());
                effectiveSignIn.setSignInName(ins.getSignInName());
                effectiveSignIn.setSignInStartTime(ins.getStartTime());
                effectiveSignIn.setSignInEndTime(ins.getEndTime());
                effectiveSignIns.add(effectiveSignIn);
            }
            return JsonUtils.objectToJson(effectiveSignIns);
        }
        return JsonUtils.objectToJson("fail");
    }

    @Override
    public String get_effective_sign_in(Integer userId) {
        if (userId != null) {
            UserSignInExample userSignInExample = new UserSignInExample();
            userSignInExample.createCriteria().andUserIdEqualTo(userId).andEffectiveEqualTo((byte) 1);
            List<UserSignIn> userSignIns = userSignInMapper.selectByExample(userSignInExample);
            List<effectiveSignIn> effectiveSignIns = new ArrayList<>();
            for (UserSignIn userSignIn : userSignIns) {
                effectiveSignIn effectiveSignIn = new effectiveSignIn();
                SignInExample signInExample = new SignInExample();
                signInExample.createCriteria().andSignInIdEqualTo(userSignIn.getSignInId());
                List<SignIn> signIns = signInMapper.selectByExample(signInExample);
                if (signIns.size() > 0 && signIns != null) {
                    effectiveSignIn.setSignInId(signIns.get(0).getSignInId());
                    effectiveSignIn.setSignInName(signIns.get(0).getSignInName());
                    effectiveSignIn.setSignInStartTime(signIns.get(0).getStartTime());
                    effectiveSignIn.setSignInEndTime(signIns.get(0).getEndTime());
                    effectiveSignIns.add(effectiveSignIn);
                }
                continue;
            }
            return JsonUtils.objectToJson(effectiveSignIns);
        }
        return JsonUtils.objectToJson("fail");

    }

    @Override
    public String get_myuneffective_sign_in(Integer userId) {
        if (userId != null) {
            SignInExample example = new SignInExample();
            example.createCriteria().andUserIdEqualTo(userId).andEffectiveEqualTo((byte) 0);
            List<SignIn> signIns = signInMapper.selectByExample(example);
            List<effectiveSignIn> effectiveSignIns = new ArrayList<>();
            for (SignIn ins : signIns) {
                effectiveSignIn effectiveSignIn = new effectiveSignIn();
                effectiveSignIn.setSignInId(ins.getSignInId());
                effectiveSignIn.setSignInName(ins.getSignInName());
                effectiveSignIn.setSignInStartTime(ins.getStartTime());
                effectiveSignIn.setSignInEndTime(ins.getEndTime());
                effectiveSignIns.add(effectiveSignIn);
            }
            return JsonUtils.objectToJson(effectiveSignIns);
        }
        return JsonUtils.objectToJson("fail");
    }

    @Override
    public String get_uneffective_sign_in(Integer userId) {
        if (userId != null) {
            UserSignInExample userSignInExample = new UserSignInExample();
            userSignInExample.createCriteria().andUserIdEqualTo(userId).andEffectiveEqualTo((byte) 0);
            List<UserSignIn> userSignIns = userSignInMapper.selectByExample(userSignInExample);
            List<effectiveSignIn> effectiveSignIns = new ArrayList<>();
            for (UserSignIn userSignIn : userSignIns) {
                effectiveSignIn effectiveSignIn = new effectiveSignIn();
                SignInExample signInExample = new SignInExample();
                signInExample.createCriteria().andSignInIdEqualTo(userSignIn.getSignInId());
                List<SignIn> signIns = signInMapper.selectByExample(signInExample);
                if (signIns != null && signIns.size() > 0) {
                    effectiveSignIn.setSignInId(signIns.get(0).getSignInId());
                    effectiveSignIn.setSignInName(signIns.get(0).getSignInName());
                    effectiveSignIn.setSignInStartTime(signIns.get(0).getStartTime());
                    effectiveSignIn.setSignInEndTime(signIns.get(0).getEndTime());
                    effectiveSignIns.add(effectiveSignIn);
                }
                continue;
            }
            return JsonUtils.objectToJson(effectiveSignIns);
        }
        return JsonUtils.objectToJson("fail");
    }

    @Override
    public String get_all_sign_in(Integer userId) {
        if (userId != null) {
            UserSignInExample userSignInExample = new UserSignInExample();
            userSignInExample.createCriteria().andUserIdEqualTo(userId);
            List<UserSignIn> userSignIns = userSignInMapper.selectByExample(userSignInExample);
            List<effectiveSignIn> effectiveSignIns = new ArrayList<>();
            for (UserSignIn userSignIn : userSignIns) {
                effectiveSignIn effectiveSignIn = new effectiveSignIn();
                SignInExample signInExample = new SignInExample();
                signInExample.createCriteria().andSignInIdEqualTo(userSignIn.getSignInId());
                List<SignIn> signIns = signInMapper.selectByExample(signInExample);
                if (signIns != null && signIns.size() > 0) {
                    effectiveSignIn.setSignInId(signIns.get(0).getSignInId());
                    effectiveSignIn.setSignInName(signIns.get(0).getSignInName());
                    effectiveSignIn.setSignInStartTime(signIns.get(0).getStartTime());
                    effectiveSignIn.setSignInEndTime(signIns.get(0).getEndTime());
                    effectiveSignIns.add(effectiveSignIn);
                }
                continue;
            }
            return JsonUtils.objectToJson(effectiveSignIns);
        }

        return JsonUtils.objectToJson("fail");
    }

    @Override
    public String get_all_my_sign_in(Integer userId) {
        if (userId != null) {
            SignInExample example = new SignInExample();
            example.createCriteria().andUserIdEqualTo(userId);
            List<SignIn> signIns = signInMapper.selectByExample(example);
            List<effectiveSignIn> effectiveSignIns = new ArrayList<>();
            if (signIns.size() > 0 && signIns != null) {
                for (SignIn ins : signIns) {
                    effectiveSignIn effectiveSignIn = new effectiveSignIn();
                    effectiveSignIn.setSignInId(ins.getSignInId());
                    effectiveSignIn.setSignInName(ins.getSignInName());
                    effectiveSignIn.setSignInStartTime(ins.getStartTime());
                    effectiveSignIn.setSignInEndTime(ins.getEndTime());
                    effectiveSignIns.add(effectiveSignIn);
                }
            }
            return JsonUtils.objectToJson(effectiveSignIns);
        }
        return JsonUtils.objectToJson("fail");
    }

    /**
     * 高赟三个
     */


    @Override
    public String sign_in(Integer userId, Integer signInId, Integer accuracy, Double siteLo, Double siteLa) {
        if (userId == null || signInId == null || accuracy == null || siteLa == null || siteLo == null) {
            return JsonUtils.objectToJson("fail");
        }
        //参与了这个打卡任务
        UserSignInExample userSignInExample = new UserSignInExample();
        userSignInExample.createCriteria().andUserIdEqualTo(userId).andSignInIdEqualTo(signInId).andEffectiveEqualTo((byte) 1);
        List<UserSignIn> userSignIns = userSignInMapper.selectByExample(userSignInExample);
        //看签到
        SignInExample signInExample = new SignInExample();
        signInExample.createCriteria().andSignInIdEqualTo(signInId);
        List<SignIn> signIns = signInMapper.selectByExample(signInExample);
        if (signIns != null && signIns.size() > 0) {
            if (userSignIns != null && userSignIns.size() > 0) {
                if (userId.equals(userSignIns.get(0).getUserId()) && signInId.equals(userSignIns.get(0).getSignInId())) {
                    Double siteLo1 = signIns.get(0).getSiteLo();
                    Double siteLa1 = signIns.get(0).getSiteLa();
                    if (Math.abs(siteLa1 - siteLa) * 111000 < accuracy && Math.abs(siteLo1 - siteLo) * 111000 < accuracy) {
                        Participate participate = new Participate();
                        participate.setUserId(userId);
                        participate.setSignInId(signInId);
                        participate.setSignInTime(GetId.getNowTime());
                        int index = participateMapper.insert(participate);
                        if (index > 0) {
                            return JsonUtils.objectToJson("succeed");
                        } else {
                            return JsonUtils.objectToJson("fail");
                        }
                    } else {
                        return JsonUtils.objectToJson("fail");
                    }
                } else {
                    return JsonUtils.objectToJson("fail");
                }
            } else {
                return JsonUtils.objectToJson("fail");
            }
        } else {
            return JsonUtils.objectToJson("fail");
        }
    }


    @Override
    public String my_sign_in_detail(Integer userId, Integer signInId) {
        SignDetailResult result = new SignDetailResult();

        if (userId == null || signInId == null) {
            return JsonUtils.objectToJson("fail");
        }
        ParticipateExample participateExample = new ParticipateExample();
        participateExample.createCriteria().andUserIdEqualTo(userId).andSignInIdEqualTo(signInId);
        List<Participate> participates = participateMapper.selectByExample(participateExample);
        if (participates != null && participates.size() > 0) {
            int count = 0;
            int index = 0;
            int sign_in_num = 0;
            int total_num = 0;
            sign_in_num = participates.size();
//            total_num=participates.size();
            Date[] effective_date_list = new Date[sign_in_num];
            Date[] total_date_list = null;
            SignInExample signInExample = new SignInExample();
            signInExample.createCriteria().andSignInIdEqualTo(signInId);
            List<SignIn> signIns = signInMapper.selectByExample(signInExample);
            if (signIns != null && signIns.size() > 0) {
                total_num = GetId.getTimeDistance(signIns.get(0).getStartTime(), signIns.get(0).getEndTime()) + 1;
                total_date_list = new Date[GetId.getTimeDistance(signIns.get(0).getStartTime(), signIns.get(0).getEndTime()) + 1];
                for (int i = 0; i < total_date_list.length; i++) {
                    total_date_list[i] = GetId.addDate(signIns.get(0).getStartTime(), i);
                }
            }
            for (Participate p : participates) {
                if (p.getSignInTime().getTime() >= signIns.get(0).getStartTime().getTime() && p.getSignInTime().getTime() <= signIns.get(0).getEndTime().getTime()) {
                    effective_date_list[count] = p.getSignInTime();
                    count++;
//                    sign_in_num++;
                }
            }
            result.setEffectiveDateList(effective_date_list);
            result.setEffSignInSum(total_num);
            result.setShouldSignInSum(sign_in_num);
            result.setTotalDateList(total_date_list);
            return JsonUtils.objectToJson(result);
        }
        return JsonUtils.objectToJson("fail");
    }


    @Override
    public String get_sign_in_detail(Integer signInId) {
        if (signInId == null) {
            return JsonUtils.objectToJson("status:[fail]");
        }
        signinDetailResult Result = new signinDetailResult();
        int all = 0;
        int index = 0;
        int count = 0;
        int All = 0;
        double rate;
        List userIdList = new ArrayList();
        List AlluserIdList = new ArrayList();
        List<String> userNameList = new ArrayList();
        List<String> UnuserNameList = new ArrayList();
        int[] everyday_number = null;
        SignInExample signInExample = new SignInExample();
        signInExample.createCriteria().andSignInIdEqualTo(signInId);
        List<SignIn> signIns = signInMapper.selectByExample(signInExample);
        int length = 0;
        int k = 1;
        int body = 0;
        Date start = null;
        Date end = null;
        if (signIns != null && signIns.size() > 0) {  //多少天
            length = GetId.getTimeDistance(signIns.get(0).getStartTime(), signIns.get(0).getEndTime()) + 1;
            start = signIns.get(0).getStartTime();
            end = signIns.get(0).getEndTime();
        }
        String[] sign_in_detail_list = new String[length];
        UserSignInExample userSignInExample1 = new UserSignInExample();
        userSignInExample1.createCriteria().andUserIdEqualTo(signInId);
        List<UserSignIn> userSignIn = userSignInMapper.selectByExample(userSignInExample1);
        if (userSignIn != null && userSignIn.size() > 0) {   //参与这个活动的人
            body = userSignIn.size();
        }
        All = length * body;             //每天需要签到的和多少天相乘即可。
//        SignInExample signInExample1=new SignInExample();
//        signInExample1.createCriteria().andSignInIdEqualTo(signInId);
//        List<SignIn> signIns1 = signInMapper.selectByExample(signInExample1);
//        Integer userId = signIns1.get(0).getUserId();
//        UserSignInExample userSignInExample1=new UserSignInExample();
//        userSignInExample1.createCriteria().andUserIdEqualTo(userId).andSignInIdEqualTo(signInId);
//        List<UserSignIn> userSignIns1 = userSignInMapper.selectByExample(userSignInExample1);
//        //进行身份验证    是组长且参与了参与表。
//        if(userSignIns1!=null && userSignIns1.size()>0){
        StatisticsExample statisticsExample=new StatisticsExample();
        statisticsExample.createCriteria().andSignInIdEqualTo(signInId);     //参与任务每日统计，多少条就是多少天。
        List<Statistics> result = statisticsMapper.selectByExample(statisticsExample);
        everyday_number=new int[length];
        if(result!=null && result.size()>0){     //得到总共的签到次数。
            for (Statistics s:result){
                int x=s.getCount();
                everyday_number[index]=x;
                index++;
                all+=x;
            }
        }
        Result.setEveryday_number(everyday_number);
        Date now=start;
        if(result!=null && result.size()>0){     //签到并且是这天的
            for (Statistics s:result){
                if(now.getTime()<end.getTime()){
                    ParticipateExample participateExample =new ParticipateExample();
                    participateExample.createCriteria().andSignInIdEqualTo(signInId).andSignInTimeEqualTo(now);   //参与了这个活动的
                    List<Participate> participates = participateMapper.selectByExample(participateExample);
                    //遍历参与者。
                    if(participates!=null && participates.size()>0){
                        for(Participate p:participates){
                            Integer userPaId = p.getUserId();          //得到id
                            UserExample userExample=new UserExample();
                            userExample.createCriteria().andUserIdEqualTo(userPaId);
                            List<User> users = userMapper.selectByExample(userExample);
                            //放入这个userid的名字。（参与了这个活动的人的名字）
                            if(users!=null && users.size()>0){
                                for (User u:users){
                                    String userName = u.getUserName();
                                    userNameList.add(userName);
                                }
                                userIdList.add(userPaId);
                            }

                            UserSignInExample userSignInExample=new UserSignInExample();
                            userSignInExample.createCriteria().andUserIdEqualTo(userPaId);
                            List<UserSignIn> userSignIns = userSignInMapper.selectByExample(userSignInExample);
                            if(userSignIns!=null && userSignIns.size()>0){
                                for(UserSignIn u:userSignIns){
                                    Integer userSignId = u.getUserId();
                                    AlluserIdList.add(userSignId);
                                }
                            }
                        }
                    }
                    now = GetId.addDate(start, 1);
                }
                sign_in_detail_list[k++]=userNameList.toString()+UnuserNameList.toString();
                userNameList.clear();
                UnuserNameList.clear();
            }
        }


        rate=((double) all/(double) All);
        AlluserIdList.removeAll(userIdList);       //从总的id中去掉签到的就是没签到的。
        for(Object id:AlluserIdList){           //遍历user得到他的名字，放入未签到表中。
            UserExample userExample=new UserExample();
            userExample.createCriteria().andUserIdEqualTo((Integer) id);
            List<User> users=userMapper.selectByExample(userExample);
            if(users!=null && users.size()>0){
                UnuserNameList.add(users.get(0).getUserName());
            }
        }
        Result.setSign_in_rate(rate);
        Result.setSign_in_detail_list(sign_in_detail_list);
        return JsonUtils.objectToJson(Result);
    }
}
