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

    @Autowired
    private HistoryService historyService;

    @Autowired
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
        String pname = new ProcessUtils().getTaskName(procID,taskID);
        if(pname!=null) {
            String[] aname = ProcessUtils.splitString(pname,"\\|");
            if(aname.length>2) { //flag is at 3rd element
                String bflag = aname[2].trim(); //array is based zero
                String procName = new ProcessUtils().getProcessesName(procID);
                if(bflag.equalsIgnoreCase("bflag")) {
                    IProcessNode processNode = new ProcessNodeFlag();
                    processNode.completeThisTask(procID,taskID);
                }else{
                    IProcessNode processNode = new ProcessNodeNonFlag();
                    processNode.completeThisTask(procID,taskID);
                }
            }else if(aname.length==2) {
                String procName = new ProcessUtils().getProcessesName(procID);
                IProcessNode processNode = new ProcessNodeNonFlag();
                processNode.completeThisTask(procID,taskID);
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

}
