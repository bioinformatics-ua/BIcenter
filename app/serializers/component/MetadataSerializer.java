package serializers.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Metadata;

import java.io.IOException;

public class MetadataSerializer extends JsonSerializer<Metadata> {
    @Override
    public void serialize(Metadata value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("name", value.getName());
        gen.writeStringField("value", value.getValue());

        // Finish output
        gen.writeEndObject();
    }
}
