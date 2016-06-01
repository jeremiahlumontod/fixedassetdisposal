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
    public void runTest() throws Exception {
        //step 1. create instance of the process
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
        processDetails.setProcidinstance(procID);
        processDetails.setProcid("accountant_Fixed_Asset_Disposal");
        processDetails = bpmRepository.save(processDetails);


        //step 2. loop thru all the task node and simulate executed task node
        do {
            processDetails = bpmRepository.findOne(processDetails.getId());
            doc = com.cbody.cbody2.loadFromString(processDetails.getCbody());
            String processID = doc.details.first().steps.first().nextstep.first().procidinstance.first().getValue();
            String taskID = doc.details.first().steps.first().nextstep.first().taskid.first().getValue();
            String taskName = doc.details.first().steps.first().nextstep.first().taskname.first().getValue();
            String bflag = doc.details.first().steps.first().nextstep.first().bflag.first().getValue();


            completeThisTask(processID,taskID);

            /**
             * todo: get next node id and name...update processDetails with that value
             * then write to db thru jpa. there is no parallel task so its safe
             * to think that only one node will be picked up at any given time
             */

            String p = getNextTaskInfo(processID);
            if (p == null) {
                break;
            }
            String[] ap = ProcessUtils.splitString(p,"=");
            taskID = ap[0];
            taskName = ap[1];

            /**
             * create new document
             *
             */

            doc = com.cbody.cbody2.createDocument();
            details = doc.details.append();
            steps = details.steps.append();
            nextstep = steps.nextstep.append();
            procidinstance = nextstep.procidinstance.append();
            procidinstance.setValue(processID);


            com.cbody.taskidType taskid = nextstep.taskid.append();
            taskid.setValue(taskID);
            com.cbody.tasknameType taskname = nextstep.taskname.append();
            taskname.setValue(taskName);
            com.cbody.stepType step = nextstep.step.append();
            step.setValue(taskID);
            com.cbody.useridType userid = nextstep.userid.append();
            userid.setValue("testuser");
            com.cbody.bflagType bflagElement = nextstep.bflag.append();
            bflagElement.setValue("false");
            com.cbody.completedType completed = nextstep.completed.append();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String d = dateFormat.format(date);
            completed.setValue(d);

            String cbody = doc.saveToString(true);
            processDetails= new ProcessDetails();
            processDetails.setCbody(cbody);
            processDetails.setProcidinstance(procID);
            processDetails.setProcid("accountant_Fixed_Asset_Disposal");
            processDetails = bpmRepository.save(processDetails);

        }while (!checkProcessStatus(procID).equalsIgnoreCase("process finished"));



    }






    @Test
    public void testStartProcess() throws Exception {
        testStartProcessTask();
    }

    public void testStartProcessTask() throws Exception {

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
        processDetails.setProcidinstance(procID);
        processDetails.setProcid("accountant_Fixed_Asset_Disposal");
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

    public String checkProcessStatus(String procID) {
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
        return s;
    }



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
        if(tasks!=null) {
            try {
                s = tasks.get(0).getId() + "=" + tasks.get(0).getName();
            }catch(Exception e) {
            }
        }
        return s;

    }

}
