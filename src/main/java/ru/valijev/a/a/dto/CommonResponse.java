package ru.valijev.a.a.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({
        "data",
        "success",
        "status"
})

@NoArgsConstructor
@Data
public class CommonResponse<AnyImage> {

    @JsonProperty("data")
    private AnyImage data;
    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("status")
    private Integer status;
}