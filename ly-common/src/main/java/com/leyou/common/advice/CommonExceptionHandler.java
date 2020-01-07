package com.leyou.common.advice;


import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptiom.LyException;
import com.leyou.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * 处理异常信息
 */

/**
 * 该注解：自动拦截所有的Controller
 */
@ControllerAdvice
public class CommonExceptionHandler {

    /**
     * 处理异常，返回值：要返回到页面的信息
     * 注解：处理运行时异常{Exceptions handled by the annotated method. If empty, will default to any
     * exceptions listed in the method argument list.}
     * @param e
     * @return
     */
    @ExceptionHandler(LyException.class)
    public ResponseEntity getEntity(LyException e){
        ExceptionEnum exceptionEnum = e.getExceptionEnum();
        return ResponseEntity.status(exceptionEnum.getCode()).body(new ExceptionResult(exceptionEnum));
    }


}
