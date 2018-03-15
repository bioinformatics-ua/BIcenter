package serializers.institution;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Server;

import java.io.IOException;

public class ServerSerializer extends JsonSerializer<Server> {
    @Override
    public void serialize(Server value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());
        gen.writeStringField("name", value.getName());
        gen.writeStringField("hostName", value.getHostName());
        gen.writeNumberField("portNumber", value.getPortNumber());

        // Finish output
        gen.writeEndObject();
    }
}
