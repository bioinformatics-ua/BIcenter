package serializers.step;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.ComponentMetadata;

import java.io.IOException;

public class ComponentMetadataSerializer extends JsonSerializer<ComponentMetadata> {
    @Override
    public void serialize(ComponentMetadata value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("name", value.getName());

        // Finish output
        gen.writeEndObject();
    }
}
