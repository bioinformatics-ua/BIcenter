package serializers.hop;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Step;

import java.io.IOException;

public class StepSerializer extends JsonSerializer<Step> {
    @Override
    public void serialize(Step value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("label", value.getLabel());

        // Finish output
        gen.writeEndObject();
    }
}
