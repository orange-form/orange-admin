package com.orangeforms.common.online.util;

/**
 * 在线表单的工具类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class OnlineUtil {

    /**
     * 根据输入参数，拼接在线表单操作的查看权限字。
     *
     * @param datasourceVariableName 数据源变量名。
     * @return 拼接后的在线表单操作的查看权限字。
     */
    public static String makeViewPermCode(String datasourceVariableName) {
        return "online:" + datasourceVariableName + ":view";
    }

    /**
     * 根据输入参数，拼接在线表单操作的编辑权限字。
     *
     * @param datasourceVariableName 数据源变量名。
     * @return 拼接后的在线表单操作的编辑权限字。
     */
    public static String makeEditPermCode(String datasourceVariableName) {
        return "online:" + datasourceVariableName + ":edit";
    }
}
