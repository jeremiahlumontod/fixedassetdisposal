package org.jml.fixedassetdisposal;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jml on 5/30/16.
 */
public class ProcessNodeNonFlag implements IProcessNode{

    @Autowired
    private TaskService taskService;

    @Override
    public void completeThisTask(String procID, String taskID) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(procID)
                .taskId(taskID)
                .singleResult();

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("bflag", "false");
        taskService.complete(task.getId(), taskVariables);
        System.out.println("completeTelephoneInterviewTask for process:" + procID + ", " + "task id:" + taskID + " executed...");

    }
}
