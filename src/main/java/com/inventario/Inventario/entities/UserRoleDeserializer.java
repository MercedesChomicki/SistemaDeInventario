package com.inventario.Inventario.entities;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserRoleDeserializer extends JsonDeserializer<Set<UserRoleEntity>> {

    @Override
    public Set<UserRoleEntity> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        Set<UserRoleEntity> roles = new HashSet<>();
        // Convertimos a List<String> explícitamente
        List<String> roleNames = p.readValueAs(List.class);

        for (String roleName : roleNames) {
            UserRoleEntity role = new UserRoleEntity();
            role.setName(UserRole.valueOf(roleName.trim().toUpperCase())); // Convertimos el string en enum
            roles.add(role);
        }

        return roles;
    }
}

