package ar.edu.uade.recipes.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AudioStepsResponse {
    @SerializedName("steps")
    private List<String> steps;

    public AudioStepsResponse() {
    }

    public AudioStepsResponse(List<String> steps) {
        this.steps = steps;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }
}

