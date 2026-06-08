package io.github.fbsarracini.javadesign.project;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddUserToProjectRequest(@NotBlank @Email String email) {
}
