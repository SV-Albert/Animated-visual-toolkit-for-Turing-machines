package execution;

import java.util.ArrayList;
import java.util.List;

public class ExecutionPath implements Comparable<ExecutionPath>{
    private List<ExecutionStep> steps;

    public ExecutionPath(){
        steps = new ArrayList<>();
    }

    public void addStep(ExecutionStep step){
        steps.add(step);
    }

    public void addAllSteps(List<ExecutionStep> steps){
        this.steps = steps;
    }

    public List<ExecutionStep> getAllSteps(){
        return steps;
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        if(steps.size() > 0){
            String previousState = "";
            int counter = 0;
            for (ExecutionStep step : steps) {
                String stateName = step.getFrom().getName();
                if (stateName.equals(previousState)){
                    counter++;
                }
                else{
                    if(counter > 0){
                        builder.append("(x").append(counter).append(") ");
                        counter = 0;
                    }
                    builder.append(stateName).append(" ");
                }
                previousState = stateName;
            }
            builder.append(steps.get(steps.size() - 1).getTo().getName());
            return builder.toString();
        }
        else{
            return "";
        }
    }

    public ExecutionPath getCopy(){
        ExecutionPath copy = new ExecutionPath();
        copy.addAllSteps(new ArrayList<>(steps));
        return copy;
    }

    @Override
    public int compareTo(ExecutionPath o) {
        return this.toString().compareTo(o.toString());
    }
}
