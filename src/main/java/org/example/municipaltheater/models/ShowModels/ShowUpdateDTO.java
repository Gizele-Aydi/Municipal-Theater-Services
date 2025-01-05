package org.example.municipaltheater.models.ShowModels;

import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "Shows")

public class ShowUpdateDTO {

    @NotBlank(message = "Show name is required.")
    private String ShowName;
    @NotNull(message = "Show date is required.")
    private Date ShowDate;
    private String ShowDescription;

    public String getShowName() {
        return ShowName;
    }

    public void setShowName(String showName) {
        ShowName = showName;
    }

    public Date getShowDate() {
        return ShowDate;
    }

    public void setShowDate(Date showDate) {
        ShowDate = showDate;
    }

    public String getShowDescription() {
        return ShowDescription;
    }

    public void setShowDescription(String showDescription) {
        ShowDescription = showDescription;
    }

}
