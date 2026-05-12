package com.kalitron.studio.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class ChatSessionStartRequestDTO implements Serializable {

    @NotBlank
    @Size(max = 120)
    private String clientName;

    @NotBlank
    @Email
    @Size(max = 120)
    private String clientEmail;

    @Size(max = 80)
    private String selectedStyle;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getSelectedStyle() {
        return selectedStyle;
    }

    public void setSelectedStyle(String selectedStyle) {
        this.selectedStyle = selectedStyle;
    }
}
