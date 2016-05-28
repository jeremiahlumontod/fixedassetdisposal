package org.jml.fixedassetdisposal;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.jml.fixedassetdisposal.repository.BPMApp;
import org.jml.fixedassetdisposal.repository.BpmRepository;
import org.jml.fixedassetdisposal.repository.ProcessDetails;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.subethamail.wiser.Wiser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {BPMApp.class})
@WebAppConfiguration
@IntegrationTest
public class fixedassetdisposalTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private BpmRepository bpmRepository;

    private Wiser wiser;

    @Before
    public void setup() {

    }

    @After
    public void cleanup() {

    }


    public List<Task> getTaskListTask(String procID) {

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID)
                .orderByTaskName().asc()
                .list();

        return tasks;
    }


    @Test
    public void testStartProcess() throws Exception {

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("newstring", new String());

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("accountant_Fixed_Asset_Disposal", variables);
        String procID = processInstance.getId();
        System.out.println("processInstance.getId()" + processInstance.getId());

        ProcessDetails processDetails= new ProcessDetails();

        com.cbody.cbody2 doc = com.cbody.cbody2.createDocument();
        com.cbody.detailsType details = doc.details.append();
        com.cbody.stepsType steps = details.steps.append();
        com.cbody.nextstepType nextstep = steps.nextstep.append();
        com.cbody.procidinstanceType procidinstance = nextstep.procidinstance.append();
        procidinstance.setValue(procID);

        List<Task> tasks = getTaskListTask(procID);
        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            com.cbody.taskidType taskid = nextstep.taskid.append();
            taskid.setValue(task.getId());
            com.cbody.tasknameType taskname = nextstep.taskname.append();
            taskname.setValue(task.getName());
            com.cbody.stepType step = nextstep.step.append();
            step.setValue(task.getId());
            com.cbody.useridType userid = nextstep.userid.append();
            userid.setValue("testuser");
            com.cbody.bflagType bflag = nextstep.bflag.append();
            bflag.setValue("false");
            com.cbody.completedType completed = nextstep.completed.append();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String d = dateFormat.format(date);
            completed.setValue(d);


            System.out.println("task.getId():" + task.getId() + ", " + "task.getName():" + task.getName());
        }

        String s = doc.saveToString(true);
        processDetails.setCbody(s);

        bpmRepository.save(processDetails);

    }





    @Test
    public void displayTaskList() {
        displayTaskListTask("47505");

    }

    public void displayTaskListTask(String procID) {

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID)
                .orderByTaskName().asc()
                .list();

        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println("task.getId():" + task.getId() + ", " + "task.getName():" + task.getName());
        }

    }



    @Test
    public void checkProcessInstance() {
        checkProcessInstanceTask("25001");

    }

    public void checkProcessInstanceTask(String procID) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID)
                .orderByTaskName().asc()
                .list();
        String s = null;
        if(tasks==null) {
            s = "process finished";
        }
        if(tasks.size()< 1) {
            s = "process finished";
        }
        if(tasks.size()>0) {
            s = "process not finish";
        }
        System.out.println("process status: " + s);
    }

}
