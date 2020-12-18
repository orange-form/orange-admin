package com.orange.demo.statsinterface.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * StudentActionTransVO对象。
 *
 * @author Jerry
 * @date 2020-08-08
 */
@Data
public class StudentActionTransVo {

    /**
     * 主键Id。
     */
    private Long transId;

    /**
     * 学生Id。
     */
    private Long studentId;

    /**
     * 学生名称。
     */
    private String studentName;

    /**
     * 学生校区。
     */
    private Long schoolId;

    /**
     * 年级Id。
     */
    private Integer gradeId;

    /**
     * 行为类型(0: 充值  1: 购课 2: 上课签到 3: 上课签退 4: 看视频课 5: 做作业 6: 刷题 7: 献花)。
     */
    private Integer actionType;

    /**
     * 设备类型(0: iOS 1: Android 2: PC)。
     */
    private Integer deviceType;

    /**
     * 看视频秒数。
     */
    private Integer watchVideoSeconds;

    /**
     * 购买献花数量。
     */
    private Integer flowerCount;

    /**
     * 购买作业数量。
     */
    private Integer paperCount;

    /**
     * 购买视频数量。
     */
    private Integer videoCount;

    /**
     * 购买课程数量。
     */
    private Integer courseCount;

    /**
     * 充值学币数量。
     */
    private Integer coinCount;

    /**
     * 做题是否正确标记。
     */
    private Integer exerciseCorrectFlag;

    /**
     * 发生时间。
     */
    private Date createTime;

    /**
     * schoolId 字典关联数据。
     */
    private Map<String, Object> schoolIdDictMap;

    /**
     * gradeId 字典关联数据。
     */
    private Map<String, Object> gradeIdDictMap;

    /**
     * actionType 常量字典关联数据。
     */
    private Map<String, Object> actionTypeDictMap;

    /**
     * deviceType 常量字典关联数据。
     */
    private Map<String, Object> deviceTypeDictMap;
}
