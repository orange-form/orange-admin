package com.flow.demo.common.core.advice;

import com.flow.demo.common.core.exception.*;
import com.flow.demo.common.core.constant.ErrorCodeEnum;
import com.flow.demo.common.core.object.ResponseResult;
import com.flow.demo.common.core.util.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeoutException;

/**
 * 业务层的异常处理类，这里只是给出最通用的Exception的捕捉，今后可以根据业务需要，
 * 用不同的函数，处理不同类型的异常。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@Slf4j
@RestControllerAdvice("com.flow.demo")
public class MyExceptionHandler {

	/**
	 * 通用异常处理方法。
	 *
	 * @param ex       异常对象。
	 * @param request  http请求。
	 * @return 应答对象。
	 */
    @ExceptionHandler(value = Exception.class)
	public ResponseResult<Void> exceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("Unhandled exception from URL [" + request.getRequestURI() + "]", ex);
		ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return ResponseResult.error(ErrorCodeEnum.UNHANDLED_EXCEPTION, ex.getMessage());
	}

	/**
	 * 无效的实体对象异常。
	 *
	 * @param ex       异常对象。
	 * @param request  http请求。
	 * @return 应答对象。
	 */
	@ExceptionHandler(value = InvalidDataModelException.class)
	public ResponseResult<Void> invalidDataModelExceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("InvalidDataModelException exception from URL [" + request.getRequestURI() + "]", ex);
		return ResponseResult.error(ErrorCodeEnum.INVALID_DATA_MODEL);
	}

	/**
	 * 无效的实体对象字段异常。
	 *
	 * @param ex       异常对象。
	 * @param request  http请求。
	 * @return 应答对象。
	 */
	@ExceptionHandler(value = InvalidDataFieldException.class)
	public ResponseResult<Void> invalidDataFieldExceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("InvalidDataFieldException exception from URL [" + request.getRequestURI() + "]", ex);
		return ResponseResult.error(ErrorCodeEnum.INVALID_DATA_FIELD);
	}

	/**
	 * 无效类字段异常。
	 *
	 * @param ex       异常对象。
	 * @param request  http请求。
	 * @return 应答对象。
	 */
	@ExceptionHandler(value = InvalidClassFieldException.class)
	public ResponseResult<Void> invalidClassFieldExceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("InvalidClassFieldException exception from URL [" + request.getRequestURI() + "]", ex);
		return ResponseResult.error(ErrorCodeEnum.INVALID_CLASS_FIELD);
	}

	/**
	 * 重复键异常处理方法。
	 *
	 * @param ex       异常对象。
	 * @param request  http请求。
	 * @return 应答对象。
	 */
	@ExceptionHandler(value = DuplicateKeyException.class)
	public ResponseResult<Void> duplicateKeyExceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("DuplicateKeyException exception from URL [" + request.getRequestURI() + "]", ex);
		return ResponseResult.error(ErrorCodeEnum.DUPLICATED_UNIQUE_KEY);
	}

	/**
	 * 数据访问失败异常处理方法。
	 *
	 * @param ex       异常对象。
	 * @param request  http请求。
	 * @return 应答对象。
	 */
	@ExceptionHandler(value = DataAccessException.class)
	public ResponseResult<Void> dataAccessExceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("DataAccessException exception from URL [" + request.getRequestURI() + "]", ex);
		if (ex.getCause() instanceof PersistenceException
				&& ex.getCause().getCause() instanceof PermissionDeniedDataAccessException) {
			return ResponseResult.error(ErrorCodeEnum.DATA_PERM_ACCESS_FAILED);
		}
		return ResponseResult.error(ErrorCodeEnum.DATA_ACCESS_FAILED);
	}

	/**
	 * 操作不存在或已逻辑删除数据的异常处理方法。
	 *
	 * @param ex       异常对象。
	 * @param request  http请求。
	 * @return 应答对象。
	 */
	@ExceptionHandler(value = NoDataAffectException.class)
	public ResponseResult<Void> noDataEffectExceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("NoDataAffectException exception from URL [" + request.getRequestURI() + "]", ex);
		return ResponseResult.error(ErrorCodeEnum.DATA_NOT_EXIST);
	}

	/**
	 * Redis缓存访问异常处理方法。
	 *
	 * @param ex       异常对象。
	 * @param request  http请求。
	 * @return 应答对象。
	 */
	@ExceptionHandler(value = RedisCacheAccessException.class)
	public ResponseResult<Void> redisCacheAccessExceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("RedisCacheAccessException exception from URL [" + request.getRequestURI() + "]", ex);
		if (ex.getCause() instanceof TimeoutException) {
			return ResponseResult.error(ErrorCodeEnum.REDIS_CACHE_ACCESS_TIMEOUT);
		}
		return ResponseResult.error(ErrorCodeEnum.REDIS_CACHE_ACCESS_STATE_ERROR);
	}
}
