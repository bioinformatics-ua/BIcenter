package serializers.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.*;

import java.io.IOException;

public class ComponentSerializer extends JsonSerializer<Component> {
    @Override
    public void serialize(Component value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        JsonSerializer<Object>  serializer = serializers.findValueSerializer(ComponentProperty.class);
        module.addSerializer(ComponentProperty.class, serializer);

        serializer = serializers.findValueSerializer(StepProperty.class);
        module.addSerializer(StepProperty.class, serializer);

        serializer = serializers.findValueSerializer(ComponentMetadata.class);
        module.addSerializer(ComponentMetadata.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("name", value.getName());
        gen.writeStringField("description", value.getDescription());
        gen.writeStringField("shortName", value.getShortName());

        gen.writeObjectField("componentProperties", mapper.valueToTree(value.getComponentProperties()));

        // Finish output
        gen.writeEndObject();
    }
}
