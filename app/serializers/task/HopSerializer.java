package serializers.task;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Hop;

import java.io.IOException;

public class HopSerializer extends JsonSerializer<Hop> {
    @Override
    public void serialize(Hop value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        // Finish output
        gen.writeEndObject();
    }
}
