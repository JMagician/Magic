package com.magic.processing.disruptor;


import com.magic.processing.commons.TaskData;

public class DisruptorEvent {
    private TaskData taskData;
    
    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }
    
    public TaskData getTaskData() {
        return taskData;
    }
}