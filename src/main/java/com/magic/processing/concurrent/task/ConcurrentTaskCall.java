package com.magic.processing.concurrent.task;

import com.magic.processing.commons.enums.ConcurrentTaskResultEnum;

/**
 * 回调函数
 */
public interface ConcurrentTaskCall {

    void call(ConcurrentTaskResultEnum result, Throwable throwable);

}
