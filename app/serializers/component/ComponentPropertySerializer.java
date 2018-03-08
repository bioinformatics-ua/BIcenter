package serializers.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import models.*;

import java.io.IOException;

public class ComponentPropertySerializer extends JsonSerializer<ComponentProperty> {
    private long stepId;

    public ComponentPropertySerializer() { super(); }
    public ComponentPropertySerializer(long stepId) {
        super();
        this.stepId = stepId;
    }

    @Override
    public void serialize(ComponentProperty value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Setup object mapper
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();


        JsonSerializer<Object>  serializer = serializers.findValueSerializer(StepProperty.class);
        module.addSerializer(StepProperty.class, serializer);

        serializer = serializers.findValueSerializer(ComponentMetadata.class);
        module.addSerializer(ComponentMetadata.class, serializer);

        serializer = serializers.findValueSerializer(Metadata.class);
        module.addSerializer(Metadata.class, serializer);

        mapper.registerModule(module);

        // Start composing output
        gen.writeStartObject();

        gen.writeNumberField("id", value.getId());

        gen.writeStringField("name", value.getName());
        gen.writeStringField("shortName", value.getShortName());
        gen.writeStringField("type", value.getType());
        gen.writeStringField("source", value.getSource());

        gen.writeObjectField("stepProperty", mapper.valueToTree(value.getStepProperty(this.stepId)));
        gen.writeObjectField("componentMetadatas", mapper.valueToTree(value.getComponentMetadatas()));

        // Finish output
        gen.writeEndObject();
    }
}
