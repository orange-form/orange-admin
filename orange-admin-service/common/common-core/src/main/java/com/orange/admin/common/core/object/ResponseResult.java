package com.orange.admin.common.core.object;

import com.orange.admin.common.core.constant.ErrorCodeEnum;
import lombok.Data;

/**
 * 接口返回对象。
 *
 * @author Stephen.Liu
 * @date 2020-04-11
 */
@Data
public class ResponseResult<T> {

    /**
     * 是否成功标记。
     */
    private boolean success = true;
    /**
     * 错误码。
     */
    private String errorCode = "NO-ERROR";
    /**
     * 错误信息描述。
     */
    private String errorMessage = "NO-MESSAGE";
    /**
     * 实际数据。
     */
    private T data = null;

    /**
     * 根据参数errorCodeEnum的枚举值，判断创建成功对象还是错误对象。
     * 如果返回错误对象，errorCode 和 errorMessage 分别取自于参数 errorCodeEnum 的 name() 和 getErrorMessage()。
     *
     * @param errorCodeEnum - 错误码枚举
     * @return 返回创建的ResponseResult实例对象
     */
    public static <T> ResponseResult<T> create(ErrorCodeEnum errorCodeEnum) {
        return create(errorCodeEnum, errorCodeEnum.getErrorMessage());
    }

    /**
     * 根据参数errorCodeEnum的枚举值，判断创建成功对象还是错误对象。
     * 如果返回错误对象，errorCode 和 errorMessage 分别取自于参数 errorCodeEnum 的 name() 和参数 errorMessage。
     *
     * @param errorCodeEnum - 错误码枚举。
     * @param errorMessage  - 如果该参数为null，错误信息取自errorCodeEnum参数内置的errorMessage，否则使用当前参数。
     * @return 返回创建的ResponseResult实例对象
     */
    public static <T> ResponseResult<T> create(ErrorCodeEnum errorCodeEnum, String errorMessage) {
        return errorCodeEnum == ErrorCodeEnum.NO_ERROR ? success()
                : error(errorCodeEnum.name(), errorMessage != null ? errorMessage : errorCodeEnum.getErrorMessage());
    }

    /**
     * 根据参数errorCodeEnum的枚举值，判断创建成功对象还是错误对象。
     * 如果返回错误对象，errorCode 和 errorMessage 分别取自于参数 errorCodeEnum 的 name() 和参数 errorMessage。
     * 如果返回的是成功对象，dataObject数据对象将被连同返回。
     *
     * @param errorCodeEnum - 错误码枚举。
     * @param errorMessage  - 如果该参数为null，错误信息取自errorCodeEnum参数内置的errorMessage，否则使用当前参数。
     * @param data - 返回的数据对象。
     * @return 返回创建的ResponseResult实例对象
     */
    public static <T> ResponseResult<T> create(ErrorCodeEnum errorCodeEnum, String errorMessage, T data) {
        return errorCodeEnum == ErrorCodeEnum.NO_ERROR ? success(data) : create(errorCodeEnum, errorMessage);
    }

    /**
     * 根据参数errorCode是否为空，判断创建成功对象还是错误对象。
     * 如果返回错误对象，errorCode 和 errorMessage 分别取自于参数 errorCode 和参数 errorMessage。
     *
     * @param errorCode    - 自定义的错误码
     * @param errorMessage - 自定义的错误信息
     * @return 返回创建的ResponseResult实例对象
     */
    public static <T> ResponseResult<T> create(String errorCode, String errorMessage) {
        return errorCode == null ? success() : error(errorCode, errorMessage);
    }

    /**
     * 创建成功对象。
     * 如果需要绑定返回数据，可以在实例化后调用setDataObject方法。
     *
     * @return 返回创建的ResponseResult实例对象
     */
    public static <T> ResponseResult<T> success() {
        return success(null);
    }

    /**
     * 创建带有返回数据的成功对象。
     *
     * @param data - 返回的数据对象
     * @return 返回创建的ResponseResult实例对象
     */
    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> resp = new ResponseResult<>();
        resp.data = data;
        return resp;
    }

    /**
     * 创建错误对象。
     * 如果返回错误对象，errorCode 和 errorMessage 分别取自于参数 errorCodeEnum 的 name() 和 getErrorMessage()。
     *
     * @param errorCodeEnum - 错误码枚举
     * @return 返回创建的ResponseResult实例对象
     */
    public static <T> ResponseResult<T> error(ErrorCodeEnum errorCodeEnum) {
        return error(errorCodeEnum.name(), errorCodeEnum.getErrorMessage());
    }

    /**
     * 创建错误对象。
     * 如果返回错误对象，errorCode 和 errorMessage 分别取自于参数 errorCodeEnum 的 name() 和参数 errorMessage。
     *
     * @param errorCodeEnum - 错误码枚举
     * @param errorMessage  - 自定义的错误信息
     * @return 返回创建的ResponseResult实例对象
     */
    public static <T> ResponseResult<T> error(ErrorCodeEnum errorCodeEnum, String errorMessage) {
        return error(errorCodeEnum.name(), errorMessage);
    }

    /**
     * 创建错误对象。
     * 如果返回错误对象，errorCode 和 errorMessage 分别取自于参数 errorCode 和参数 errorMessage。
     *
     * @param errorCode    - 自定义的错误码
     * @param errorMessage - 自定义的错误信息
     * @return 返回创建的ResponseResult实例对象
     */
    public static <T> ResponseResult<T> error(String errorCode, String errorMessage) {
        return new ResponseResult<>(false, errorCode, errorMessage);
    }

    /**
     * 是否成功。
     *
     * @return true成功，否则false。
     */
    public boolean isSuccess() {
        return success;
    }

    private ResponseResult() {

    }

    private ResponseResult(boolean success, String errorCode, String errorMessage) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
