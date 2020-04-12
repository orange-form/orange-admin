package com.orange.admin.common.biz.advice;

import lombok.extern.slf4j.Slf4j;
import com.orange.admin.common.core.constant.ErrorCodeEnum;
import com.orange.admin.common.core.exception.RedisCacheAccessException;
import com.orange.admin.common.core.object.ResponseResult;
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
 * @date 2020-04-11
 */
@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = Exception.class)
	public ResponseResult<?> exceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("Unhandled exception from URL [" + request.getRequestURI() + "]", ex);
		return ResponseResult.error(ErrorCodeEnum.UNHANDLED_EXCEPTION);
	}

	@ExceptionHandler(value = DuplicateKeyException.class)
	public ResponseResult<?> duplicateKeyExceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("DuplicateKeyException exception from URL [" + request.getRequestURI() + "]", ex);
		return ResponseResult.error(ErrorCodeEnum.DUPLICATED_UNIQUE_KEY);
	}

	@ExceptionHandler(value = DataAccessException.class)
	public ResponseResult<?> dataAccessExceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("DataAccessException exception from URL [" + request.getRequestURI() + "]", ex);
		if (ex.getCause() instanceof PersistenceException
				&& ex.getCause().getCause() instanceof PermissionDeniedDataAccessException) {
			return ResponseResult.error(ErrorCodeEnum.DATA_PERM_ACCESS_FAILED);
		}
		return ResponseResult.error(ErrorCodeEnum.DATA_ACCESS_FAILED);
	}

	@ExceptionHandler(value = RedisCacheAccessException.class)
	public ResponseResult<?> redisCacheAccessExceptionHandle(Exception ex, HttpServletRequest request) {
		log.error("RedisCacheAccessException exception from URL [" + request.getRequestURI() + "]", ex);
		if (ex.getCause() instanceof TimeoutException) {
			return ResponseResult.error(ErrorCodeEnum.REDIS_CACHE_ACCESS_TIMEOUT);
		}
		return ResponseResult.error(ErrorCodeEnum.REDIS_CACHE_ACCESS_STATE_ERROR);
	}
}
