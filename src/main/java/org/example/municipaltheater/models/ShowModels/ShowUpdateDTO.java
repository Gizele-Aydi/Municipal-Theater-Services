package org.example.municipaltheater.models.ShowModels;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String showName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @NotNull(message = "Show date is required.")
    private Date showDate;
    private String showDescription;

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public String getShowDescription() {
        return showDescription;
    }

    public void setShowDescription(String showDescription) {
        this.showDescription = showDescription;
    }
}
