package serializers.user;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.rbac.User;

import java.io.IOException;

public class UserNoChildSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(User user, JsonGenerator jgen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        jgen.writeStartObject();

        jgen.writeNumberField("id", user.getId());

        jgen.writeStringField("name", user.getName());
        jgen.writeStringField("email", user.getEmail());

        jgen.writeEndObject();
    }
}
