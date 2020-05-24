package com.orange.admin.common.biz.advice;

import com.orange.admin.common.core.exception.InvalidClassFieldException;
import com.orange.admin.common.core.exception.InvalidDataFieldException;
import com.orange.admin.common.core.exception.InvalidDataModelException;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.exception.RedisCacheAccessException;
import com.orange.admin.common.core.object.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeoutException;

/**
 * 业务层的异常处理类，这里只是给出最通用的Exception的捕捉，今后可以根据业务需要，
 * 用不同的函数，处理不同类型的异常。
 *
 * @author Stephen.Liu
 * @date 2020-05-24
 */
@Slf4j
@RestControllerAdvice
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
		return ResponseResult.error(ErrorCodeEnum.UNHANDLED_EXCEPTION);
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
