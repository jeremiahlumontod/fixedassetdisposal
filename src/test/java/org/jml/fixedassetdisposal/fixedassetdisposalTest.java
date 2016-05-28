package org.jml.fixedassetdisposal;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {MyApp.class})
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
    private ApplicantRepository applicantRepository;

    private Wiser wiser;

    @Before
    public void setup() {

    }

    @After
    public void cleanup() {

    }

    @Test
    public void testHappyPath() {

        // Create test applicant
        Applicant applicant = new Applicant("John Doe", "john@activiti.org", "12344");
        applicantRepository.save(applicant);

        // Start process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("applicant", applicant);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("hireProcessWithJpa", variables);

        // First, the 'phone interview' should be active
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskCandidateGroup("dev-managers")
                .singleResult();
        Assert.assertEquals("Telephone interview", task.getName());

        // Completing the phone interview with success should trigger two new tasks
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("telephoneInterviewOutcome", true);
        taskService.complete(task.getId(), taskVariables);

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .orderByTaskName().asc()
                .list();
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals("Financial negotiation", tasks.get(0).getName());
        Assert.assertEquals("Tech interview", tasks.get(1).getName());

        // Completing both should wrap up the subprocess, send out the 'welcome mail' and end the process instance
        taskVariables = new HashMap<String, Object>();
        taskVariables.put("techOk", true);
        taskService.complete(tasks.get(0).getId(), taskVariables);

        taskVariables = new HashMap<String, Object>();
        taskVariables.put("financialOk", true);
        taskService.complete(tasks.get(1).getId(), taskVariables);

        // Verify email
        Assert.assertEquals(1, wiser.getMessages().size());

        // Verify process completed
        Assert.assertEquals(1, historyService.createHistoricProcessInstanceQuery().finished().count());

    }



    @Test
    public void testStartProcess() {

        // Create test applicant
        Applicant applicant = new Applicant("John Doe", "john@activiti.org", "12344");
        applicantRepository.save(applicant);

        // Start process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("applicant", applicant);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("boot4", variables);
        System.out.println("processInstance.getId()" + processInstance.getId());
    }

    @Test
    public void testStartPhoneInterviewProcess() {

        // Create test applicant
        Applicant applicant = new Applicant("John Doe", "john@activiti.org", "12344");
        applicantRepository.save(applicant);

        // Start process instance
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("applicant", applicant);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("hireProcessWithJpa", variables);
        System.out.println("processInstance.getId()" + processInstance.getId());

        // First, the 'phone interview' should be active
        Task task = taskService.createTaskQuery()
                //.processInstanceId(processInstance.getId())
                .processInstanceId("2501")
                .taskCandidateGroup("dev-managers")
                .singleResult();

        System.out.println("task.getName(): " + task.getName());
        Assert.assertEquals("Telephone interview", task.getName());


    }

    @Test
    public void deleteProcInstance() {
        //deleteProcInstanceTask("5001");
        deleteProcInstanceTask("55001");
    }

    public void deleteProcInstanceTask(String procID) {
        runtimeService.deleteProcessInstance(procID,"not interested");
    }


    @Test
    public void displayTaskList() {
        //taskList("2501");
        //taskList("5001");
        //taskList("37501");
        //taskList("42501");
        //taskList("62501");
        //taskList("11");
        taskList("25001");



    }

    @Test
    public void displayTaskListPerTask() {
        //taskListPerTask("2501","7511");
        //taskListPerTask("2501","7515");
        taskListPerTask("5001","5006");
    }

    @Test
    public void showEmail() {
        showEmailTask("5001");
    }

    @Test
    public void completeTelephoneInterview() {
        //completeTelephoneInterviewTask("5001","5006");
        completeTelephoneInterviewTask("25001","25006");
    }

    @Test
    public void uncompleteTelephoneInterview() {
        //uncompleteTelephoneTask("5001","5006");
        //uncompleteTelephoneInterviewTask("37501","37506");
        //uncompleteTelephoneInterviewTask("42501","42506");
        //uncompleteTelephoneInterviewTask("62501","62506");
        //uncompleteTelephoneInterviewTask("11","16");
        uncompleteTelephoneInterviewTask("15001","15006");

    }


    public void completeTelephoneInterviewTask(String procID, String taskID) {

        // First, the 'phone interview' should be active
        Task task = taskService.createTaskQuery()
                .processInstanceId(procID)
                .taskCandidateGroup("dev-managers")
                .taskId(taskID)
                .singleResult();

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("telephoneInterviewOutcome", true);

        taskService.complete(task.getId(), taskVariables);
        System.out.println("completeTelephoneInterviewTask for process:" + procID + ", " + "task id:" + taskID + " executed...");


    }

    public void uncompleteTelephoneInterviewTask(String procID, String taskID) {

        // First, the 'phone interview' should be active
        Task task = taskService.createTaskQuery()
                .processInstanceId(procID)
                .taskCandidateGroup("dev-managers")
                .taskId(taskID)
                .singleResult();

        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("telephoneInterviewOutcome", false);
        taskVariables.put("applicant.email", "jeremiah.lumontod@gmail.com");

        taskService.complete(task.getId(), taskVariables);
        System.out.println("uncompleteTelephoneInterviewTask for process:" + procID + ", " + "task id:" + taskID + " executed...");
        //runtimeService.suspendProcessInstanceById(procID);
        //runtimeService.deleteProcessInstance(procID,"not interested");
    }


    @Test
    public void completeFinancialNegotiation() {
        //completeFinancialNegotiationTask("2501","7514");
        //completeFinancialNegotiationTask("5001","27514");
        completeFinancialNegotiationTask("25001","27514");
    }

    public void completeFinancialNegotiationTask(String procID, String taskID) {
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables = new HashMap<String, Object>();
        taskVariables.put("financialOk", true);

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID).taskId(taskID)
                .orderByTaskName().asc()
                .list();

        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println("task.getId():" + task.getId() + ", " + "task.getName():" + task.getName() + " completed...");
            taskService.complete(tasks.get(0).getId(), taskVariables);
        }
    }

    @Test
    public void completeTechnicalInterview() {
        //completeTechnicalInterviewTask("2501","7511");
        //completeTechnicalInterviewTask("5001","27511");
        completeTechnicalInterviewTask("25001","27511");
    }

    public void completeTechnicalInterviewTask(String procID, String taskID) {
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables = new HashMap<String, Object>();
        taskVariables.put("techOk", true);

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID).taskId(taskID)
                .orderByTaskName().asc()
                .list();

        if(tasks.size()<1) {
            System.out.println("no record found!!!");
        }
        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println("task.getId():" + task.getId() + ", " + "task.getName():" + task.getName() + " completed...");
            taskService.complete(tasks.get(0).getId(), taskVariables);
        }


    }

    public void uncompleteTechnicalInterviewTask(String procID, String taskID) {
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables = new HashMap<String, Object>();
        taskVariables.put("techOk", true);

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID).taskId(taskID)
                .orderByTaskName().asc()
                .list();

        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println("task.getId():" + task.getId() + ", " + "task.getName():" + task.getName() + " uncompleted...");
            taskService.complete(tasks.get(0).getId(), taskVariables);
        }


    }

    public void taskList(String procID) {

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID)
                .orderByTaskName().asc()
                .list();

        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println("task.getId():" + task.getId() + ", " + "task.getName():" + task.getName());
        }

    }

    public void taskListPerTask(String procID, String taskID) {

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID).taskId(taskID)
                .orderByTaskName().asc()
                .list();

        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println("task.getId():" + task.getId() + ", " + "task.getName():" + task.getName());
        }

    }


    @Test
    public void showCompletedProcesses() {
        showCompletedProcessesTask();
    }

    public void showCompletedProcessesTask() {
        List <HistoricProcessInstance> historicProcessInstanceList = historyService.createHistoricProcessInstanceQuery().list();
        for(int i = 0; i < historicProcessInstanceList.size(); i++) {
            HistoricProcessInstance historicProcessInstance = historicProcessInstanceList.get(i);
            System.out.println("historicProcessInstance.getId():" + historicProcessInstance.getId() + ", " + "historicProcessInstance.getName():" + historicProcessInstance.getName());
        }

    }

    public void showEmailTask(String procID) {
        System.out.println("wiser.getMessages(): " + wiser.getMessages());
    }

    @Test
    public void deleteHistory() {
        deleteHistoryTask("2501");
    }

    public void deleteHistoryTask(String procID) {
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procID).singleResult();
        if (historicProcessInstance != null) {
            historyService.deleteHistoricProcessInstance(historicProcessInstance.getId());
        }
    }


    @Test
    public void sendWelcomeEmail() {
        //completeFinancialNegotiationTask("2501","7514");
        //completeFinancialNegotiationTask("5001","27514");
        sendWelcomeEmailTask("25001","32506");
    }

    public void sendWelcomeEmailTask(String procID, String taskID) {

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID).taskId(taskID)
                .orderByTaskName().asc()
                .list();

        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println("task.getId():" + task.getId() + ", " + "task.getName():" + task.getName() + " completed...");
            taskService.complete(tasks.get(0).getId());
        }
    }

    @Test
    public void sendDeclineEmail() {
        //completeFinancialNegotiationTask("2501","7514");
        //completeFinancialNegotiationTask("5001","27514");
        sendWelcomeEmailTask("15001","17505");
    }

    public void sendDeclineEmailTask(String procID, String taskID) {

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(procID).taskId(taskID)
                .orderByTaskName().asc()
                .list();

        for(int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println("task.getId():" + task.getId() + ", " + "task.getName():" + task.getName() + " completed...");
            taskService.complete(tasks.get(0).getId());
        }
    }

    @Test
    public void checkProcessInstance() {
        //checkProcessInstanceTask("11");
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
