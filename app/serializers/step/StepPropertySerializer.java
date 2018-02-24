package serializers.step;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.StepProperty;

import java.io.IOException;

public class StepPropertySerializer extends JsonSerializer<StepProperty> {
    @Override
    public void serialize(StepProperty value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("value", value.getValue());
        gen.writeNumberField("step", value.getStep().getId());

        // Finish output
        gen.writeEndObject();
    }
}
