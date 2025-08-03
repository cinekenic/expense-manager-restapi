package com.crud.restapi.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validate")
public class ValidationTestController {

    @PostMapping
    public String validate(@RequestBody @Valid SampleRequest request) {
        return "OK";
    }

    public static class SampleRequest {
        @NotBlank(message = "Name is required")
        private String name;

        // getter/setter
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
