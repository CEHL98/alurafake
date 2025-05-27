package br.com.alura.AluraFake.dto.request.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OptionDTO {

    @NotNull
    @Size(min = 4, max = 80)
    private String option;

    @NotNull
    private Boolean isCorrect;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean correct) {
        isCorrect = correct;
    }
}
