package com.magician.tools.processing.concurrent.task;

import com.magician.tools.processing.commons.enums.ConcurrentTaskResultEnum;

/**
 * 回调函数
 */
public interface ConcurrentTaskCall {

    void call(ConcurrentTaskResultEnum result, Throwable throwable);

}
