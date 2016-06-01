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

    @Override
    public void completeThisTask(String procID, String taskID, TaskService taskService) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(procID)
                .taskId(taskID)
                .singleResult();

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("bflag", "false");
        taskService.complete(task.getId(), taskVariables);
        System.out.println("completeThisTask for process:" + procID + ", " + "task id:" + taskID + ", task name: " + task.getName() + " executed...");

    }
}
