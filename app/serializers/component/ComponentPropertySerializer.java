package serializers.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.*;

import java.io.IOException;

public class ComponentPropertySerializer extends JsonSerializer<ComponentProperty> {
    @Override
    public void serialize(ComponentProperty value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();


        JsonSerializer<Object>  serializer = serializers.findValueSerializer(StepProperty.class);
        module.addSerializer(StepProperty.class, serializer);

        serializer = serializers.findValueSerializer(ComponentMetadata.class);
        module.addSerializer(ComponentMetadata.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("name", value.getName());
        gen.writeStringField("shortName", value.getShortName());
        gen.writeStringField("type", value.getType());

        gen.writeObjectField("stepProperty", mapper.valueToTree(value.getStepProperty()));
        gen.writeObjectField("componentMetadatas", mapper.valueToTree(value.getComponentMetadatas()));

        // Finish output
        gen.writeEndObject();
    }
}
