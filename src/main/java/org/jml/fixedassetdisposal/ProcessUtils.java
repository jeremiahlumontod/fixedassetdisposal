package org.jml.fixedassetdisposal;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jml on 5/30/16.
 */
public class ProcessUtils {

    private HistoryService historyService;
    private TaskService taskService;

    public String getProcessesName(String procID) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procID).singleResult();
        String n = null;
        if (historicProcessInstance != null) {
            n = historicProcessInstance.getName();
        }
        return n;
    }

    public static String[] splitString(String s, String splitter) {
        String[] a = s.split(splitter);
        return a;
    }

    public void completeThisTask(String procID, String taskID) {
        String pname = getTaskName(procID,taskID);
        if(pname!=null) {
            String[] aname = ProcessUtils.splitString(pname,"\\|");
            if(aname.length>2) { //flag is at 3rd element
                String bflag = aname[2].trim(); //array is based zero
                String procName = getProcessesName(procID);
                if(bflag.equalsIgnoreCase("bflag")) {
                    IProcessNode processNode = new ProcessNodeFlag();
                    processNode.completeThisTask(procID,taskID,taskService);
                }else{
                    IProcessNode processNode = new ProcessNodeNonFlag();
                    processNode.completeThisTask(procID,taskID,taskService);
                }
            }else if(aname.length==2) {
                String procName = getProcessesName(procID);
                IProcessNode processNode = new ProcessNodeNonFlag();
                processNode.completeThisTask(procID,taskID,taskService);
            }
        }
    }

    public String getTaskName(String procID, String taskID) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(procID).taskId(taskID)
                .orderByTaskName().asc()
                .singleResult();

        if(task!=null) {
            return task.getName();
        }else{
            return null;
        }
    }


    public String getNextTaskInfo(String procID) {

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID)
                .orderByTaskName().asc()
                .list();
        String s = null;
        if(s!=null) {
            s = tasks.get(0).getId() + "|" + tasks.get(0).getName();
        }
        return s;

    }

    public HistoryService getHistoryService() {
        return historyService;
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }
}
