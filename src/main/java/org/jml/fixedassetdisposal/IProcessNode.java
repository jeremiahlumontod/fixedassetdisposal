package org.jml.fixedassetdisposal;

import org.activiti.engine.TaskService;

/**
 * Created by jml on 5/30/16.
 */
public interface IProcessNode {
    public void completeThisTask(String procID, String taskID, TaskService taskService);

}
