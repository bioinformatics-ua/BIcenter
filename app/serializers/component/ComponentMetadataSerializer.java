package serializers.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.ComponentMetadata;
import models.Metadata;
import models.StepProperty;

import java.io.IOException;

public class ComponentMetadataSerializer extends JsonSerializer<ComponentMetadata> {
    @Override
    public void serialize(ComponentMetadata value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object>  serializer = serializers.findValueSerializer(Metadata.class);
        module.addSerializer(Metadata.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("name", value.getName());
        gen.writeStringField("shortName", value.getShortName());
        gen.writeStringField("value", value.getValue());
        gen.writeStringField("source", value.getSource());

        gen.writeObjectField("metadatas", mapper.valueToTree(value.getMetadatas()));

        // Finish output
        gen.writeEndObject();
    }
}
