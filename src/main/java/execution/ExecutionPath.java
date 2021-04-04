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
        if(steps.size() > 0){
            String previousState = "";
            for (ExecutionStep step : steps) {
                String stateName = step.getFrom().getName();
                if (stateName.equals(previousState)) {
                    builder.append("...");
                } else {
                    builder.append(stateName);
                    builder.append("-");
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
    public boolean equals(Object o) {
        ExecutionPath other = (ExecutionPath) o;
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        for (int i = 0; i < Math.min(steps.size(), other.getAllSteps().size()); i++) {
            if(steps.get(i) != other.getAllSteps().get(i)){
                return false;
            }
        }
        return true;

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
