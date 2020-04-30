package com.dp.plat.activiti.process.behavior;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

public class SequentialMultiInstanceBehavior extends org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior{

	public SequentialMultiInstanceBehavior(ActivityImpl activity, AbstractBpmnActivityBehavior innerActivityBehavior) {
		super(activity, innerActivityBehavior);
	}

	public void createInstances(ActivityExecution execution, int loopCounter) throws Exception {
		int nrOfInstances = resolveNrOfInstances(execution);
	    if (nrOfInstances < 0) {
	      throw new ActivitiIllegalArgumentException("Invalid number of instances: must be a non-negative integer value" 
	              + ", but was " + nrOfInstances);
	    }
	    
	    setLoopVariable(execution, NUMBER_OF_INSTANCES, nrOfInstances);
	    setLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES, loopCounter);
	    setLoopVariable(execution, getCollectionElementIndexVariable(), loopCounter);
	    setLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES, 1);
	    logLoopDetails(execution, "initialized", loopCounter, loopCounter, 1, nrOfInstances);
	    
	    if (nrOfInstances>0) {
	    	executeOriginalBehavior(execution, loopCounter);
	    }
	}

	@Override
	public int resolveNrOfInstances(ActivityExecution execution) {
		return super.resolveNrOfInstances(execution);
	}

}
