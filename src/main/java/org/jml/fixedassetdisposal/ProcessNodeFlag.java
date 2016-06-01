package org.jml.fixedassetdisposal;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jml on 5/30/16.
 */
public class ProcessNodeFlag implements IProcessNode{

    @Override
    public void completeThisTask(String procID, String taskID, TaskService taskService) {
        ProcessUtils p = new ProcessUtils();
        p.setTaskService(taskService);
        String pname = p.getTaskName(procID,taskID);
        if(pname!=null) {
            String[] aname = ProcessUtils.splitString(pname,"\\|");
            if(aname.length>2) { //flag is at 3rd element
                String bflag = aname[2].trim(); //array is based zero
                Map<String, Object> taskVariables = new HashMap<String, Object>();
                if(bflag.equalsIgnoreCase("bflag")) {
                    taskVariables.put("bflag", "true");
                }else{
                    taskVariables.put("bflag", "false");
                }
                Task task = taskService.createTaskQuery()
                        .processInstanceId(procID)
                        .taskId(taskID)
                        .singleResult();
                taskService.complete(task.getId(), taskVariables);
                System.out.println("completeThisTask for process:" + procID + ", " + "task id:" + taskID + ", task name: " + pname + " executed...");
            }
        }

    }
}
