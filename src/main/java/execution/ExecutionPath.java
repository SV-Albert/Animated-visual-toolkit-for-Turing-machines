package execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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
        int numberOfSteps = steps.size();
        for (int i = 0; i < numberOfSteps-1; i++) {
            ExecutionStep step = steps.get(i);
            if(step.isSplitStep()){
                builder.append(step.getFrom().getName());
                builder.append("...");
            }
        }
        builder.append(steps.get(numberOfSteps-1).getFrom().getName());

        return builder.toString();
    }

    public ExecutionPath copy(){
        ExecutionPath copy = new ExecutionPath();
        copy.addAllSteps(new ArrayList<>(steps));
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExecutionPath that = (ExecutionPath) o;
        return Objects.equals(steps, that.steps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(steps);
    }

    @Override
    public int compareTo(ExecutionPath o) {
        return this.toString().compareTo(o.toString());
    }
}
