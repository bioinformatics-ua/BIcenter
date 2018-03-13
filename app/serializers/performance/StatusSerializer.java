package serializers.performance;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Status;

import java.io.IOException;

public class StatusSerializer extends JsonSerializer<Status> {
    @Override
    public void serialize(Status value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("stepName", value.getStep().getLabel());
        gen.writeNumberField("status", value.getStatus());
        gen.writeStringField("logText", value.getLogText());

        // Finish output
        gen.writeEndObject();
    }
}
